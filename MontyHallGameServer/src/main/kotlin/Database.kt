package org.aquamarine5.brainspark

import io.ktor.util.logging.Logger
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

@Serializable
data class Database(
    val firstGameCount: Int,
    val firstGameWins: Int,
    val secondGameCount: Int,
    val secondGameWins: Int,
    val thirdGameCount: Int,
    val thirdGameWins: Int
)

class DatabaseManager(private val log: Logger) {
    val firstGameCount = AtomicInteger(0)
    val firstGameWins = AtomicInteger(0)
    val secondGameCount = AtomicInteger(0)
    val secondGameWins = AtomicInteger(0)
    val thirdGameCount = AtomicInteger(0)
    val thirdGameWins = AtomicInteger(0)

    private val recordFile = File("record.json")
    private val json = Json { prettyPrint = true }

    init {
        loadFromFile()
    }

    fun getRecords(): Database {
        return Database(
            firstGameCount.get(),
            firstGameWins.get(),
            secondGameCount.get(),
            secondGameWins.get(),
            thirdGameCount.get(),
            thirdGameWins.get()
        )
    }

    suspend fun clearRecords() {
        firstGameCount.set(0)
        firstGameWins.set(0)
        secondGameCount.set(0)
        secondGameWins.set(0)
        thirdGameCount.set(0)
        thirdGameWins.set(0)
        saveToFile()
    }

    private fun loadFromFile() {
        if (recordFile.exists()) {
            try {
                val content = recordFile.readText()
                if (content.isNotBlank()) {
                    val database = json.decodeFromString<Database>(content)
                    firstGameCount.set(database.firstGameCount)
                    firstGameWins.set(database.firstGameWins)
                    secondGameCount.set(database.secondGameCount)
                    secondGameWins.set(database.secondGameWins)
                    thirdGameCount.set(database.thirdGameCount)
                    thirdGameWins.set(database.thirdGameWins)
                }
            } catch (e: Exception) {
                log.error("Error loading database from ${recordFile.path}: ${e.message}")
            }
        }
    }

    suspend fun startAutoSave() {
        while (true) {
            delay(60_000) // 每分钟 (60 * 1000 ms)
            saveToFile()
        }
    }

    suspend fun saveToFile() {
        withContext(Dispatchers.IO) {
            try {
                val currentData = Database(
                    firstGameCount.get(),
                    firstGameWins.get(),
                    secondGameCount.get(),
                    secondGameWins.get(),
                    thirdGameCount.get(),
                    thirdGameWins.get()
                )
                val jsonString = json.encodeToString(currentData)
                recordFile.writeText(jsonString)
                log.info("Database saved to ${recordFile.path}")
            } catch (e: Exception) {
                println("Error saving database to ${recordFile.path}: ${e.message}")
            }
        }
    }

    fun gameFinish(firstGameWin: Boolean?, secondGameWin: Boolean?, thirdGameWin: Boolean?) {
        if (firstGameWin != null) {
            firstGameCount.incrementAndGet()
            if (firstGameWin)
                firstGameWins.incrementAndGet()
        }

        if (secondGameWin != null) {
            secondGameCount.incrementAndGet()
            if (secondGameWin)
                secondGameWins.incrementAndGet()
        }

        if (thirdGameWin != null) {
            thirdGameCount.incrementAndGet()
            if (thirdGameWin)
                thirdGameWins.incrementAndGet()
        }
        log.debug(
            "first: ${firstGameWins.get()}/${firstGameCount.get()}, " +
                    "second: ${secondGameWins.get()}/${secondGameCount.get()}, " +
                    "third: ${thirdGameWins.get()}/${thirdGameCount.get()}"
        )
    }
}
