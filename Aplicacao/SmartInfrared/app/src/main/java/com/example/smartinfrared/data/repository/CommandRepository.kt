package com.example.smartinfrared.data.repository

import com.example.smartinfrared.data.database.CommandDao
import com.example.smartinfrared.data.database.CommandEntity
import kotlinx.coroutines.flow.Flow

class CommandRepository(private val commandDao: CommandDao) {
//    fun getAllCommands() = commandDao.getAll()
//    fun deleteCommandById(commandId: Int) = commandDao.deleteById(commandId)
    suspend fun insertCommand(command: CommandEntity) = commandDao.insert(command)
    suspend fun deleteCommand(command: CommandEntity) = commandDao.delete(command)
    fun getAllCommandsFlow(): Flow<List<CommandEntity>> = commandDao.getAllCommandsFlow()

}