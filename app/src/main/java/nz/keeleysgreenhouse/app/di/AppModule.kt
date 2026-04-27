package nz.keeleysgreenhouse.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nz.keeleysgreenhouse.app.data.AppDatabase
import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.dao.DiseaseDao
import nz.keeleysgreenhouse.app.data.dao.PestDao
import nz.keeleysgreenhouse.app.data.dao.SearchHistoryDao
import nz.keeleysgreenhouse.app.data.dao.TaskDao
import nz.keeleysgreenhouse.app.data.dao.UserPlantingDao
import nz.keeleysgreenhouse.app.data.seed.DatabaseSeeder
import nz.keeleysgreenhouse.app.data.seed.SeedSource
import nz.keeleysgreenhouse.app.domain.usecase.GetAllMonthsCropsUseCase
import nz.keeleysgreenhouse.app.domain.usecase.GetCropDetailUseCase
import nz.keeleysgreenhouse.app.domain.usecase.GetDiseaseDetailUseCase
import nz.keeleysgreenhouse.app.domain.usecase.GetMonthCropsUseCase
import nz.keeleysgreenhouse.app.domain.usecase.GetPestDetailUseCase
import nz.keeleysgreenhouse.app.domain.usecase.GetThisMonthCropsUseCase
import nz.keeleysgreenhouse.app.domain.usecase.ObserveCropsUseCase
import nz.keeleysgreenhouse.app.domain.usecase.ObserveDiseasesUseCase
import nz.keeleysgreenhouse.app.domain.usecase.ObservePestsUseCase
import nz.keeleysgreenhouse.app.domain.usecase.SearchAllUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "greenhouse.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideSeedSource(@ApplicationContext context: Context): SeedSource =
        SeedSource { name -> context.assets.open(name) }

    @Provides
    @Singleton
    fun provideSeeder(db: AppDatabase, source: SeedSource): DatabaseSeeder =
        DatabaseSeeder(db, source)

    @Provides
    fun provideCropDao(db: AppDatabase): CropDao = db.cropDao()

    @Provides
    fun providePestDao(db: AppDatabase): PestDao = db.pestDao()

    @Provides
    fun provideDiseaseDao(db: AppDatabase): DiseaseDao = db.diseaseDao()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideUserPlantingDao(db: AppDatabase): UserPlantingDao = db.userPlantingDao()

    @Provides
    fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao = db.searchHistoryDao()

    @Provides
    fun provideSearchAllUseCase(
        cropDao: CropDao,
        pestDao: PestDao,
        diseaseDao: DiseaseDao
    ): SearchAllUseCase = SearchAllUseCase(cropDao, pestDao, diseaseDao)

    @Provides
    fun provideGetThisMonthCropsUseCase(cropDao: CropDao): GetThisMonthCropsUseCase =
        GetThisMonthCropsUseCase(cropDao)

    @Provides
    fun provideObserveCropsUseCase(cropDao: CropDao): ObserveCropsUseCase =
        ObserveCropsUseCase(cropDao)

    @Provides
    fun provideGetAllMonthsCropsUseCase(cropDao: CropDao): GetAllMonthsCropsUseCase =
        GetAllMonthsCropsUseCase(cropDao)

    @Provides
    fun provideGetMonthCropsUseCase(cropDao: CropDao): GetMonthCropsUseCase =
        GetMonthCropsUseCase(cropDao)

    @Provides
    fun provideGetCropDetailUseCase(
        cropDao: CropDao,
        pestDao: PestDao,
        diseaseDao: DiseaseDao
    ): GetCropDetailUseCase = GetCropDetailUseCase(cropDao, pestDao, diseaseDao)

    @Provides
    fun provideObservePestsUseCase(pestDao: PestDao): ObservePestsUseCase =
        ObservePestsUseCase(pestDao)

    @Provides
    fun provideGetPestDetailUseCase(
        pestDao: PestDao,
        cropDao: CropDao
    ): GetPestDetailUseCase = GetPestDetailUseCase(pestDao, cropDao)

    @Provides
    fun provideObserveDiseasesUseCase(diseaseDao: DiseaseDao): ObserveDiseasesUseCase =
        ObserveDiseasesUseCase(diseaseDao)

    @Provides
    fun provideGetDiseaseDetailUseCase(
        diseaseDao: DiseaseDao,
        cropDao: CropDao
    ): GetDiseaseDetailUseCase = GetDiseaseDetailUseCase(diseaseDao, cropDao)
}
