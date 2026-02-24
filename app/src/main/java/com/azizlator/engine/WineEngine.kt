package com.azizlator.engine

import android.content.Context
import android.net.Uri
import java.io.File

class WineEngine(
    private val context: Context,
    private val deviceProfile: DeviceProfile
) {

    private var wineProcess: Process? = null
    private val wineDir = File(context.filesDir, "wine")
    private val containerDir = File(context.filesDir, "containers")

    init {
        wineDir.mkdirs()
        containerDir.mkdirs()
    }

    fun startContainer(containerId: String, callback: (Boolean) -> Unit) {
        Thread {
            try {
                val container = File(containerDir, containerId)
                container.mkdirs()

                val envVars = DeviceOptimizer(context).getOptimizedEnvVars(deviceProfile)
                val command = buildWineCommand(container, envVars)

                val processBuilder = ProcessBuilder(command)
                processBuilder.environment().putAll(envVars)
                processBuilder.directory(container)

                wineProcess = processBuilder.start()
                callback(true)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(false)
            }
        }.start()
    }

    fun stopContainer() {
        wineProcess?.destroy()
        wineProcess = null
    }

    fun installExe(uri: Uri?, callback: (Boolean) -> Unit) {
        if (uri == null) {
            callback(false)
            return
        }
        Thread {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val exeFile = File(context.cacheDir, "install.exe")
                exeFile.outputStream().use { out ->
                    inputStream?.copyTo(out)
                }
                // Wine will install it
                val envVars = DeviceOptimizer(context).getOptimizedEnvVars(deviceProfile)
                val cmd = listOf(getWinePath(), exeFile.absolutePath)
                val pb = ProcessBuilder(cmd)
                pb.environment().putAll(envVars)
                val p = pb.start()
                p.waitFor()
                callback(p.exitValue() == 0)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(false)
            }
        }.start()
    }

    private fun buildWineCommand(container: File, env: Map<String, String>): List<String> {
        return when (deviceProfile.performanceMode) {
            "ULTRA", "HIGH" -> listOf(
                getWinePath(),
                "explorer",
                "/desktop=shell,${deviceProfile.recommendedResolution}"
            )
            "BALANCED" -> listOf(
                getWinePath(),
                "explorer",
                "/desktop=shell,1024x768"
            )
            "COMPATIBILITY", "LOW_END" -> listOf(
                getWinePath(),
                "explorer",
                "/desktop=shell,800x600"
            )
            else -> listOf(getWinePath(), "explorer")
        }
    }

    private fun getWinePath(): String {
        // Path to Wine binary (installed via Box64/Box86)
        val localWine = File(context.filesDir, "bin/wine")
        return if (localWine.exists()) localWine.absolutePath else "wine"
    }

    fun isRunning(): Boolean = wineProcess?.isAlive ?: false
}
