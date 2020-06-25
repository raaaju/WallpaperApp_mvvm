package com.georgcantor.wallpaperapp.view.fragment.videos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.repository.ApiRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class VideosViewModel(private val repository: ApiRepository) : ViewModel() {

    private val disposable = CompositeDisposable()

    val isProgressVisible = MutableLiveData<Boolean>().apply { this.value = true }
    val videos = MutableLiveData<List<CommonPic>>()

    fun getVideos(playlistId: String) {
        disposable.add(
            Observable.fromCallable {
                repository.getVideos(playlistId).subscribe({
                    val vidList = mutableListOf<CommonPic>()
                    it.body()?.items?.map {
                        vidList.add(
                            CommonPic(
                                it.snippet?.thumbnails?.standard?.url ?: "",
                                it.snippet?.thumbnails?.standard?.width?.toInt() ?: 0,
                                it.snippet?.thumbnails?.standard?.height?.toInt() ?: 0,
                                it.etag,
                                it.snippet?.thumbnails?.standard?.url ?: "",
                                it.snippet?.thumbnails?.standard?.url ?: "",
                                it.hashCode(),
                                it.snippet?.resourceId?.videoId
                            )
                        )
                    }
                    videos.postValue(vidList)
                }, {
                })
            }
                .subscribeOn(Schedulers.io())
                .subscribe()
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}