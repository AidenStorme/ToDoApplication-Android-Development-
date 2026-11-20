package be.aidenstorme.todoapplication.di

import be.aidenstorme.todoapplication.data.ActivityRepository
import be.aidenstorme.todoapplication.data.NetworkActivityRepository
import be.aidenstorme.todoapplication.data.OfflineToDoRepository
import be.aidenstorme.todoapplication.data.OfflineUserRepository
import be.aidenstorme.todoapplication.data.ToDoRepository
import be.aidenstorme.todoapplication.data.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindToDoRepository(impl: OfflineToDoRepository): ToDoRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: OfflineUserRepository): UserRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(impl: NetworkActivityRepository): ActivityRepository
}
