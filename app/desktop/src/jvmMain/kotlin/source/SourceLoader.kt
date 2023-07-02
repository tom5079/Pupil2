package source

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.arkivanov.essenty.parcelable.Parcelize
import org.kodein.di.DI
import org.kodein.di.bindProvider
import xyz.quaver.pupil.core.source.Source
import xyz.quaver.pupil.core.source.SourceEntry
import xyz.quaver.pupil.core.source.SourceLoader
import java.io.File
import java.io.InputStream
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile

@Parcelize
class DesktopSourceLoader(
    private val url: String,
    private val className: String
) : SourceLoader {

    fun icon(): InputStream? {
        val classLoader = URLClassLoader.newInstance(arrayOf(URL(url)))
        return classLoader.loadClass(className).getResourceAsStream("/drawable/icon.webp")
    }

    override fun loadSource(di: DI): Source? = runCatching {
        val classLoader = URLClassLoader.newInstance(arrayOf(URL(url)))
        classLoader.loadClass(className).getDeclaredConstructor().newInstance() as Source
    }.getOrNull()
}

class DesktopSourceEntry(
    override val name: String,
    override val version: String,
    override val sourceLoader: DesktopSourceLoader
) : SourceEntry {
    @Composable
    override fun Icon(modifier: Modifier) {
        val image = remember {
            val icon = sourceLoader.icon()?.readBytes()
                ?: this::class.java.getResourceAsStream("/drawable/pupil_icon.webp")!!.readBytes()

            org.jetbrains.skia.Image.makeFromEncoded(icon).toComposeImageBitmap()
        }

        Image(image, "$name icon", modifier = modifier)
    }
}

private val applicationDirectory = DesktopSourceEntry::class.java.protectionDomain?.codeSource?.location?.path

fun resolveSourceEntry(path: String): DesktopSourceEntry? = runCatching {
    val jar = JarFile(path)

    val manifest = jar.manifest.mainAttributes

    val name = manifest.getValue("Source-Name") ?: return null
    val version = manifest.getValue("Source-Version") ?: return null

    val url = "jar:file:$path!/"
    val classLoader = URLClassLoader.newInstance(arrayOf(URL(url)))
    val entries = jar.entries()

    val source = run {
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()

            if (entry.isDirectory || !entry.name.endsWith(".class")) {
                continue
            }

            val className = entry.name.let { it.substring(0, it.length - 6) }.replace('/', '.')
            val klass = runCatching {
                classLoader.loadClass(className).getDeclaredConstructor()
            }.getOrNull()

            if (klass != null) {
                return@run DesktopSourceLoader(url, className)
            }
        }

        return null
    }

    return DesktopSourceEntry(
        name,
        version,
        source
    )
}.getOrNull()

fun discoverSources(): List<DesktopSourceEntry> {
    val pathsToSearch = buildList {
        add(File(System.getProperty("user.home"), ".pupil"))
        applicationDirectory?.let { add(File(it)) }
    }

    return buildList {
        pathsToSearch.forEach { directory ->
            if (!directory.exists() || !directory.isDirectory) return@forEach

            val jarFiles = directory.listFiles { _, fileName ->
                fileName.lowercase().endsWith(".jar")
            }

            jarFiles?.forEach { file ->
                resolveSourceEntry(file.path)?.let { add(it) }
            }
        }
    }
}

val sourceModule = DI.Module("sourceModule") {
    bindProvider<List<SourceEntry>> { discoverSources() }
}