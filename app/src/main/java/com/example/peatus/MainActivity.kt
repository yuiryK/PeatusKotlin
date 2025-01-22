package com.example.peatus

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
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

        //create AutoCompleteTextView and
        val autoTextView = AutoCompleteTextView(this)
        val autoTextViewStops = AutoCompleteTextView(this)
        autoTextView.id = View.generateViewId()
        autoTextViewStops.id = View.generateViewId()
        Storage.autoTextViewId = autoTextView.id
        Storage.autoTextViewStopsId = autoTextViewStops.id
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
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        autoTextView.layoutParams = layoutParams
        autoTextViewStops.layoutParams = layoutParams
        button.layoutParams = layoutParams
        layoutParams.setMargins(30, 30, 30, 30)
        autoTextView.setHint(R.string.hint)
        button.text = getString(R.string.clear)


        val linearLayout = findViewById<LinearLayout>(R.id.linear_layout)
        // Add AutoCompleteTextView and button to LinearLayout
        linearLayout?.addView(autoTextView)
        linearLayout?.addView(autoTextViewStops)
        linearLayout?.addView(linearLayoutBuses)
        linearLayout?.addView(button)

      handleRequest(ApiEndPoint.REGIONS, autoTextView, "")

        autoTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            handleRequest(ApiEndPoint.STOPS, autoTextViewStops, "/$selectedItem")


        }
        autoTextViewStops.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            val region = autoTextView.text.toString()
            val userApiUrl = ApiEndPoint.BUSES.baseUrl
            val url =
                APIUrlBuilder.setBaseUrl(userApiUrl).setRoute("/$region", selectedItem).build()
            val apiClient = ApiClient(url)
            val linearLayoutBuses = findViewById<LinearLayout>(R.id.linear_layout_buses)
                linearLayoutBuses.removeAllViews()

            lifecycleScope.launch {
                 val result = apiClient.fetchData()
                 // Handle the result here
                 val jsonHandler = JsonHandler()

                 // Десериализация и отображение данных
                 val items = jsonHandler.deserializeDynamic(result)
                 val sortedItems = naturalSort(items, "title")
                 val titles = sortedItems.map { it["title"].toString() }
                 val linearLayout = findViewById<LinearLayout>(R.id.linear_layout_buses)
                 for (item in titles) {
                     val button = Button(this@MainActivity) // Создаём новую кнопку
                     button.text = item.toString()       // Устанавливаем текст кнопки
                     button.layoutParams = LinearLayout.LayoutParams(
                         LinearLayout.LayoutParams.MATCH_PARENT, // Ширина кнопки
                         LinearLayout.LayoutParams.WRAP_CONTENT,  // Высота кнопки
                     )
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

            val stoptime = findViewById<ListView>(R.id.stop_time_textview)
            handleRequest(ApiEndPoint.STOPTIME, stoptime, "/$region", selectedItem)

        }

        button.setOnClickListener {
            autoTextView.setText("")
            val adapter = autoTextViewStops.adapter
            if (adapter != null) {
                (adapter as ArrayAdapter<*>).clear()  // Очищаем адаптер
            }
            autoTextViewStops.setText("")
            val linearLayoutBuses = findViewById<LinearLayout>(R.id.linear_layout_buses)
            linearLayoutBuses.removeAllViews()
            val listTextView = findViewById<ListView>(R.id.stop_time_textview)
            val adapter2 = listTextView.adapter
            if (adapter2 != null) {
                (adapter2 as ArrayAdapter<*>).clear()  // Очищаем адаптер
            }
        }

    }

    override fun onBackPressed() {
        if (isWhiteBackground) {
            findViewById<FrameLayout>(R.id.container).setBackgroundColor(Color.TRANSPARENT)
            ItemFragment.newInstance(1, emptyList())
            isWhiteBackground = false
        }

        super.onBackPressed()
    }

    private fun handleRequest(apiEndpoint: ApiEndPoint, view: View, vararg routeParams: String) {
        val getApiUrl: (ApiEndPoint) -> String = { endpoint -> endpoint.baseUrl }
        val userApiUrl = getApiUrl(apiEndpoint)

        val routePath = routeParams.joinToString(separator = "/")
        val url = APIUrlBuilder.setBaseUrl(userApiUrl).setRoute(routePath).build()

        try {
            lifecycleScope.launch {
                val apiClient = ApiClient(url)
                val result = apiClient.fetchData()

                val jsonHandler = JsonHandler()
                val items = jsonHandler.deserializeDynamic(result)

                jsonHandler.displayDynamicItems(items)

                val sortedItems = naturalSort(items, "title")
                val titles = sortedItems.map { it["title"].toString() }

                when (view) {
                    is AutoCompleteTextView -> {
                        val adapter = ArrayAdapter(
                            view.context,
                            android.R.layout.simple_dropdown_item_1line,
                            titles
                        )
                        view.setAdapter(adapter)
                    }

               /*     is LinearLayout -> {
                        // Для каждого элемента в titles создаём кнопку и добавляем её в LinearLayout
                        view.removeAllViews() // Очищаем старые элементы
                        for (item in titles) {
                            val button = Button(this@MainActivity).apply {
                                text = item
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                setOnClickListener {
                                    // Обработка клика на кнопке
                                    val ids = Storage.autoTextViewId
                                    val idStop = Storage.autoTextViewStopsId
                                    val region =
                                        findViewById<AutoCompleteTextView>(ids).text.toString()
                                    val stop =
                                        findViewById<AutoCompleteTextView>(idStop).text.toString()
                                    val buttonText = this.text.toString()
                                    //handleRequest(ApiEndPoint.BUSTIME, this, nText)
                                    val getApiUrl = ApiEndPoint.BUSTIME
                                    val userApiUrl = getApiUrl(apiEndpoint)
                                    val routeParams = arrayOf("/$region", stop, buttonText)

                                    val routePath = routeParams.joinToString(separator = "/")
                                    val url = APIUrlBuilder.setBaseUrl(userApiUrl).setRoute(routePath).build()
                                    lifecycleScope.launch {
                                        val apiClient = ApiClient(url)
                                        val result = apiClient.fetchData()

                                        val jsonHandler = JsonHandler()
                                        val items = jsonHandler.deserializeDynamic(result)

                                        jsonHandler.displayDynamicItems(items)

                                        val sortedItems = naturalSort(items, "title")
                                        val titles = sortedItems.map { it["title"].toString() }

                                        val itemFragment = ItemFragment.newInstance(1, titles)
                                        val fragmentTransaction =
                                            supportFragmentManager.beginTransaction()
                                        fragmentTransaction.replace(R.id.container, itemFragment)
                                        fragmentTransaction.addToBackStack(null)
                                        fragmentTransaction.commit()
                                        findViewById<FrameLayout>(R.id.container).setBackgroundColor(
                                            Color.WHITE
                                        )
                                        isWhiteBackground = true
                                    }
                                }
                            }
                            view.addView(button)
                        }

                    }*/

                    is Button -> {

                    }
                    is ListView -> {
                        val titles = parseJsonToFormattedStrings(result)
                        val adapter = ArrayAdapter(
                            view.context,
                            android.R.layout.simple_dropdown_item_1line,
                            titles
                        )
                        val stoptime = findViewById<ListView>(R.id.stop_time_textview)
                        stoptime.adapter = adapter
                    }


                    else -> {
                        println("No specific handling for this view type.")
                    }
                }
            }
        }
    catch (e: Exception) {
            println("Error occurred: ${e.message}")
        }
    }
}
