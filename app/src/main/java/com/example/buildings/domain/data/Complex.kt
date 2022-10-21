package com.example.buildings.domain.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "complexes")
data class Complex(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val name: String = "",
    val image: String = "",
    val area: String = "",
    val city: String = "",
    val metro: String = "",
    val year: Int = 0,
    val floors: Int = 0,
    val cost: Double = 0.0,
    val buildingsCount: Int = 0,
    val parking: Boolean = false,
    val rented: Boolean = false
)
