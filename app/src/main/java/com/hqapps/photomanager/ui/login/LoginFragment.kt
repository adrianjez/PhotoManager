package com.hqapps.photomanager.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.hqapps.photomanager.R
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    companion object {
        const val SHARED_PREFERENCES_KEY = "SharedPreferencesKey"
        const val PASSWORD_KEY = "PasswordKey"
        const val PASSWORD_MIN_LENGTH = 5
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        viewModel.passwordInputErrorStringId.observe(this, Observer(::onPasswordErrorStringSet))
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences =
            requireActivity().getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        sharedPreferences.getString(PASSWORD_KEY, null)?.let {
            loginButton.text = getString(R.string.login)
            loginButton.setOnClickListener {
                if (passwordContainer.text == it) {
                    passwordContainer.error = null
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToPhotosFragment()
                    )
                } else {
                    passwordContainer.error = getString(R.string.password_incorrect)
                }
            }
        } ?: run {
            loginButton.text = getString(R.string.set_password)
            loginButton.setOnClickListener {
                if (viewModel.verifyPasswordToSave(passwordContainer)) {
                    passwordContainer.error = null
                    sharedPreferences
                        .edit()
                        .putString(PASSWORD_KEY, passwordContainer.text.toString())
                        .apply()
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToPhotosFragment()
                    )
                } else {
                    passwordContainer.error = getString(R.string.password_requirements)
                }
            }
        }
    }

    private fun onPasswordErrorStringSet(passwordErrorStringResId: Int?){
        passwordErrorStringResId?.let {
            passwordContainer.error = getString(passwordErrorStringResId)
        } ?: run {
            passwordContainer.error = null
        }
    }
}