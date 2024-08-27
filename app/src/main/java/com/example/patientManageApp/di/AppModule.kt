package com.example.patientManageApp.di

import com.example.patientManageApp.data.FirebaseRepositoryImpl
import com.example.patientManageApp.domain.repository.FirebaseRepository
import com.example.patientManageApp.domain.usecase.GetCameraData
import com.example.patientManageApp.domain.usecase.GetOccurrenceData
import com.example.patientManageApp.domain.usecase.GetOccurrenceJPG
import com.example.patientManageApp.domain.usecase.GetOccurrenceMP4
import com.example.patientManageApp.domain.usecase.GetPatientData
import com.example.patientManageApp.domain.usecase.GetUserData
import com.example.patientManageApp.domain.usecase.RemoveUserData
import com.example.patientManageApp.domain.usecase.UpdateAgreeTermOfService
import com.example.patientManageApp.domain.usecase.UpdateCameraData
import com.example.patientManageApp.domain.usecase.UpdateFcmToken
import com.example.patientManageApp.domain.usecase.UpdatePatientData
import com.example.patientManageApp.domain.usecase.UpdateUserData
import com.example.patientManageApp.domain.usecase.UseCases
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return Firebase.database
    }

    @Provides
    @Singleton
    fun providerFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        db: FirebaseDatabase,
        storage: FirebaseStorage
    ): FirebaseRepository = FirebaseRepositoryImpl(db, storage)

    @Provides
    @Singleton
    fun provideUseCases(
        firebaseRepository: FirebaseRepository
    ) = UseCases(
        getUserData = GetUserData(firebaseRepository),
        getPatientData = GetPatientData(firebaseRepository),
        updateUserData = UpdateUserData(firebaseRepository),
        updatePatientData = UpdatePatientData(firebaseRepository),
        removeUserData = RemoveUserData(firebaseRepository),
        updateCameraData = UpdateCameraData(firebaseRepository),
        getCameraData = GetCameraData(firebaseRepository),
        getOccurrenceData = GetOccurrenceData(firebaseRepository),
        updateAgreeTermOfService = UpdateAgreeTermOfService(firebaseRepository),
        updateFcmToken = UpdateFcmToken(firebaseRepository),
        getOccurrenceJPG = GetOccurrenceJPG(firebaseRepository),
        getOccurrenceMP4 = GetOccurrenceMP4(firebaseRepository)
    )
}