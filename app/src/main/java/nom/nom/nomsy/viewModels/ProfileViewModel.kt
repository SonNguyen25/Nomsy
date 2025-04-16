package nom.nom.nomsy.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import nom.nom.nomsy.data.local.UserDatabase
import nom.nom.nomsy.data.local.entities.User
import nom.nom.nomsy.data.remote.UpdateProfileRequest
import nom.nom.nomsy.data.repository.AuthRepository
import nom.nom.nomsy.utils.Result
import kotlinx.coroutines.launch

class ProfileViewModel(app: Application) : AndroidViewModel(app), IProfileViewModel {
    private val repo = AuthRepository(userDatabase = UserDatabase.getDatabase(app))

    private val _profile = MutableLiveData<Result<User>>()
    override val profile: LiveData<Result<User>> = _profile

    private val _updateResult = MutableLiveData<Result<User>?>()
    override val updateResult: MutableLiveData<Result<User>?> = _updateResult

    override fun fetchByUsername(username: String) = viewModelScope.launch {
        repo.getProfileByUsername(username).observeForever { _profile.postValue(it) }
    }

    override fun updateProfile(username: String, req: UpdateProfileRequest) =
        viewModelScope.launch {
            repo.updateProfile(username, req).observeForever { result ->
                _updateResult.postValue(result)
                if (result is Result.Success) {
                    // immediately overwrite the "profile" LiveData with the new user
                    _profile.postValue(Result.Success(result.data))
                }
            }
        }

    override fun clearUpdateState() {
        _updateResult.value = null
    }
}

