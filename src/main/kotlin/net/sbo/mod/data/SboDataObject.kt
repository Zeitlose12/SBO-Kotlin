package net.sbo.mod.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object SboDataObject {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun <T> load(modName: String, fileName: String, defaultData: T, type: Class<T>): T {
        val modConfigDir = File(FabricLoader.getInstance().configDir.toFile(), modName)
        if (!modConfigDir.exists()) {
            modConfigDir.mkdirs()
        }
        val dataFile = File(modConfigDir, fileName)

        if (!dataFile.exists()) {
            save(modName, defaultData, fileName)
            return defaultData
        }

        return try {
            FileReader(dataFile).use { reader ->
                gson.fromJson(reader, type)
            }
        } catch (e: JsonSyntaxException) {
            println("[$modName] Error parsing JSON, resetting to default data.")
            save(modName, defaultData, fileName)
            defaultData
        } catch (e: Exception) {
            println("[$modName] Error reading data file, resetting to default data.")
            e.printStackTrace()
            save(modName, defaultData, fileName)
            defaultData
        }
    }

    fun loadAllData(modName: String): SboConfigBundle {
        val sboData = load(modName, "SboData.json", SboData(), SboData::class.java)
        val achievementsData = load(modName, "sbo_achievements.json", AchievementsData(), AchievementsData::class.java)
        return SboConfigBundle(sboData, achievementsData)
    }

    fun <T> save(modName: String, data: T, fileName: String) {
        val modConfigDir = File(FabricLoader.getInstance().configDir.toFile(), modName)
        if (!modConfigDir.exists()) {
            modConfigDir.mkdirs()
        }
        val dataFile = File(modConfigDir, fileName)
        FileWriter(dataFile).use { writer ->
            gson.toJson(data, writer)
        }
    }
}