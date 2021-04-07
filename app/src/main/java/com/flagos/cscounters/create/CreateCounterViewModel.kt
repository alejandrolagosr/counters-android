package com.flagos.cscounters.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flagos.cscounters.helpers.NetworkHelper
import com.flagos.data.model.CounterItem
import com.flagos.data.repository.CountersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateCounterViewModel(
    private val countersRepository: CountersRepository,
    private val networkHelper: NetworkHelper
): ViewModel() {

    private var _onShowNoInternetDialog = MutableLiveData<Unit>()
    val onShowNoInternetDialog: LiveData<Unit>
        get() = _onShowNoInternetDialog

    private var _onShowError = MutableLiveData<String>()
    val onShowError: LiveData<String>
        get() = _onShowError

    private var _onCounterSaved = MutableLiveData<Unit>()
    val onCounterSaved: LiveData<Unit>
        get() = _onCounterSaved

    fun saveCounter(title: String) {
        if (networkHelper.isConnected()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    countersRepository.save(CounterItem(title = title))
                    _onCounterSaved.postValue(Unit)
                } catch (exception: Exception) {
                    _onShowError.postValue(exception.message.orEmpty())
                }
            }
        } else {
            _onShowNoInternetDialog.postValue(Unit)
        }
    }
}
