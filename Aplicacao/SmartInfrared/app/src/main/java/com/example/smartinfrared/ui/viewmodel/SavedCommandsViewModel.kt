package com.example.smartinfrared.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartinfrared.data.database.CommandEntity
import com.example.smartinfrared.data.repository.CommandRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedCommandsViewModel @Inject constructor(
    private val repository: CommandRepository
) : ViewModel() {

    val commands: Flow<List<CommandEntity>> = repository.getAllCommandsFlow()

    fun deleteCommand(command: CommandEntity) {
        viewModelScope.launch {
            repository.deleteCommand(command)
        }
    }

    fun insertCommand(command: CommandEntity) {
        viewModelScope.launch {
            repository.insertCommand(command)
        }
    }
}