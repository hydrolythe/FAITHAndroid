package be.hogent.faith.storage

import android.content.Context
import android.content.SharedPreferences
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.io.File
import java.util.UUID
import java.util.concurrent.Callable

class DummyStorageRepository(
    private val context: Context,
    private val storagePathProvider: StoragePathProvider
) : IDummyStorageRepository{

    private val STORAGE = "be.hogent.faith.service.usecases.BACKPACK"
    private var preferences: SharedPreferences? = null
    private var details = ArrayList<DummyDetail>()

    override fun getBackpackDetailsUserTestData() : List<Detail> {
        details = ArrayList()
        var hulpList = ArrayList<Detail>()
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences!!.getString("backpackDetail", null)
        val type=
            object : TypeToken<ArrayList<DummyDetail?>?>() {}.type
        try{
            details = gson.fromJson(json, type)
        }
        catch (e : Exception){

        }

        hulpList = DummyDetailMapper.mapFromEntities(details) as ArrayList<Detail>
        return hulpList
    }

    override fun storeDetail(detail: Detail): Completable {
        var dummyDetail = DummyDetailMapper.mapToEntity(detail)
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences!!.edit()
        val gson = Gson()
        val jsonGet = preferences!!.getString("backpackDetail", null)
        val type=
            object : TypeToken<ArrayList<DummyDetail?>?>() {}.type
        try{
            details = gson.fromJson(jsonGet, type)
        }
        catch (e : Exception){

        }
        editor.clear()
        details.add(dummyDetail)
        val json: String? = gson.toJson(details)
        editor.putString("backpackDetail", json)
        editor.apply()
        return Completable.fromCallable { true }
    }

}