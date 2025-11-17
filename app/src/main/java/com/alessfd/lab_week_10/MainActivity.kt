package com.alessfd.lab_week_10

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.alessfd.lab_week_10.database.Total
import com.alessfd.lab_week_10.database.TotalDatabase
import com.alessfd.lab_week_10.database.TotalObject
import com.alessfd.lab_week_10.viewmodels.TotalViewModel
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val db by lazy { prepareDatabase() }

    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeValueFromDatabase()
        prepareViewModel()
    }

    override fun onStart() {
        super.onStart()
        val saved = db.totalDao().getTotal(ID)

        if (saved.isNotEmpty()) {
            Toast.makeText(
                this,
                "Last Update: ${saved.first().total.date}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onPause() {
        super.onPause()

        val currentValue = viewModel.total.value ?: 0
        val newDate = Date().toString()

        db.totalDao().update(
            Total(
                ID,
                TotalObject(
                    currentValue,
                    newDate
                )
            )
        )
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        viewModel.total.observe(this) { value ->
            updateText(value)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java,
            "total-database"
        )
            .fallbackToDestructiveMigration()  // <-- fixes crashes after schema change
            .allowMainThreadQueries()
            .build()
    }

    private fun initializeValueFromDatabase() {
        val totalList = db.totalDao().getTotal(ID)

        if (totalList.isEmpty()) {
            db.totalDao().insert(
                Total(
                    id = ID,
                    total = TotalObject(
                        value = 0,
                        date = Date().toString()
                    )
                )
            )
        } else {
            val valueFromDB = totalList.first().total.value
            viewModel.setTotal(valueFromDB)
        }
    }

    companion object {
        const val ID: Long = 1
    }
}