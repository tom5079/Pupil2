package xyz.quaver.pupil.android.source

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelize
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dalvik.system.PathClassLoader
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance
import xyz.quaver.pupil.common.source.Source
import xyz.quaver.pupil.common.source.SourceEntry
import xyz.quaver.pupil.common.source.SourceLoader

@Parcelize
class AndroidSourceLoader(
    private val sourceDir: String, private val classPath: String
) : SourceLoader {
    override fun loadSource(di: DI): Source? = runCatching {
        val context by di.instance<Application>()

        val classLoader = PathClassLoader(sourceDir, null, context.classLoader)

        return Class.forName(classPath, false, classLoader)
            .getConstructor()
            .newInstance() as Source
    }.getOrNull()
}

class AndroidSourceEntry(
    override val name: String,
    override val version: String,
    override val sourceLoader: SourceLoader,
    val icon: Drawable
) : SourceEntry {
    @Composable
    override fun Icon(modifier: Modifier) {
        Image(rememberDrawablePainter(icon), "${name} icon", modifier = modifier)
    }
}

private const val SOURCES_FEATURE = "pupil.source"
private const val SOURCES_PACKAGE_PREFIX = "xyz.quaver.pupil.source"
private const val SOURCES_PATH = "pupil.source.path"

private val PackageInfo.isSourceFeatureEnabled
    get() = this.reqFeatures.orEmpty().any { it.name == SOURCES_FEATURE }

private fun resolveSourceEntry(context: Context, packageInfo: PackageInfo): List<AndroidSourceEntry> {
    val packageManager = context.packageManager

    val applicationInfo = packageInfo.applicationInfo

    val packageName = packageManager.getApplicationLabel(applicationInfo).toString().substringAfter("[Pupil] ")
    val packagePath = packageInfo.packageName

    val icon = packageManager.getApplicationIcon(applicationInfo)

    val version = packageInfo.versionName

    return packageInfo
        .applicationInfo
        .metaData
        ?.getString(SOURCES_PATH)
        ?.split(';')
        ?.mapNotNull { sourcesPath ->
            runCatching {
                val (sourceName, sourcePath) = sourcesPath.split(':', limit = 2)

                val sourceDir = applicationInfo.sourceDir
                val classPath = "${packagePath}${sourcePath}"

                val classLoader = PathClassLoader(sourceDir, null, context.classLoader)

                Class.forName(classPath, false, classLoader).getConstructor()

                AndroidSourceEntry(
                    if (sourceName == packageName) sourceName else "$sourceName ($packageName)",
                    version,
                    AndroidSourceLoader(sourceDir, classPath),
                    icon
                )
            }.getOrNull()
        }.orEmpty()
}

private fun discoverSources(context: Context): List<AndroidSourceEntry> {
    val packageManager = context.packageManager

    val packages = packageManager.getInstalledPackages(
        PackageManager.GET_CONFIGURATIONS or PackageManager.GET_META_DATA
    )

    val test = packages.filter { it.packageName.contains("manatoki") }

    return packages.filter { it.isSourceFeatureEnabled }.flatMap { packageInfo ->
        resolveSourceEntry(context, packageInfo)
    }
}

fun sourceModule(context: Context) = DI.Module("sourceModule") {
    bindProvider<List<SourceEntry>> { discoverSources(context) }
}