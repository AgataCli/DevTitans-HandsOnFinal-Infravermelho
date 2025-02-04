package com.example.smartinfrared.data

import android.content.Context
import androidx.room.Room
import com.example.smartinfrared.data.database.AppDatabase
import com.example.smartinfrared.data.database.CommandDao
import com.example.smartinfrared.data.database.ControlDao
import com.example.smartinfrared.data.repository.CommandRepository
import com.example.smartinfrared.data.repository.ControlRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smart-infrared-db"
        ).build()
    }

    @Provides
    fun provideCommandDao(database: AppDatabase) = database.commandDao()

    @Provides
    fun provideControlDao(database: AppDatabase) = database.controlDao()

    @Provides
    fun provideCommandRepository(dao: CommandDao) = CommandRepository(dao)

    @Provides
    fun provideControlRepository(dao: ControlDao) = ControlRepository(dao)
}