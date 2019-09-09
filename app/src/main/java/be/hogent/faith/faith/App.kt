package be.hogent.faith.faith

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import be.hogent.faith.database.di.databaseModule
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.service.usecases.di.serviceModule
import be.hogent.faith.storage.di.storageModule
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

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
                    storageModule
                )
            )
        }
    }
}