import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import xyz.quaver.pupil.source.hitomi.networking.HitomiHttpClient
import kotlin.test.Test
import kotlin.test.assertEquals

class HitomiHttpClientTest {

    val client = HitomiHttpClient()

    @Test
    fun testFetchNozomi() {
        val result = runBlocking {
            client.fetchNozomi(range = 0..9).getOrThrow()
        }

        assertEquals(10, result.size)
    }

    @Test
    fun testGetGalleryInfo() {
        val testCase = listOf(
            2599492 to "楽描き ボンバーガール パイン",
            2599488 to "Shikikan Wa Itazura Ga Shitakute Shouganai!!",
            2599481 to "JC Kyonyuu Miria-Chan Junyuu & Junyuu Tekoki Hen"
        )

        runBlocking {
            val actual = testCase.map { (id, title) ->
                async {
                    client.getGalleryInfo(id) to title
                }
            }

            actual.forEach {
                val (result, expectedTitle) = it.await()

                val galleryInfo = result.getOrThrow()

                assertEquals(expectedTitle.lowercase(), galleryInfo.title.lowercase())
            }
        }

    }
}