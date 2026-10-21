package com.shushly.app.android

import android.app.Application

class ShushlyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: ShushlyApplication? = null
            private set
    }
}
