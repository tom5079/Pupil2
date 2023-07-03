package xyz.quaver.pupil.source.hitomi.networking

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

object gg {
    private var lastRetrieval: Instant? = null

    private val mutex = Mutex()

    private var mDefault = 0
    private val mMap = mutableMapOf<Int, Int>()

    private var b = ""

    private suspend fun refresh(client: HttpClient) = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (lastRetrieval == null || (lastRetrieval!! + 1.minutes) < now()) {
                val ggjs = client.get("https://ltn.hitomi.la/gg.js").bodyAsText()

                mDefault = Regex("var o = (\\d)").find(ggjs)!!.groupValues[1].toInt()
                val o = Regex("o = (\\d); break;").find(ggjs)!!.groupValues[1].toInt()

                mMap.clear()
                Regex("case (\\d+):").findAll(ggjs).forEach {
                    val case = it.groupValues[1].toInt()
                    mMap[case] = o
                }

                b = Regex("b: '(.+)'").find(ggjs)!!.groupValues[1]

                lastRetrieval = now()
            }
        }
    }

    suspend fun m(client: HttpClient, g: Int): Int {
        refresh(client)

        return mMap[g] ?: mDefault
    }

    suspend fun b(client: HttpClient): String {
        refresh(client)
        return b
    }

    fun s(h: String): String {
        val m = Regex("(..)(.)$").find(h)
        return m!!.groupValues.let { it[2] + it[1] }.toInt(16).toString(10)
    }
}

suspend fun HttpClient.subdomainFromURL(url: String, base: String? = null): String {
    var retval = "b"

    if (!base.isNullOrBlank())
        retval = base

    val b = 16

    val r = Regex("""/[0-9a-f]{61}([0-9a-f]{2})([0-9a-f])""")
    val m = r.find(url) ?: return "a"

    val g = m.groupValues.let { it[2] + it[1] }.toIntOrNull(b)

    if (g != null) {
        retval = (97 + gg.m(this, g)).toChar().toString() + retval
    }

    return retval
}

suspend fun HttpClient.urlFromUrl(url: String, base: String? = null): String {
    return url.replace(Regex("""//..?\.hitomi\.la/"""), "//${subdomainFromURL(url, base)}.hitomi.la/")
}

suspend fun HttpClient.fullPathFromHash(hash: String): String =
    "${gg.b(this)}${gg.s(hash)}/$hash"

fun realFullPathFromHash(hash: String): String =
    hash.replace(Regex("""^.*(..)(.)$"""), "$2/$1/$hash")

suspend fun HttpClient.urlFromHash(image: GalleryFiles, dir: String? = null, ext: String? = null): String {
    val ext = ext ?: dir ?: image.name.takeLastWhile { it != '.' }
    val dir = dir ?: "images"
    return "https://a.hitomi.la/$dir/${fullPathFromHash(image.hash)}.$ext"
}

suspend fun HttpClient.urlFromUrlFromHash(
    image: GalleryFiles,
    dir: String? = null,
    ext: String? = null,
    base: String? = null
) =
    if (base == "tn")
        urlFromUrl("https://a.hitomi.la/$dir/${realFullPathFromHash(image.hash)}.$ext", base)
    else
        urlFromUrl(urlFromHash(image, dir, ext), base)

suspend fun HttpClient.rewriteTnPaths(html: String): String {
    val match = Regex("""//tn\.hitomi\.la/[^/]+/[0-9a-f]/[0-9a-f]{2}/[0-9a-f]{64}""").find(html) ?: return html

    val replacement = urlFromUrl(match.value, "tn")

    return html.replaceRange(match.range, replacement)
}

suspend fun HttpClient.imageUrlFromImage(image: GalleryFiles): List<String> {
    val imageList = mutableListOf<String>()

    if (image.hasAVIF != 0)
        imageList.add(urlFromUrlFromHash(image, "avif"))

    imageList.add(urlFromUrlFromHash(image, "webp", null, "a"))

    return imageList.toList()
}