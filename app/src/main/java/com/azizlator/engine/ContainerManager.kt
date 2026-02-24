package com.azizlator.engine

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

data class Container(
    val id: String,
    val name: String,
    val status: String,
    val wineVersion: String,
    val dxvkVersion: String,
    val createdAt: Long
)

class ContainerManager(private val context: Context) {

    private val containersFile = File(context.filesDir, "containers.json")

    fun getContainers(): List<Container> {
        if (!containersFile.exists()) return emptyList()
        return try {
            val json = JSONArray(containersFile.readText())
            (0 until json.length()).map { i ->
                val obj = json.getJSONObject(i)
                Container(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    status = obj.getString("status"),
                    wineVersion = obj.optString("wine_version", "Wine 8.0"),
                    dxvkVersion = obj.optString("dxvk_version", "DXVK 2.3"),
                    createdAt = obj.optLong("created_at", System.currentTimeMillis())
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getContainer(id: String): Container? {
        return getContainers().find { it.id == id }
    }

    fun createContainer(name: String, wineVersion: String = "Wine 8.0", dxvkVersion: String = "DXVK 2.3"): Container {
        val container = Container(
            id = UUID.randomUUID().toString(),
            name = name,
            status = "Ready",
            wineVersion = wineVersion,
            dxvkVersion = dxvkVersion,
            createdAt = System.currentTimeMillis()
        )
        val containers = getContainers().toMutableList()
        containers.add(container)
        saveContainers(containers)

        // Create directory
        File(context.filesDir, "containers/${container.id}").mkdirs()

        return container
    }

    fun deleteContainer(id: String) {
        val containers = getContainers().filter { it.id != id }
        saveContainers(containers)
        File(context.filesDir, "containers/$id").deleteRecursively()
    }

    fun updateStatus(id: String, status: String) {
        val containers = getContainers().map {
            if (it.id == id) it.copy(status = status) else it
        }
        saveContainers(containers)
    }

    private fun saveContainers(containers: List<Container>) {
        val json = JSONArray()
        containers.forEach { c ->
            json.put(JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("status", c.status)
                put("wine_version", c.wineVersion)
                put("dxvk_version", c.dxvkVersion)
                put("created_at", c.createdAt)
            })
        }
        containersFile.writeText(json.toString())
    }
}
