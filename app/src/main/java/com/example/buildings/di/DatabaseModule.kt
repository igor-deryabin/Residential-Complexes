package com.example.buildings.di

import android.app.Application
import androidx.room.Room
import com.example.buildings.data.local.ComplexesDao
import com.example.buildings.data.local.MainDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    fun provideDatabase(application: Application): MainDatabase {
        return Room.databaseBuilder(application, MainDatabase::class.java, "main_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideComplexesDao(database: MainDatabase): ComplexesDao {
        return  database.complexesDao
    }

    single { provideDatabase(androidApplication()) }
    single { provideComplexesDao(get()) }
}