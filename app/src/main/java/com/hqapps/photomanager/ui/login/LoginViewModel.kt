package com.hqapps.photomanager.ui.login

import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.hqapps.photomanager.R
import com.hqapps.photomanager.base.BaseViewModel

class LoginViewModel : BaseViewModel() {


    val passwordInputErrorStringId = MutableLiveData<Int?>()

    fun verifyPasswordToSave(passwordContainer: EditText): Boolean {
        val result = passwordContainer.text.isNotEmpty() &&
                (passwordContainer.text.length > LoginFragment.PASSWORD_MIN_LENGTH)

        passwordInputErrorStringId.postValue(if(result) null else R.string.password_requirements)
        return result
    }
}