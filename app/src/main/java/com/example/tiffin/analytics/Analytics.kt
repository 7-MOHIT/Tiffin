package com.example.tiffin.analytics

import android.util.Log

interface Analytics {
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
}

class LogcatAnalytics(private val tag: String = "TiffinAnalytics") : Analytics {
    override fun logEvent(name: String, params: Map<String, Any>) {
        val formattedParams = if (params.isEmpty()) "" else " | params=$params"
        Log.d(tag, "📊 [EVENT] $name$formattedParams")
    }
}
