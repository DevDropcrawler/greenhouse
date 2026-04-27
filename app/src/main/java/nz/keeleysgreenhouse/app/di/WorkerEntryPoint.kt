package nz.keeleysgreenhouse.app.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nz.keeleysgreenhouse.app.data.dao.TaskDao

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkerEntryPoint {
    fun taskDao(): TaskDao
}
