package com.azizlator.engine

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import java.io.File

data class DeviceProfile(
    val chipset: String,
    val gpu: String,
    val ramGB: Int,
    val performanceMode: String,
    val recommendation: String,
    val dxvkEnabled: Boolean,
    val turnipEnabled: Boolean,
    val recommendedResolution: String,
    val cpuCores: Int,
    val isLowEnd: Boolean
)

class DeviceOptimizer(private val context: Context) {

    fun detectDevice(): DeviceProfile {
        val chipset = getChipset()
        val gpu = getGPU()
        val ramGB = getRAMInGB()
        val cpuCores = Runtime.getRuntime().availableProcessors()

        return when {
            // Snapdragon 8 Gen Series - Ultra Performance
            chipset.contains("SM8550") || chipset.contains("SM8650") ||
            chipset.contains("8 Gen 2") || chipset.contains("8 Gen 3") -> DeviceProfile(
                chipset = chipset, gpu = "Adreno 740/750",
                ramGB = ramGB, cpuCores = cpuCores,
                performanceMode = "ULTRA",
                recommendation = "Max settings - All games supported",
                dxvkEnabled = true, turnipEnabled = true,
                recommendedResolution = "1920x1080",
                isLowEnd = false
            )

            // Snapdragon 8 Gen 1 / 888
            chipset.contains("SM8450") || chipset.contains("SM8350") ||
            chipset.contains("8 Gen 1") || chipset.contains("888") -> DeviceProfile(
                chipset = chipset, gpu = "Adreno 730/660",
                ramGB = ramGB, cpuCores = cpuCores,
                performanceMode = "HIGH",
                recommendation = "High settings - Most games supported",
                dxvkEnabled = true, turnipEnabled = true,
                recommendedResolution = "1280x720",
                isLowEnd = false
            )

            // Snapdragon 7 Series
            chipset.contains("SM7") || chipset.contains("778") ||
            chipset.contains("870") || chipset.contains("7 Gen") -> DeviceProfile(
                chipset = chipset, gpu = "Adreno 642/644",
                ramGB = ramGB, cpuCores = cpuCores,
                performanceMode = "BALANCED",
                recommendation = "Medium settings - SA:MP works great",
                dxvkEnabled = true, turnipEnabled = false,
                recommendedResolution = "1024x768",
                isLowEnd = false
            )

            // Dimensity 9000+
            chipset.contains("MT6989") || chipset.contains("MT6985") ||
            chipset.contains("9200") || chipset.contains("9300") -> DeviceProfile(
                chipset = chipset, gpu = "Mali-G715",
                ramGB = ramGB, cpuCores = cpuCores,
                performanceMode = "HIGH",
                recommendation = "High settings supported",
                dxvkEnabled = true, turnipEnabled = false,
                recommendedResolution = "1280x720",
                isLowEnd = false
            )

            // Dimensity 1000-8000
            chipset.contains("MT68") -> DeviceProfile(
                chipset = chipset, gpu = "Mali-G77/G57",
                ramGB = ramGB, cpuCores = cpuCores,
                performanceMode = "BALANCED",
                recommendation = "Medium settings",
                dxvkEnabled = true, turnipEnabled = false,
                recommendedResolution = "1024x768",
                isLowEnd = false
            )

            // MediaTek Helio G88 / G90 / G96 (هاتفك!)
            chipset.contains("MT6769") || chipset.contains("Helio G88") ||
            chipset.contains("G88") || chipset.contains("G90") ||
            chipset.contains("G96") || chipset.contains("G99") -> DeviceProfile(
                chipset = "MediaTek Helio G88",
                gpu = "Mali-G52 MC2",
                ramGB = ramGB, cpuCores = cpuCores,
                performanceMode = "COMPATIBILITY",
                recommendation = "Optimized mode - SA:MP & light apps",
                dxvkEnabled = false, turnipEnabled = false,
                recommendedResolution = "800x600",
                isLowEnd = true
            )

            // Helio G85 / G80 / lower
            chipset.contains("Helio G") || chipset.contains("MT6762") -> DeviceProfile(
                chipset = chipset, gpu = "Mali-G52",
                ramGB = ramGB, cpuCores = cpuCores,
                performanceMode = "LOW_END",
                recommendation = "Light apps only",
                dxvkEnabled = false, turnipEnabled = false,
                recommendedResolution = "800x600",
                isLowEnd = true
            )

            // Unknown / Default
            else -> DeviceProfile(
                chipset = chipset.ifEmpty { "Unknown" },
                gpu = gpu.ifEmpty { "Unknown" },
                ramGB = ramGB, cpuCores = cpuCores,
                performanceMode = "AUTO",
                recommendation = "Auto-optimized settings",
                dxvkEnabled = ramGB >= 6,
                turnipEnabled = false,
                recommendedResolution = if (ramGB >= 6) "1024x768" else "800x600",
                isLowEnd = ramGB < 4
            )
        }
    }

    private fun getChipset(): String {
        return try {
            val proc = File("/proc/cpuinfo").readText()
            val hardware = proc.lines().find { it.startsWith("Hardware") }
            hardware?.substringAfter(":")?.trim() ?: Build.HARDWARE
        } catch (e: Exception) {
            Build.HARDWARE
        }
    }

    private fun getGPU(): String {
        return try {
            // Try to read from system properties
            val process = Runtime.getRuntime().exec("getprop ro.product.manufacturer")
            Build.HARDWARE
        } catch (e: Exception) {
            "Unknown GPU"
        }
    }

    private fun getRAMInGB(): Int {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        return (info.totalMem / (1024 * 1024 * 1024)).toInt()
    }

    fun getOptimizedEnvVars(profile: DeviceProfile): Map<String, String> {
        val vars = mutableMapOf<String, String>()

        vars["WINEPREFIX"] = "/data/data/com.azizlator/wine"
        vars["DISPLAY"] = ":0"

        when (profile.performanceMode) {
            "ULTRA", "HIGH" -> {
                vars["DXVK_HUD"] = "0"
                vars["DXVK_ASYNC"] = "1"
                vars["MESA_VK_WSI_PRESENT_MODE"] = "mailbox"
                vars["vblank_mode"] = "0"
                vars["WINE_CPU_TOPOLOGY"] = "${profile.cpuCores}:0x${(1 shl profile.cpuCores) - 1}"
            }
            "BALANCED" -> {
                vars["DXVK_ASYNC"] = "1"
                vars["vblank_mode"] = "0"
                vars["WINE_CPU_TOPOLOGY"] = "4:0xf"
            }
            "COMPATIBILITY", "LOW_END" -> {
                vars["LIBGL_ALWAYS_SOFTWARE"] = "0"
                vars["GALLIUM_DRIVER"] = "virpipe"
                vars["WINE_CPU_TOPOLOGY"] = "2:0x3"
                vars["MESA_GL_VERSION_OVERRIDE"] = "3.3"
            }
        }

        if (profile.turnipEnabled) {
            vars["TU_DEBUG"] = "noconform"
            vars["MESA_VK_DEVICE_SELECT"] = "0"
        }

        return vars
    }
}
