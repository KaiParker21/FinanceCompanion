package com.skye.financecompanion

import android.app.Application
import com.skye.financecompanion.di.AppContainer
import com.skye.financecompanion.di.DefaultAppContainer

class FinanceApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}