package source

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.kodein.di.DI
import org.kodein.di.bindProvider
import xyz.quaver.pupil.common.source.Source
import xyz.quaver.pupil.common.source.SourceEntry
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile

class DesktopSourceEntry(
    override val name: String,
    override val version: String,
    override val source: Source
) : SourceEntry {
    @Composable
    override fun Icon() {
        val image = remember {
            val icon = source::class.java.getResourceAsStream("/drawable/icon.webp")?.readBytes()
                ?: this::class.java.getResourceAsStream("/drawable/icon.webp")!!.readBytes()

            org.jetbrains.skia.Image.makeFromEncoded(icon).toComposeImageBitmap()
        }

        Image(image, "$name icon")
    }
}

private val applicationDirectory = DesktopSourceEntry::class.java.protectionDomain.codeSource.location.path

fun resolveSourceEntry(path: String): DesktopSourceEntry? = runCatching {
    val jar = JarFile(path)

    val manifest = jar.manifest.mainAttributes

    val name = manifest.getValue("Source-Name") ?: return null
    val version = manifest.getValue("Source-Version") ?: return null

    val classLoader = URLClassLoader.newInstance(arrayOf(URL("jar:file:$path!/")))
    val entries = jar.entries()

    val source = run {
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()

            if (entry.isDirectory || !entry.name.endsWith(".class")) {
                continue
            }

            val className = entry.name.let { it.substring(0, it.length - 6) }.replace('/', '.')
            val klass = runCatching {
                classLoader.loadClass(className).getDeclaredConstructor().newInstance()
            }.getOrNull() as? Source

            if (klass != null) {
                return@run klass
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
    val pathsToSearch = listOf(
        File(System.getProperty("user.home"), ".pupil"),
        File(applicationDirectory)
    )

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