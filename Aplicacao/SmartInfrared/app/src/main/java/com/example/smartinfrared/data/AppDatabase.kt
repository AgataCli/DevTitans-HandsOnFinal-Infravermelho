package com.example.smartinfrared.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.smartinfrared.data.database.CommandDao
import com.example.smartinfrared.data.database.CommandEntity
import com.example.smartinfrared.data.database.ControlDao
import com.example.smartinfrared.data.database.Converters

@Database(entities = [CommandEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun commandDao(): CommandDao
    //abstract fun controlDao(): ControlDao
}