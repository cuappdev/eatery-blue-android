package com.example.eateryblueandroid.networking

import com.squareup.moshi.JsonClass
import java.util.*

//class that represents the main object returned
@JsonClass(generateAdapter = true)
data class ResponseObject(val success: Boolean, val eateries: List<Eatery>)

data class Eatery(val id: Int,
                  val name: String,
                  val image_url: String,
                  val menu_summary: String,
                  val campus_area: String,
                  val events: List<Event>,
                  val latitude: Float,
                  val longitude: Float,
                  val payment_methods: List<String>,
                  val location: String,
                  val order_online: Boolean,
                  val online_order_url: String? = null, val wait_times: List<WaitTime>, val alerts: List<Alert>)

data class WaitTime(val canonical_date: Date, val data: List<String>)

data class Event(val description: String,
                 val canonical_date: Date,
                 val start_timestamp: Int,
                 val end_timestamp: Int,
                 val menu: List<MenuCategory>)

data class MenuCategory(val category: String, val items: List<MenuItem>)

data class MenuItem(val healthy: Boolean,
                    val name: String,
                    val base_price: String? = null,
                    val description: String? = null,
                    val sections: String? = null)

data class Alert(val id: Int, val description: String, val start_timestamp: Int, val end_timestamp: Int)