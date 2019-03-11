package be.hogent.faith.domain.repository

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import io.reactivex.Flowable

interface EventRepository : Repository<Event>