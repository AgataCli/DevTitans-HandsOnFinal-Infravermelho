package com.example.smartinfrared.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.smartinfrared.data.database.CommandDao
import com.example.smartinfrared.data.database.CommandEntity
import com.example.smartinfrared.data.database.ControlDao

@Database(entities = [CommandEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun commandDao(): CommandDao
    //abstract fun controlDao(): ControlDao
}