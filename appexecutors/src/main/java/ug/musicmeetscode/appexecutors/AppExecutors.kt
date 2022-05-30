package ug.musicmeetscode.appexecutors

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class AppExecutors private constructor(
    private val diskIO: Executor,
    private val networkIO: Executor,
    private val mainThread: Executor
) {
    fun diskIO(): Executor {
        return diskIO
    }

    fun mainThread(): Executor {
        return mainThread
    }

    fun networkIO(): Executor {
        return networkIO
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        private val LOCK = Any()

        @JvmStatic
        var instance: AppExecutors? = null
            get() {
                if (field == null) {
                    synchronized(LOCK) {
                        field = AppExecutors(
                            Executors.newSingleThreadExecutor(),
                            Executors.newFixedThreadPool(3),
                            MainThreadExecutor()
                        )
                    }
                }
                return field
            }
            private set
    }
}

class VolleySingleTon private constructor(private val context: Context) {
    private var requestQueue: RequestQueue?
    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.applicationContext)
        }
        return requestQueue
    }

    fun <T> addToRequestQueue(request: Request<T>?) {
        getRequestQueue()?.add(request)
    }

    companion object {
        @SuppressLint("StaticFieldLeak") private var instance: VolleySingleTon? = null

        @Synchronized fun getInstance(context: Context): VolleySingleTon? {
            if (instance == null) {
                instance = VolleySingleTon(context)
            }
            return instance
        }
    }

    init {
        requestQueue = getRequestQueue()
    }
}