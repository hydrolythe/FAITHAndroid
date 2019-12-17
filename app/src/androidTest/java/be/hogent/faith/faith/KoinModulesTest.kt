package be.hogent.faith.faith

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.hogent.faith.database.di.databaseModule
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.service.usecases.di.serviceModule
import be.hogent.faith.storage.di.storageModule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.check.checkModules


@RunWith(AndroidJUnit4::class)
class KoinModulesTest : AutoCloseKoinTest() {


    @Test
    fun dryRunTest() {
        stopKoin()
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(
                listOf(
                    appModule,
                    databaseModule,
                    serviceModule,
                    storageModule, androidTestModule
                )
            )
        }.checkModules()
    }
}
