package com.school.coroutines

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityViewModel : ViewModel() {
    private val _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State>
        get() = _state

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.v("Error", throwable.message.toString())
    }

    private val scope = CoroutineScope(SupervisorJob() + exceptionHandler)

    init {
        refreshData()
    }

    private fun refreshData() {
        scope.launch {
            withContext(Dispatchers.IO) {
                val result = Repository.getPosts()
                if (result.isSuccessful) {
                    _state.postValue(result.body()?.let { State.Loaded(it) })
                } else {
                    _state.postValue(State.Loaded(emptyList()))
                }
            }
        }
    }

    fun processAction(action: Action) {
        when (action) {
            Action.RefreshData -> refreshData()
        }
    }
}
