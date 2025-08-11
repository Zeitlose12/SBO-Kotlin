package net.sbo.mod.utils.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.sbo.mod.SBOKotlin
import net.sbo.mod.utils.Register
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.collections.iterator
import kotlin.concurrent.thread

object SboDataObject {
    @JvmField
    var sboData: SboData = SboData()

    @JvmField
    var achievementsData: AchievementsData = AchievementsData()

    @JvmField
    var pastDianaEventsData: PastDianaEventsData = PastDianaEventsData()

    @JvmField
    var dianaTrackerTotal: DianaTrackerTotalData = DianaTrackerTotalData()

    @JvmField
    var dianaTrackerSession: DianaTrackerSessionData = DianaTrackerSessionData()

    @JvmField
    var dianaTrackerMayor: DianaTrackerMayorData = DianaTrackerMayorData()

    @JvmField
    var pfConfigState: PartyFinderConfigState = PartyFinderConfigState()

    @JvmField
    var partyFinderData: PartyFinderData = PartyFinderData()

    lateinit var SBOConfigBundle: SboConfigBundle
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private const val MAX_BACKUPS = 5

    fun init() {
        SBOConfigBundle = loadAllData("SBO")
        sboData = SBOConfigBundle.sboData
        achievementsData = SBOConfigBundle.achievementsData
        pastDianaEventsData = SBOConfigBundle.pastDianaEventsData
        dianaTrackerTotal = SBOConfigBundle.dianaTrackerTotalData
        dianaTrackerSession = SBOConfigBundle.dianaTrackerSessionData
        dianaTrackerMayor = SBOConfigBundle.dianaTrackerMayorData
        pfConfigState = SBOConfigBundle.partyFinderConfigState
        partyFinderData = SBOConfigBundle.partyFinderData
        saveAllDataThreaded("SBO")
        savePeriodically(10)
        ServerLifecycleEvents.SERVER_STOPPING.register {
            saveAndBackupAllDataThreaded("SBO")
        }
    }

    fun <T> load(modName: String, fileName: String, defaultData: T, type: Class<T>): T {
        val modConfigDir = File(FabricLoader.getInstance().configDir.toFile(), modName)
        if (!modConfigDir.exists()) {
            modConfigDir.mkdirs()
        }
        val dataFile = File(modConfigDir, fileName)

        if (!dataFile.exists()) {
            SBOKotlin.logger.info("[$modName] $fileName not found. Creating with default data.")
            save(modName, defaultData, fileName)
            return defaultData
        }

        return try {
            FileReader(dataFile).use { reader ->
                gson.fromJson(reader, type)
            }
        } catch (e: JsonSyntaxException) {
            SBOKotlin.logger.error("[$modName] Error parsing JSON in $fileName, resetting to default data.")
            save(modName, defaultData, fileName)
            defaultData
        } catch (e: Exception) {
            SBOKotlin.logger.error("[$modName] Error reading data file $fileName, resetting to default data.", e)
            e.printStackTrace()
            save(modName, defaultData, fileName)
            defaultData
        }
    }

    private fun loadAchievementsData(modName: String): AchievementsData {
        val modConfigDir = File(FabricLoader.getInstance().configDir.toFile(), modName)
        val dataFile = File(modConfigDir, "sbo_achievements.json")
        val defaultData = AchievementsData()

        if (!dataFile.exists()) {
            SBOKotlin.logger.info("[$modName] sbo_achievements.json not found. Creating with default data.")
            save(modName, defaultData, "sbo_achievements.json")
            return defaultData
        }

        return try {
            val typeToken = object : TypeToken<Map<String, Any>>() {}.type
            val oldFormatData: Map<String, Any> = gson.fromJson(FileReader(dataFile), typeToken)

            val combinedUnlockedIds = mutableSetOf<Int>()
            val achievementMap = mutableMapOf<String, Boolean>()
            var isOldFormat = false

            // Iterate through all key-value pairs in the old file
            for ((key, value) in oldFormatData) {
                if (key != "unlocked" && value is Boolean) {
                    // This handles the "1": true, "2": true, etc. pairs
                    achievementMap[key] = value
                    key.toIntOrNull()?.let { combinedUnlockedIds.add(it) }
                    isOldFormat = true
                }
            }

            // Add the already-unlocked list from the old file, if it exists
            val existingUnlockedList = (oldFormatData["unlocked"] as? List<*>)?.filterIsInstance<Int>() ?: emptyList()
            combinedUnlockedIds.addAll(existingUnlockedList)

            if (isOldFormat) {
                SBOKotlin.logger.info("[$modName] Old achievements file format detected. Migrating to new format.")
                val newAchievementsData = AchievementsData(
                    unlocked = combinedUnlockedIds.toList().sorted(),
                    achievements = achievementMap
                )
                save(modName, newAchievementsData, "sbo_achievements.json")
                SBOKotlin.logger.info("[$modName] Achievements data migrated successfully.")
                newAchievementsData
            } else {
                // If not old format, load it directly as the new AchievementsData class
                // which now includes the 'achievements' map.
                load(modName, "sbo_achievements.json", defaultData, AchievementsData::class.java)
            }
        } catch (e: Exception) {
            SBOKotlin.logger.error("[$modName] Error reading sbo_achievements.json, resetting to default data.", e)
            save(modName, defaultData, "sbo_achievements.json")
            defaultData
        }
    }

    fun loadAllData(modName: String): SboConfigBundle {
        val sboData = load(modName, "SboData.json", SboData(), SboData::class.java)
        val achievementsData = loadAchievementsData(modName)
        val pastDianaEventsData = load(modName, "pastDianaEvents.json", PastDianaEventsData(), PastDianaEventsData::class.java)
        val dianaTrackerTotalData = load(modName, "dianaTrackerTotal.json", DianaTrackerTotalData(), DianaTrackerTotalData::class.java)
        val dianaTrackerSessionData = load(modName, "dianaTrackerSession.json", DianaTrackerSessionData(), DianaTrackerSessionData::class.java)
        val dianaTrackerMayorData = load(modName, "dianaTrackerMayor.json", DianaTrackerMayorData(), DianaTrackerMayorData::class.java)
        val partyFinderConfigState = load(modName, "partyFinderConfigState.json", PartyFinderConfigState(), PartyFinderConfigState::class.java)
        val partyFinderData = load(modName, "partyFinderData.json", PartyFinderData(), PartyFinderData::class.java)
        return SboConfigBundle(sboData, achievementsData, pastDianaEventsData, dianaTrackerTotalData, dianaTrackerSessionData, dianaTrackerMayorData, partyFinderConfigState, partyFinderData)
    }

    private fun zipFolder(folderToZip: File, zipFilePath: File) {
        ZipOutputStream(FileOutputStream(zipFilePath)).use { zos ->
            folderToZip.walk().filter { it.isFile }.forEach { file ->
                val entry = ZipEntry(file.name)
                zos.putNextEntry(entry)
                FileInputStream(file).use { fis ->
                    fis.copyTo(zos)
                }
                zos.closeEntry()
            }
        }
    }

    private fun createBackup(modName: String) {
        try {
            val modConfigDir = File(FabricLoader.getInstance().configDir.toFile(), modName)
            val backupDir = File(modConfigDir, "backup")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val tempBackupDir = File(backupDir, "SBOBackup_$timestamp")
            tempBackupDir.mkdirs()

            val bundle = SBOConfigBundle
            saveToFolder(tempBackupDir, bundle.sboData, "SboData.json")
            saveToFolder(tempBackupDir, bundle.achievementsData, "sbo_achievements.json")
            saveToFolder(tempBackupDir, bundle.pastDianaEventsData, "pastDianaEvents.json")
            saveToFolder(tempBackupDir, bundle.dianaTrackerTotalData, "dianaTrackerTotal.json")
            saveToFolder(tempBackupDir, bundle.dianaTrackerSessionData, "dianaTrackerSession.json")
            saveToFolder(tempBackupDir, bundle.dianaTrackerMayorData, "dianaTrackerMayor.json")
            saveToFolder(tempBackupDir, bundle.partyFinderConfigState, "partyFinderConfigState.json")
            saveToFolder(tempBackupDir, bundle.partyFinderData, "partyFinderData.json")

            val zipFile = File(backupDir, "SBOBackup_$timestamp.zip")
            zipFolder(tempBackupDir, zipFile)

            tempBackupDir.deleteRecursively()
            SBOKotlin.logger.info("[$modName] Created new backup: ${zipFile.name}")

            val existingBackups = backupDir.listFiles { _, name -> name.endsWith(".zip") }?.toList() ?: emptyList()
            if (existingBackups.size > MAX_BACKUPS) {
                val oldestBackup = existingBackups.minByOrNull { it.lastModified() }
                oldestBackup?.let {
                    it.delete()
                    SBOKotlin.logger.info("[$modName] Deleted old backup: ${it.name}")
                }
            }
        } catch (e: Exception) {
            SBOKotlin.logger.error("[$modName] Error creating backup:", e)
        }
    }

    val configMapforSave = mapOf(
        "SboData" to Pair({ save("SBO", sboData, "SboData.json") }, sboData),
        "AchievementsData" to Pair({ save("SBO", achievementsData, "sbo_achievements.json") }, achievementsData),
        "PastDianaEventsData" to Pair({ save("SBO", pastDianaEventsData, "pastDianaEvents.json") }, pastDianaEventsData),
        "DianaTrackerTotalData" to Pair({ save("SBO", dianaTrackerTotal, "dianaTrackerTotal.json") }, dianaTrackerTotal),
        "DianaTrackerSessionData" to Pair({ save("SBO", dianaTrackerSession, "dianaTrackerSession.json") }, dianaTrackerSession),
        "DianaTrackerMayorData" to Pair({ save("SBO", dianaTrackerMayor, "dianaTrackerMayor.json") }, dianaTrackerMayor),
        "PartyFinderConfigState" to Pair({ save("SBO", pfConfigState, "partyFinderConfigState.json") }, pfConfigState),
        "PartyFinderData" to Pair({ save("SBO", partyFinderData, "partyFinderData.json") }, partyFinderData)
    )

    private fun <T> saveToFolder(folder: File, data: T, fileName: String) {
        FileWriter(File(folder, fileName)).use { writer ->
            gson.toJson(data, writer)
        }
    }

    private fun saveAllData() {
        configMapforSave.forEach { (configName, configData) ->
            configData.first.invoke()
        }
    }

    fun saveAllDataThreaded(modName: String) {
        thread(isDaemon = true) {
            SBOKotlin.logger.info("[$modName] Saving all data to disk...")
            saveAllData()
            SBOKotlin.logger.info("[$modName] All data saved successfully.")
        }
    }

    fun saveAndBackupAllDataThreaded(modName: String) {
        thread(isDaemon = true) {
            SBOKotlin.logger.info("Saving all data to disk and creating backup...")
            saveAllData()
            SBOKotlin.logger.info("All data saved successfully.")
            createBackup(modName)
        }
    }

    /**
     * Saves all data periodically based on the specified interval in minutes.
     * @param interval The interval in minutes at which to save the data.
     */
    fun savePeriodically(interval: Int) {
        Register.onTick(20 * 60 * interval) { client ->
            saveAllDataThreaded("SBO")
        }
    }

    /**
     * Saves the given data to a file in the mod's config directory.
     * If the directory does not exist, it will be created.
     * @param modName The name of the mod.
     * @param data The data to save.
     * @param fileName The name of the file to save the data in.
     */
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

    /**
     * Saves the specified config by its name.
     * If the config name is not valid, it will log a warning.
     * @param configName The name of the config to save.
     */
    fun save(configName: String) {
        configMapforSave[configName]?.first?.invoke()
            ?: SBOKotlin.logger.warn("[$configName] is not a valid config name. Please use a valid config name")
    }
}