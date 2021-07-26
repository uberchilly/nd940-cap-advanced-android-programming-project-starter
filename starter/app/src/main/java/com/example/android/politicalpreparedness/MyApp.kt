package com.example.android.politicalpreparedness

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.election.*
import com.example.android.politicalpreparedness.navigation.NavigationDispatcher
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(appModule)
        }
    }
}

val appModule = module {

    single {
        CivicsApi.retrofitService
    }
    single {
        ElectionDatabase.getInstance(get())
    }
    single {
        val db: ElectionDatabase = get()
        return@single db.electionDao
    }
    single<ElectionRepository> {
        ElectionRepositoryImpl(get(), get())
    }
    single {
        NavigationDispatcher()
    }
    viewModel {
        ElectionsViewModel(get(), get())
    }
    single<VoterInfoRepository> {
        VoterInfoRepositoryImpl(get())
    }
    viewModel { (handle: SavedStateHandle) ->
        VoterInfoViewModel(handle, get(), get())
    }

    viewModel {
        RepresentativeViewModel(get())
    }
}