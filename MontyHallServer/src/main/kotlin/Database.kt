package org.aquamarine5.brainspark

import com.google.gson.Gson
import io.ktor.util.logging.Logger
import java.io.File
import java.util.*
import kotlin.concurrent.atomics.AtomicInt

data class Record(
    val firstGameCount: Int,
    val firstGameWins: Int,
    val secondGameCount: Int,
    val secondGameWins: Int,
    val thirdGameCount: Int,
    val thirdGameWins: Int
)

class Database(private val logger: Logger) {

    val firstGameCount = AtomicInt(0)
    val firstGameWins = AtomicInt(0)
    val secondGameCount = AtomicInt(0)
    val secondGameWins = AtomicInt(0)
    val thirdGameCount = AtomicInt(0)
    val thirdGameWins = AtomicInt(0)

    init {
        loadRecord()
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                saveRecord()
            }
        }, 60000, 60000)
    }

    fun gameFinish(firstGameWin: Boolean, secondGameWin: Boolean, thirdGameWin: Boolean) {
        firstGameCount.addAndFetch(1)
        if (firstGameWin) {
            firstGameWins.addAndFetch(1)
        }
        secondGameCount.addAndFetch(1)
        if (secondGameWin) {
            secondGameWins.addAndFetch(1)
        }
        thirdGameCount.addAndFetch(1)
        if (thirdGameWin) {
            thirdGameWins.addAndFetch(1)
        }
    }

    private fun saveRecord() {
        val record = Record(
            firstGameCount.load(),
            firstGameWins.load(),
            secondGameCount.load(),
            secondGameWins.load(),
            thirdGameCount.load(),
            thirdGameWins.load()
        )
        val gson = Gson()
        val json = gson.toJson(record)
        File("record.json").writeText(json)
        logger.info("Record saved")
    }

    private fun loadRecord() {
        val file = File("record.json")
        if (file.exists()) {
            val gson = Gson()
            val json = file.readText()
            val record = gson.fromJson(json, Record::class.java)
            firstGameCount.set(record.firstGameCount)
            firstGameWins.set(record.firstGameWins)
            secondGameCount.set(record.secondGameCount)
            secondGameWins.set(record.secondGameWins)
            thirdGameCount.set(record.thirdGameCount)
            thirdGameWins.set(record.thirdGameWins)
            logger.info("Record loaded")
        }
    }
}