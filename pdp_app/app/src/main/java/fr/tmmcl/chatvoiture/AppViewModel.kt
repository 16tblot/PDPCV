package fr.tmmcl.chatvoiture
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class AppViewModel : ViewModel()
{
    val httpClient = HttpClient()
    private val userCertified : MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    fun setUserCertified(value: Boolean){
        userCertified.value = value;
    }
    fun isUserCertified(): LiveData<Boolean> {
        return userCertified;
    }
    //TODO: mettre les operations qui utilisent httpClient ici et utiliser des uistate?
    //https://developer.android.com/codelabs/basic-android-kotlin-training-viewmodel
}