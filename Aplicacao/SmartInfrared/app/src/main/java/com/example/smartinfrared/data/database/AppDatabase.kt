package com.example.smartinfrared.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CommandEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun commandDao(): CommandDao
    abstract fun controlDao(): ControlDao
}