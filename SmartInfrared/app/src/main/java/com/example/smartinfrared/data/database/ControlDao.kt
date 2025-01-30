package com.example.smartinfrared.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ControlDao {
    @Query("SELECT * FROM controls")
    fun getAll(): List<ControlEntity>

    @Insert
    fun insert(control: ControlEntity)
}