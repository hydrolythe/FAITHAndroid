package be.hogent.faith.faith

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import be.hogent.faith.BuildConfig
import be.hogent.faith.database.di.databaseModule
import be.hogent.faith.encryption.di.encryptionModule
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.service.di.serviceModule
import be.hogent.faith.storage.di.storageModule
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        startKoin {
            androidLogger()
            androidContext(this@App)

            modules(
                listOf(
                    appModule,
                    databaseModule,
                    serviceModule,
                    encryptionModule,
                    storageModule
                )
            )
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}