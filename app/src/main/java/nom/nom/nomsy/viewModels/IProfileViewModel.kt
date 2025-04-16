package nom.nom.nomsy.viewModels

import androidx.lifecycle.LiveData
import nom.nom.nomsy.data.local.entities.User
import nom.nom.nomsy.data.remote.UpdateProfileRequest
import nom.nom.nomsy.utils.Result

interface IProfileViewModel {
    val profile: LiveData<Result<User>>
    val updateResult: LiveData<Result<User>?>
    fun fetchByUsername(username: String): kotlinx.coroutines.Job
    fun updateProfile(username: String, req: UpdateProfileRequest): kotlinx.coroutines.Job
    fun clearUpdateState()
}