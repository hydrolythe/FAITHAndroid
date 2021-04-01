package be.hogent.faith.faith.models

import android.os.Parcelable
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.detail.FilmDetail
import kotlinx.android.parcel.Parcelize
import java.util.UUID

/**
 * Represents a container of details (duh).
 * The backpack, cinema,... all share the functionality of displaying a collection of details.
 *
 * The event is different from the DetailsContainer because there are multiple events, but there
 * is always only one Backpack, Cinema,... This has consequences from the service layer and beyond,
 * where for example encryption and storage has to be handled differently.
 */
@Parcelize
open class DetailsContainer:Parcelable {

    private var _details = mutableListOf<Detail>()
    val details: List<Detail>
        get() = _details

    fun get(uuid: UUID): Detail? {
        return details.find { it.uuid == uuid }
    }

    fun addDetail(detail: Detail) {
        _details.add(detail)
    }

    fun removeDetail(detail: Detail) {
        _details.remove(detail)
    }

    fun setDetails(details: List<Detail>) {
        _details = details.toMutableList()
    }
}
@Parcelize
class Backpack : DetailsContainer()
@Parcelize
class TreasureChest : DetailsContainer()
@Parcelize
class Cinema : DetailsContainer() {

    fun getFilm(uuid: UUID): FilmDetail? {
        return films.find { it.uuid == uuid }
    }

    val films: List<FilmDetail>
        get() = details.filterIsInstance<FilmDetail>()

    fun getFiles(): List<Detail> {
        return details.filter { it !is FilmDetail }
    }

    fun addFilm(film: FilmDetail) {
        addDetail(film)
    }

    fun removeFilm(film: FilmDetail) {
        removeDetail(film)
    }
}
