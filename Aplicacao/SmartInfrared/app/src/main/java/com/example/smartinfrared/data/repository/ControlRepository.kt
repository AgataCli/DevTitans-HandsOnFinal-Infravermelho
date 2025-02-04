package com.example.smartinfrared.data.repository

import com.example.smartinfrared.data.database.ControlDao
import com.example.smartinfrared.data.database.ControlEntity

class ControlRepository(private val controlDao: ControlDao) {
    fun getAllCommands() = controlDao.getAll()
    fun insertCommand(command: ControlEntity) = controlDao.insert(command)
}