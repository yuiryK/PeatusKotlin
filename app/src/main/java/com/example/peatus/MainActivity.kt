package com.example.peatus

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.ViewGroup
import android.widget.*
import java.util.concurrent.Executors
import java.net.URL



class MainActivity : AppCompatActivity() {
    private var isWhiteBackground: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val simpleTextView = findViewById<TextView>(R.id.simpleTextView)
        Executors.newSingleThreadExecutor().execute {
            val json = URL("https://peatus.metaler.com.ua/regions").readText()
            simpleTextView.post { simpleTextView.text = json }
        }
        //create AutoCompleteTextView and
        val autoTextView = AutoCompleteTextView(this)
        val autoTextViewStops = AutoCompleteTextView(this)
        val button = Button(this)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        autoTextView.layoutParams = layoutParams
        autoTextViewStops.layoutParams = layoutParams
        button.layoutParams = layoutParams
        layoutParams.setMargins(30, 30, 30, 30)
        autoTextView.setHint(R.string.hint)
        button.text = getString(R.string.submit)



        val linearLayout = findViewById<LinearLayout>(R.id.linear_layout)
        // Add AutoCompleteTextView and button to LinearLayout
        linearLayout?.addView(autoTextView)
        linearLayout?.addView(autoTextViewStops)
        linearLayout?.addView(button)


        // Get the array of languages
        val languages = resources.getStringArray(R.array.Languages)
        // Create adapter and add in AutoCompleteTextView
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, languages)
        autoTextView.setAdapter(adapter)
        autoTextViewStops.setAdapter(adapter)

        button.setOnClickListener {
            isWhiteBackground = true
          findViewById<FrameLayout>(R.id.container).setBackgroundColor(Color.WHITE)
          val itemFragment = ItemFragment()
          val fragmentTransaction = supportFragmentManager.beginTransaction()
          fragmentTransaction.replace(R.id.container, itemFragment)
          fragmentTransaction.addToBackStack(null)
          fragmentTransaction.commit()
        }

    }
    override fun onBackPressed() {
        if (isWhiteBackground) {
            findViewById<FrameLayout>(R.id.container).setBackgroundColor(Color.TRANSPARENT)
            isWhiteBackground = false
        }

        super.onBackPressed()
    }
}