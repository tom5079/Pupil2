package xyz.quaver.pupil.source.hitomi.networking

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.nio.ByteBuffer

const val protocol = "https:"
const val domain = "ltn.hitomi.la"
const val nozomiExtension = ".nozomi"

class HitomiHttpClient {
    private val httpClient = HttpClient(OkHttp) {

    }

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    suspend fun fetchNozomi(
        area: String? = null,
        tag: String = "index",
        language: String = "all",
        popular: String? = null,
        range: IntRange
    ): Result<List<Int>> = runCatching {
        val nozomiAddress = when {
            area == null -> "$protocol//$domain/$tag-$language$nozomiExtension"
            popular != null && area != "popular" -> "$protocol//$domain/$area/popular/$popular/$tag-$language$nozomiExtension"
            else -> "$protocol//$domain/$area/$tag-$language$nozomiExtension"
        }

        val byteRange = IntRange(range.first * 4, range.last * 4 + 3)

        withContext(Dispatchers.IO) {
            val response = httpClient.get(nozomiAddress) {
                header("Range", "bytes=${byteRange.first}-${byteRange.last}")
            }

            if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.PartialContent) {
                error("Error while fetching $nozomiAddress (byte ${range.first} - byte ${range.last}), HTTP Error ${response.status}")
            }

            buildList {
                val channel = response.bodyAsChannel()

                while (!channel.isClosedForRead) {
                    add(channel.readInt())
                }
            }
        }
    }

    suspend fun GalleryInfo.thumbnail(): Result<String?> = runCatching {
        val thumbnail = files.firstOrNull() ?: return@runCatching null

        withContext(Dispatchers.IO) {
            httpClient.urlFromUrlFromHash(thumbnail, "webpbigtn", "webp", "tn")
        }
    }

    suspend fun getGalleryInfo(galleryID: Int): Result<GalleryInfo> = runCatching {
        withContext(Dispatchers.IO) {
            val response = httpClient.get("$protocol//$domain/galleries/$galleryID.js").bodyAsText()
                .replace("var galleryinfo = ", "")
            json.decodeFromString(response)
        }
    }

    private suspend fun getURLAtRange(url: String, range: IntRange): Result<ByteBuffer> = runCatching {
        withContext(Dispatchers.IO) {
            val response = httpClient.get(url) {
                header("Range", "bytes=${range.first}-${range.last}")
            }

            if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.PartialContent) {
                error("Error while fetching $url (byte ${range.first} - byte ${range.last}), HTTP Error ${response.status}")
            }

            val bufferSize = range.last - range.first + 1
            ByteBuffer.allocateDirect(bufferSize).apply {
                val channel = response.bodyAsChannel()

                val bytesRead = channel.readFully(this)

                assert(bytesRead == this.capacity())
                assert(!this.hasRemaining())
                assert(channel.availableForRead == 0)

                rewind()
            }
        }
    }
}