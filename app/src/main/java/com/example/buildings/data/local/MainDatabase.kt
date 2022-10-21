package com.example.buildings.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.buildings.domain.data.Complex

@Database(
    entities = [(Complex::class)],
    version = 1, exportSchema = false
)
abstract class MainDatabase: RoomDatabase() {
    abstract val complexesDao: ComplexesDao
}