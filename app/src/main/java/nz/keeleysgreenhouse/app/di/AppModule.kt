package nz.keeleysgreenhouse.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nz.keeleysgreenhouse.app.data.AppDatabase
import nz.keeleysgreenhouse.app.data.seed.DatabaseSeeder
import nz.keeleysgreenhouse.app.data.seed.SeedSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "greenhouse.db").build()

    @Provides
    @Singleton
    fun provideSeedSource(@ApplicationContext context: Context): SeedSource =
        SeedSource { name -> context.assets.open(name) }

    @Provides
    @Singleton
    fun provideSeeder(db: AppDatabase, source: SeedSource): DatabaseSeeder =
        DatabaseSeeder(db, source)
}
