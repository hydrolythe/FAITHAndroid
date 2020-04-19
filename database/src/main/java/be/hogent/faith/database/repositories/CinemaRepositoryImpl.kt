package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseCinemaRepository
import be.hogent.faith.database.mappers.DetailMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.repository.CinemaRepository

class CinemaRepositoryImpl(
    userMapper: UserMapper,
    detailMapper: DetailMapper,
    firebaseDetailContainerRepository: FirebaseCinemaRepository
) : CinemaRepository, DetailContainerRepositoryImpl<Cinema>(
    userMapper,
    detailMapper,
    firebaseDetailContainerRepository
)