package com.example.drinkinggame

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {

    private lateinit var truths: MutableList<Truth>
    private lateinit var questionTextView: TextView
    private lateinit var buttons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val malePlayers = mutableListOf<String>()
        val femalePlayers = mutableListOf<String>()

        val addPlayerButton = findViewById<Button>(R.id.addPlayerButton)
        addPlayerButton.setOnClickListener {
            val radioMale = findViewById<RadioButton>(R.id.radioMale)
            val radioFemale = findViewById<RadioButton>(R.id.radioFemale)
            val playerName = findViewById<EditText>(R.id.nameInput)

            if(radioMale.isChecked){
                malePlayers += playerName.text.toString()
                Toast.makeText(this, "Male Player Added: ${playerName.text}", Toast.LENGTH_SHORT).show()
            }
            else if (radioFemale.isChecked){
                femalePlayers += playerName.text.toString()
                Toast.makeText(this, "Female Player Added: ${playerName.text}", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show()
            }
        }

        questionTextView = findViewById(R.id.questionView)
        val button1 = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)
        buttons = listOf(button1, button2, button3, button4)

        copyAssetToFile(this, "truths.json", "truths.json")
        val file = File(filesDir, "truths.json")

        if (file.exists()) {
            val jsonString = file.readText()
            truths = getTruthsFromJson(jsonString).toMutableList()
            showRandomQuestion()

            buttons.forEach { button ->
                button.setOnClickListener {
                    showRandomQuestion()
                }
            }
        } else {
            Log.e("MainActivity", "truths.json not found")
        }
    }

    private fun showRandomQuestion() {
        if (truths.isNotEmpty()) {
            val randomTruth = truths.removeAt(truths.indices.random())

            questionTextView.text = randomTruth.question

            val options = listOf(
                randomTruth.opt1,
                randomTruth.opt2,
                randomTruth.opt3,
                randomTruth.answer
            ).shuffled()

            buttons.forEachIndexed { index, button ->
                button.text = options[index]
            }
        } else {
            questionTextView.text = "Game Over"
            buttons.forEach { it.visibility = View.GONE }
        }
    }

    private fun getTruthsFromJson(jsonString: String): List<Truth> {
        val listType = object : TypeToken<List<Truth>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

    private fun copyAssetToFile(context: Context, assetFileName: String, outputFileName: String) {
        try {
            context.assets.open(assetFileName).use { inputStream ->
                FileOutputStream(File(context.filesDir, outputFileName)).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("MainActivity", "Failed to copy asset file: $e")
        }
    }
}

data class Truth(
    val question: String,
    val opt1: String,
    val opt2: String,
    val opt3: String,
    val answer: String
)

data class Player(
    var name: String,
    var gender: String
)