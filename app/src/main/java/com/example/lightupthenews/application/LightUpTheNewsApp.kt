package com.example.lightupthenews.application

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import com.example.lightupthenews.BuildConfig
import com.example.lightupthenews.user.UserViewModel
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.vk.api.sdk.VK
import timber.log.Timber

lateinit var app: Application
lateinit var reses: Resources
lateinit var sharedPreferences: SharedPreferences
lateinit var userViewModel: UserViewModel

class LightUpTheNewsApp : Application() {

    companion object {
        lateinit var instance: LightUpTheNewsApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        reses = resources
        app = requireNotNull(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        sharedPreferences = getSharedPreferences("currentUserID", MODE_PRIVATE)
        userViewModel = UserViewModel(app)

        Timber.i("created")

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        VK.initialize(applicationContext)
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
    }

}