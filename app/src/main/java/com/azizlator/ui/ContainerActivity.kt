package com.azizlator.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.azizlator.R
import com.azizlator.engine.ContainerManager
import com.azizlator.engine.DeviceOptimizer
import com.azizlator.engine.WineEngine

class ContainerActivity : AppCompatActivity() {

    private lateinit var containerManager: ContainerManager
    private lateinit var wineEngine: WineEngine
    private lateinit var deviceOptimizer: DeviceOptimizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        containerManager = ContainerManager(this)
        deviceOptimizer = DeviceOptimizer(this)
        wineEngine = WineEngine(this, deviceOptimizer.detectDevice())

        val containerId = intent.getStringExtra("container_id")
        val tvStatus = findViewById<TextView>(R.id.tv_container_status)
        val btnStart = findViewById<Button>(R.id.btn_start_container)
        val btnStop = findViewById<Button>(R.id.btn_stop_container)
        val btnInstall = findViewById<Button>(R.id.btn_install_app)
        val etContainerName = findViewById<EditText>(R.id.et_container_name)
        val spinnerWineVersion = findViewById<Spinner>(R.id.spinner_wine_version)
        val spinnerDxvk = findViewById<Spinner>(R.id.spinner_dxvk)

        // Wine versions
        val wineVersions = arrayOf("Wine 8.0", "Wine 7.0", "Wine 6.0 (Stable)")
        spinnerWineVersion.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, wineVersions)

        // DXVK versions
        val dxvkVersions = arrayOf("DXVK 2.3", "DXVK 2.2", "VirGL (Low-End)", "Disabled")
        spinnerDxvk.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dxvkVersions)

        if (containerId != null) {
            val container = containerManager.getContainer(containerId)
            etContainerName.setText(container?.name ?: "Container")
            tvStatus.text = "Status: ${container?.status ?: "Ready"}"
        }

        btnStart.setOnClickListener {
            tvStatus.text = "Status: 🟢 Starting..."
            wineEngine.startContainer(containerId ?: "default") { success ->
                runOnUiThread {
                    tvStatus.text = if (success) "Status: 🟢 Running" else "Status: 🔴 Error"
                }
            }
        }

        btnStop.setOnClickListener {
            wineEngine.stopContainer()
            tvStatus.text = "Status: ⚫ Stopped"
        }

        btnInstall.setOnClickListener {
            // Open file picker for .exe files
            val intent = android.content.Intent(android.content.Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            val tvStatus = findViewById<TextView>(R.id.tv_container_status)
            tvStatus.text = "Status: 📦 Installing ${uri?.lastPathSegment}..."
            wineEngine.installExe(uri) { success ->
                runOnUiThread {
                    tvStatus.text = if (success) "Status: ✅ Installed!" else "Status: ❌ Install Failed"
                }
            }
        }
    }
}
