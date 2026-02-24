package com.azizlator.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.azizlator.R
import com.azizlator.engine.DeviceOptimizer
import com.azizlator.utils.PrefsManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: PrefsManager
    private lateinit var deviceOptimizer: DeviceOptimizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = PrefsManager(this)
        deviceOptimizer = DeviceOptimizer(this)

        val switchAutoOptimize = findViewById<Switch>(R.id.switch_auto_optimize)
        val switchForceTurnip = findViewById<Switch>(R.id.switch_force_turnip)
        val switchDXVK = findViewById<Switch>(R.id.switch_dxvk)
        val spinnerResolution = findViewById<Spinner>(R.id.spinner_resolution)
        val spinnerCpuCores = findViewById<Spinner>(R.id.spinner_cpu_cores)
        val tvDeviceProfile = findViewById<TextView>(R.id.tv_device_profile)
        val btnAutoDetect = findViewById<Button>(R.id.btn_auto_detect)
        val btnSave = findViewById<Button>(R.id.btn_save_settings)

        // Resolution options
        val resolutions = arrayOf("800x600", "1024x768", "1280x720", "1920x1080", "Auto")
        spinnerResolution.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resolutions)

        // CPU Cores
        val cores = arrayOf("1", "2", "4", "6", "Auto")
        spinnerCpuCores.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cores)

        // Load saved settings
        switchAutoOptimize.isChecked = prefs.getBoolean("auto_optimize", true)
        switchForceTurnip.isChecked = prefs.getBoolean("force_turnip", false)
        switchDXVK.isChecked = prefs.getBoolean("dxvk_enabled", true)

        // Device profile
        val profile = deviceOptimizer.detectDevice()
        tvDeviceProfile.text = """
            🖥️ Chipset: ${profile.chipset}
            🎮 GPU: ${profile.gpu}
            💾 RAM: ${profile.ramGB}GB
            ⚡ Mode: ${profile.performanceMode}
            🎯 Recommended: ${profile.recommendation}
        """.trimIndent()

        btnAutoDetect.setOnClickListener {
            val autoProfile = deviceOptimizer.detectDevice()
            switchAutoOptimize.isChecked = true
            switchDXVK.isChecked = autoProfile.dxvkEnabled
            switchForceTurnip.isChecked = autoProfile.turnipEnabled
            val resIndex = resolutions.indexOf(autoProfile.recommendedResolution)
            if (resIndex >= 0) spinnerResolution.setSelection(resIndex)
            Toast.makeText(this, "✅ Auto-detected for ${autoProfile.chipset}", Toast.LENGTH_SHORT).show()
        }

        btnSave.setOnClickListener {
            prefs.setBoolean("auto_optimize", switchAutoOptimize.isChecked)
            prefs.setBoolean("force_turnip", switchForceTurnip.isChecked)
            prefs.setBoolean("dxvk_enabled", switchDXVK.isChecked)
            prefs.setString("resolution", spinnerResolution.selectedItem.toString())
            prefs.setString("cpu_cores", spinnerCpuCores.selectedItem.toString())
            Toast.makeText(this, "✅ Settings Saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
