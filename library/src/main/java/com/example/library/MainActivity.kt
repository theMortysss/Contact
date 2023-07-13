package com.example.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.library.utils.Constants
import com.yandex.mapkit.MapKitFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(Constants.MAPKIT_API_KEY)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
