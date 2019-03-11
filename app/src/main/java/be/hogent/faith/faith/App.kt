package be.hogent.faith.faith

import android.app.Application
import be.hogent.faith.database.di.databaseModule
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.service.usecases.di.serviceModule
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        startKoin(this, listOf(
            appModule,
            databaseModule,
            serviceModule
        ), logger = AndroidLogger())
    }
}