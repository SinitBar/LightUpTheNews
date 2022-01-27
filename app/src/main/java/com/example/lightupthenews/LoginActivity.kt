package com.example.lightupthenews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.lightupthenews.application.reses
import com.example.lightupthenews.application.userViewModel
import com.example.lightupthenews.databinding.ActivityLoginBinding
import com.example.lightupthenews.network.ImageRequester
import com.example.lightupthenews.user.NOT_LOGGED
import com.example.lightupthenews.user.User
import com.example.lightupthenews.user.UserStatusLogged
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKAuthException
import timber.log.Timber

private lateinit var binding: ActivityLoginBinding
private var inVk = false
private var inFacebook = false
private val facebookCallbackManager = CallbackManager.Factory.create()

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        Timber.i("created")

        if (userViewModel.currentUserId != NOT_LOGGED) {
            if (User.get(userViewModel.currentUserId).logged == UserStatusLogged.IN_VK) {
                inVk = true
                binding.buttonLoginVk.text = getString(R.string.log_out)
            } else if (User.get(userViewModel.currentUserId).logged == UserStatusLogged.IN_FACEBOOK) {
                inFacebook = true
            }
        }

        binding.buttonContinueNotLogged.setOnClickListener {
            userViewModel.logOut()
            userViewModel.doNotLogIn = true
            finish()
        }

        binding.buttonLoginVk.setOnClickListener {
            userViewModel.logOut()
            inVk = !inVk
            if (inVk) {
                Timber.i("clicked to log in vk")
                VK.login(this, arrayListOf(VKScope.WALL, VKScope.PHOTOS))
            } else {
                Timber.i("clicked to log out of vk")
                binding.buttonLoginVk.text = getString(R.string.continue_with_vk)
            }
        }

        if (inVk || inFacebook)
            binding.buttonContinue.visibility = View.VISIBLE

        binding.buttonContinue.setOnClickListener { finish() }

        val facebookLoginButton: LoginButton = binding.buttonLoginFacebook
        facebookLoginButton.setOnClickListener {
            userViewModel.logOut()
            inFacebook = !inFacebook
            if (inFacebook) {
                facebookLoginButton.registerCallback(
                    facebookCallbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onCancel() {
                            inFacebook = false
                            binding.loggedInfo.text = reses.getText(R.string.not_logged)
                        }

                        override fun onError(error: FacebookException) {
                            inFacebook = false
                            binding.loggedInfo.text = reses.getText(R.string.not_logged)
                            userViewModel.logOut()
                        }

                        override fun onSuccess(result: LoginResult) {
                            Timber.i("onSuccessFacebook called")
                            val userId = result.accessToken.userId
                            if (User.getUsers().isNotEmpty()) {
                                if (userViewModel.currentUserId != NOT_LOGGED) {
                                    if (User.getUsers()[userViewModel.currentUserId].loginId == userId) {
                                        Timber.i("user is logging out of facebook")
                                        binding.loggedInfo.text = reses.getText(R.string.not_logged)
                                        return
                                    }
                                }
                            }
                            binding.loggedInfo.text = "User id: ${userId}"
                            val profileImageUrl =
                                "https://graph.facebook.com/" + result.accessToken.userId +
                                        "/picture?return_ssl_resources=1"
                            ImageRequester.setImageFromUrl(binding.imgLoggedIn, profileImageUrl)
                            User.addOrLoginUser(
                                User(
                                    loginId = userId,
                                    imageUrl = profileImageUrl,
                                    logged = UserStatusLogged.IN_FACEBOOK
                                )
                            )
                            Timber.i("addOrLoginUser in Facebook, currentUserId: ${userViewModel.currentUserId}")
                            finish()
                        }
                    }
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.i("onActivityResult called")
        if (inVk) {
            val callback = object : VKAuthCallback {
                override fun onLogin(token: VKAccessToken) {
                    val userId = token.userId
                    Timber.i("for vk userId is $userId")
                    val profileImageUrl =
                        "https://api.vk.com/method/users.get?user_id=$userId&v=5.131"
                    ImageRequester.setImageFromUrl(binding.imgLoggedIn, profileImageUrl)
                    User.addOrLoginUser(
                        User(
                            loginId = userId.toString(),
                            imageUrl = profileImageUrl,
                            logged = UserStatusLogged.IN_VK
                        )
                    )
                    Timber.i("addOrLoginUser in VK, currentUserId: ${userViewModel.currentUserId}")
                    binding.buttonLoginVk.text = getString(R.string.log_out)
                    finish()
                }

                override fun onLoginFailed(authException: VKAuthException) {
                    TODO("Not yet implemented")
                }
            }
            if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
                Timber.i("data was null if onActivityResult was true")
                super.onActivityResult(requestCode, resultCode, data)
            }

        } else if (inFacebook) {
            super.onActivityResult(requestCode, resultCode, data)
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }


}