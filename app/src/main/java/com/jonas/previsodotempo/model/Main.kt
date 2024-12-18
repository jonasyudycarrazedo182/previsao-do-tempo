package com.jonas.previsodotempo.model

import com.google.gson.JsonObject
import org.json.JSONObject
import java.util.WeakHashMap

data class Main (
    val main: JsonObject,
    val sys: JsonObject,
    val weather: List<Weather>,
    val name: String,
)
