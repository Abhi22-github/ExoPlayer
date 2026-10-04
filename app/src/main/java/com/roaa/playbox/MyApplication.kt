package com.roaa.playbox

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.VideoFrameDecoder

class MyApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        // TEMP: Crashlytics verification
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("crash_test", MODE_PRIVATE)
            if (!prefs.getBoolean("done", false)) {
                prefs.edit().putBoolean("done", true).commit()
                throw RuntimeException("Crashlytics test crash (release)")
            }
        }, 3000)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .crossfade(true)
            .build()
    }
}