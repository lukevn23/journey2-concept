package ninja.luke.mobi.journey2.app.screen.single3

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Single3ViewModel : ViewModel() {

    private var _liveData: MutableLiveData<String?> = MutableLiveData(null)
    val liveData: LiveData<String?> get() = _liveData

    fun handleLongRunningProcess() {
        viewModelScope.launch {
            delay(3000)
            _liveData.postValue("Go")
        }
    }

}