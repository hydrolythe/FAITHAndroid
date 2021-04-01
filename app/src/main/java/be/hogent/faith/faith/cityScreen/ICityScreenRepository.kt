package be.hogent.faith.faith.cityScreen

import be.hogent.faith.faith.VoidResult

interface ICityScreenRepository {
    suspend fun logout():VoidResult
}