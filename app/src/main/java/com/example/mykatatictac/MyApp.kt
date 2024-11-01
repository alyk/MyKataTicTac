package com.example.mykatatictac

import android.app.Application

class MyApp : Application() {
    init {
        instance = this
    }

    companion object {
        var instance: MyApp? = null

        fun applicationContext(): MyApp {
            return instance as MyApp
        }
    }
}