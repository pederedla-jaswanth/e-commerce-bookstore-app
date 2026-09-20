package com.example.ebookstore

import android.app.Application
import com.example.ebookstore.data.local.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point required by Hilt.
 * Must be registered in AndroidManifest.xml via android:name=".EBookStoreApplication".
 *
 * On first launch [DatabaseSeeder.seedIfEmpty] populates the orders table with
 * 5 demo orders so the Orders screen is immediately populated without needing
 * a real checkout flow. The seeder is a no-op on subsequent launches.
 */
@HiltAndroidApp
class EBookStoreApplication : Application() {

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    override fun onCreate() {
        super.onCreate()
        databaseSeeder.seedIfEmpty()
    }
}
