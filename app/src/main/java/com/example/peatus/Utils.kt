import org.json.JSONArray

fun naturalCompare(str1: String, str2: String): Int {
    val regex = """(\D+|\d+)""".toRegex()

    val parts1 = regex.findAll(str1).map { it.value }.toList()
    val parts2 = regex.findAll(str2).map { it.value }.toList()

    // Сравниваем части поочередно
    val maxLength = maxOf(parts1.size, parts2.size)
    for (i in 0 until maxLength) {
        val part1 = parts1.getOrElse(i) { "" }.toLowerCase()
        val part2 = parts2.getOrElse(i) { "" }.toLowerCase()

        // Если это числа, сравниваем как целые числа
        if (part1.all { it.isDigit() } && part2.all { it.isDigit() }) {
            val num1 = part1.toInt()
            val num2 = part2.toInt()
            val comparison = num1.compareTo(num2)
            if (comparison != 0) return comparison
        } else {
            // Если это строки, сравниваем как обычные строки
            val comparison = part1.compareTo(part2)
            if (comparison != 0) return comparison
        }
    }

    return 0
}


fun naturalSort(items: List<Map<String, Any>>, key: String): List<Map<String, Any>> {
    return items.sortedWith { map1, map2 ->
        val value1 = map1[key].toString()
        val value2 = map2[key].toString()
        naturalCompare(value1, value2)
    }
}

// Функция для преобразования JSON в отформатированные строки
fun parseJsonToFormattedStrings(jsonString: String): List<String> {
    val jsonArray = JSONArray(jsonString)
    val resultList = mutableListOf<String>()

    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)

        // Извлекаем значения и формируем строку
        val title = jsonObject.getString("title")
        val routeLongName = jsonObject.getString("route_long_name")
        val adjustedDate = jsonObject.getString("adjusted_date")
        val adjustedTime = jsonObject.getString("adjusted_time")

        // Формируем строку для добавления в список
        val formattedString = "$title $routeLongName $adjustedDate $adjustedTime"

        // Добавляем строку в список
        resultList.add(formattedString)
    }

    return resultList
}
