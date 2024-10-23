package com.example.api_list

import android.app.Application
import com.example.api_list.database.DatabaseBuilder

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        init()

    }

    /**
     * fun para inicializar nossas depêndencias atrás do nosso context
     */
    private fun init() {
        DatabaseBuilder.getInstance(this)
    }
}