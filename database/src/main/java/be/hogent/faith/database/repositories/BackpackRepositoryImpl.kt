package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseBackpackRepository
import be.hogent.faith.database.mappers.DetailMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.repository.BackpackRepository

class BackpackRepositoryImpl(
    userMapper: UserMapper,
    detailMapper: DetailMapper,
    firebaseDetailContainerRepository: FirebaseBackpackRepository
) : BackpackRepository, DetailContainerRepositoryImpl<Backpack>(
    userMapper,
    detailMapper,
    firebaseDetailContainerRepository
)