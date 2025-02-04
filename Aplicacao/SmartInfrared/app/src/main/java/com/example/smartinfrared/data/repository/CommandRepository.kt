package com.example.smartinfrared.data.repository

import com.example.smartinfrared.data.database.CommandDao
import com.example.smartinfrared.data.database.CommandEntity

class CommandRepository(private val commandDao: CommandDao) {
    fun getAllCommands() = commandDao.getAll()
    fun insertCommand(command: CommandEntity) = commandDao.insert(command)
    fun deleteCommandById(commandId: Int) = commandDao.deleteById(commandId)
    fun deleteCommand(command: CommandEntity) = commandDao.delete(command)

}