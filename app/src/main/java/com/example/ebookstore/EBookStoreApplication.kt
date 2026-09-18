package com.example.ebookstore

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point required by Hilt.
 * Must be registered in AndroidManifest.xml via android:name=".EBookStoreApplication".
 */
@HiltAndroidApp
class EBookStoreApplication : Application()
