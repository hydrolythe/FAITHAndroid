package be.hogent.faith.faith

import be.hogent.faith.faith.util.TempFileProvider
import org.koin.dsl.module

val testModule = module(override = true) {
    single { TestFileProvider() as TempFileProvider }
}