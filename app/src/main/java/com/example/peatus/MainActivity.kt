package com.example.peatus

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.ViewGroup
import android.widget.*
import androidx.collection.ObjectList
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import naturalSort
import parseJsonToFormattedStrings


class MainActivity : AppCompatActivity() {
    private var isWhiteBackground: Boolean = false
    @SuppressLint("SuspiciousIndentation", "ResourceType", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userApiUrl = ApiEndPoint.REGIONS.baseUrl
        val url1 = APIUrlBuilder.setBaseUrl(userApiUrl).setRoute("/Narva linn", "Tempo", "31").build()
        val apiClient = ApiClient(url1)

        //create AutoCompleteTextView and
        val autoTextView = AutoCompleteTextView(this)
        2000000.also { autoTextView.id = it }
        val autoTextViewStops = AutoCompleteTextView(this)
            autoTextView.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && autoTextView.text.isEmpty()) {
                    autoTextView.showDropDown() // Показывает весь список, если текст пустой
                }
            }
            autoTextViewStops.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && autoTextViewStops.text.isEmpty()) {
                    autoTextViewStops.showDropDown() // Показывает весь список, если текст пустой
            }
        }
        val button = Button(this)
        val linearLayoutBuses = LinearLayout(this)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        autoTextView.layoutParams = layoutParams
        autoTextViewStops.layoutParams = layoutParams
        button.layoutParams = layoutParams
        layoutParams.setMargins(30, 30, 30, 30)
        autoTextView.setHint(R.string.hint)
        button.text = getString(R.string.clear)


        button.setOnClickListener {
          //  autoTextView.setText("")
            /*val adapter2 = autoTextViewStops.adapter
            if (adapter2 != null) {
                (adapter2 as ArrayAdapter<*>).clear()  // Очищаем адаптер
            }*/
            //autoTextViewStops.setText("")
            //linearLayoutBuses.removeAllViews()
            runOnUiThread {
                val stopTimeText = findViewById<ListView>(R.id.stop_time_textview)
            }
        }

        val linearLayout = findViewById<LinearLayout>(R.id.linear_layout)
        // Add AutoCompleteTextView and button to LinearLayout
        linearLayout?.addView(autoTextView)
        linearLayout?.addView(autoTextViewStops)
        linearLayout?.addView(linearLayoutBuses)
        linearLayout?.addView(button)

        lifecycleScope.launch {
            // Call the suspend function inside the coroutine
            val result = apiClient.fetchData()
            // Handle the result here
            val jsonHandler = JsonHandler()

            // Десериализация и отображение данных
            val items = jsonHandler.deserializeDynamic(result)
            val sortedItems = naturalSort(items, "title")
            val result2 = sortedItems.joinToString(separator = "\n")
           // val simpleTextView = findViewById<TextView>(R.id.simpleTextView)
            ///simpleTextView.post { simpleTextView.text = result2 }
            // Create adapter and add in AutoCompleteTextView
            val titles = sortedItems.map { it["title"].toString()}
            val adapter = ArrayAdapter(this@MainActivity,
                android.R.layout.simple_list_item_1, titles)
            autoTextView.setAdapter(adapter)
        }
        autoTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            val userApiUrl = ApiEndPoint.STOPS.baseUrl
            val url1 = APIUrlBuilder.setBaseUrl(userApiUrl).setRoute("/$selectedItem").build()
            val apiClient = ApiClient(url1)
            lifecycleScope.launch {
                val result = apiClient.fetchData()
                // Handle the result here
                val jsonHandler = JsonHandler()

                // Десериализация и отображение данных
                val items = jsonHandler.deserializeDynamic(result)
                val sortedItems = naturalSort(items, "title")
                val result2 = sortedItems.joinToString(separator = "\n")
                val titles = sortedItems.map { it["title"].toString()}
                val adapter = ArrayAdapter(this@MainActivity,
                    android.R.layout.simple_list_item_1, titles)
                autoTextViewStops.setAdapter(adapter)

            }

        }
        autoTextViewStops.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            val region = autoTextView.text.toString()
            val userApiUrl = ApiEndPoint.BUSES.baseUrl
            val url1 = APIUrlBuilder.setBaseUrl(userApiUrl).setRoute("/$region", selectedItem).build()
            val apiClient = ApiClient(url1)
            val linearLayoutBuses = findViewById<LinearLayout>(R.id.linear_layout_buses)
            linearLayoutBuses.removeAllViews()
            lifecycleScope.launch {
                val result = apiClient.fetchData()
                // Handle the result here
                val jsonHandler = JsonHandler()

                // Десериализация и отображение данных
                val items = jsonHandler.deserializeDynamic(result)
                val sortedItems = naturalSort(items, "title")
                val result2 = sortedItems.joinToString(separator = "\n")
                val titles = sortedItems.map { it["title"].toString() }
                val linearLayout = findViewById<LinearLayout>(R.id.linear_layout_buses)
                for (item in titles) {
                    val button = Button(this@MainActivity) // Создаём новую кнопку
                    button.text = item.toString()       // Устанавливаем текст кнопки
                    button.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Ширина кнопки
                        LinearLayout.LayoutParams.WRAP_CONTENT,  // Высота кнопки
                    )
                    val layoutBusesParameter = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Ширина кнопки
                        LinearLayout.LayoutParams.WRAP_CONTENT,)
                    layoutBusesParameter.setMargins(30, 30, 30, 30)
                    button.setOnClickListener {
                        val region = autoTextView.text.toString()
                        val stop = autoTextViewStops.text.toString()
                        val btn = button.text.toString()
                        val userApiUrl = ApiEndPoint.BUSTIME.baseUrl
                        val url1 =
                            APIUrlBuilder.setBaseUrl(userApiUrl).setRoute("/$region", stop, btn)
                                .build()
                        val apiClient = ApiClient(url1)
                        lifecycleScope.launch {
                            val result = apiClient.fetchData()
                            // Handle the result here
                            val jsonHandler = JsonHandler()

                            // Десериализация и отображение данных
                            val items = jsonHandler.deserializeDynamic(result)
                            val sortedItems = naturalSort(items, "title")
                            val result2 = sortedItems.joinToString(separator = "\n")
                            val titles = sortedItems.map { it["title"].toString() }
                            val itemFragment = ItemFragment.newInstance(1, titles)
                            val fragmentTransaction = supportFragmentManager.beginTransaction()
                            fragmentTransaction.replace(R.id.container, itemFragment)
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                            findViewById<FrameLayout>(R.id.container).setBackgroundColor(Color.WHITE)
                            isWhiteBackground = true

                        }
                    }
                    linearLayout.addView(button)
                }
            }
            val selectedItem2 = parent.getItemAtPosition(position).toString()
            val region2 = autoTextView.text.toString()
            val userApiUrl2 = ApiEndPoint.STOPTIME.baseUrl
            val url2 = APIUrlBuilder.setBaseUrl(userApiUrl2).setRoute("/$region2", selectedItem2).build()
            val apiClient2 = ApiClient(url2)
            lifecycleScope.launch {
                val result = apiClient2.fetchData()
                // Handle the result here
                val jsonHandler = JsonHandler()

                // Десериализация и отображение данных
                val items = jsonHandler.deserializeDynamic(result)
                val sortedItems = naturalSort(items, "title")
                val result2 = sortedItems.joinToString(separator = "\n")
                //val titles = sortedItems.map { it["title"].toString() }
                val titles = parseJsonToFormattedStrings(result)
                val adapter = ArrayAdapter(this@MainActivity,
                    android.R.layout.simple_list_item_1, titles)
                val stoptime = findViewById<ListView>(R.id.stop_time_textview)
                stoptime.adapter = adapter
            }

        }

        button.setOnClickListener {
            autoTextView.setText("")
            val adapter2 = autoTextViewStops.adapter
            if (adapter2 != null) {
                (adapter2 as ArrayAdapter<*>).clear()  // Очищаем адаптер
            }
            autoTextViewStops.setText("")
            val linearLayoutBuses = findViewById<LinearLayout>(R.id.linear_layout_buses)
            linearLayoutBuses.removeAllViews()
            val listTextView = findViewById<ListView>(R.id.stop_time_textview)
            val adapter3 = listTextView.adapter
            if (adapter3 != null) {
                (adapter3 as ArrayAdapter<*>).clear()  // Очищаем адаптер
            }
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