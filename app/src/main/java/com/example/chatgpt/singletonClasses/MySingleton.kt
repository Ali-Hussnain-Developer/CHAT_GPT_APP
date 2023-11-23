package com.example.chatgpt.singletonClasses

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class MySingleton private constructor(context: Context) {
    private var requestQueue: RequestQueue? = null

    init {
        Companion.context = context
        requestQueue = getRequestQueue()
    }

    companion object {
        private var instance: MySingleton? = null
        private var context: Context? = null

        @Synchronized
        fun getInstance(context: Context): MySingleton {
            if (instance == null) {
                instance = MySingleton(context)
            }
            return instance as MySingleton
        }
    }

    fun getRequestQueue(): RequestQueue {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context!!.applicationContext)
        }
        return requestQueue!!
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        getRequestQueue().add(req)
    }
}
