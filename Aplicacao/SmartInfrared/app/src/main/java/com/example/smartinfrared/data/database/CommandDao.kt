package com.example.smartinfrared.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CommandDao {
    @Query("SELECT * FROM commands")
    fun getAll(): List<CommandEntity>

    @Insert
    fun insert(command: CommandEntity)

    @Query("DELETE FROM commands WHERE id = :commandId")
    fun deleteById(commandId: Int)

    @Delete
    fun delete(command: CommandEntity)
}