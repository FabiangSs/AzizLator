package com.azizlator.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azizlator.R
import com.azizlator.engine.DeviceOptimizer
import com.azizlator.engine.ContainerManager
import com.azizlator.utils.ContainerAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var deviceOptimizer: DeviceOptimizer
    private lateinit var containerManager: ContainerManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvDeviceInfo: TextView
    private lateinit var tvPerformanceMode: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init components
        deviceOptimizer = DeviceOptimizer(this)
        containerManager = ContainerManager(this)

        // Views
        recyclerView = findViewById(R.id.rv_containers)
        tvDeviceInfo = findViewById(R.id.tv_device_info)
        tvPerformanceMode = findViewById(R.id.tv_performance_mode)
        val btnNewContainer = findViewById<Button>(R.id.btn_new_container)
        val btnSettings = findViewById<ImageButton>(R.id.btn_settings)

        // Setup RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Show device info
        val deviceProfile = deviceOptimizer.detectDevice()
        tvDeviceInfo.text = "📱 ${deviceProfile.chipset} | ${deviceProfile.ramGB}GB RAM"
        tvPerformanceMode.text = "⚡ Mode: ${deviceProfile.performanceMode}"

        // Load containers
        loadContainers()

        // Buttons
        btnNewContainer.setOnClickListener {
            startActivity(Intent(this, ContainerActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun loadContainers() {
        val containers = containerManager.getContainers()
        val adapter = ContainerAdapter(containers) { container ->
            val intent = Intent(this, ContainerActivity::class.java)
            intent.putExtra("container_id", container.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadContainers()
    }
}
