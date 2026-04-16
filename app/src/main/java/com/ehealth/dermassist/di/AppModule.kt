package com.ehealth.dermassist.di

import com.ehealth.dermassist.data.repository.AppRepositoryImpl
import com.ehealth.dermassist.data.repository.ScanRepositoryImpl
import com.ehealth.dermassist.domain.repository.AppRepository
import com.ehealth.dermassist.domain.repository.ScanRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAppRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): AppRepository {
        return AppRepositoryImpl(firebaseAuth, firestore)
    }

    @Provides
    @Singleton
    fun provideScanRepository(firestore: FirebaseFirestore): ScanRepository {
        return ScanRepositoryImpl(firestore)
    }
}
