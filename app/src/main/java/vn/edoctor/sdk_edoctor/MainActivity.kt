package vn.edoctor.sdk_edoctor

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    companion object {
        var eDoctor: EDoctor? = null
    }
    lateinit var textOpen: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eDoctor = EDoctor(this, supportFragmentManager)
        setContentView(R.layout.activity_main)
        textOpen = findViewById(R.id.text_open)
        textOpen.setOnClickListener{
            eDoctor?.openWebView(onSuccess = {
                Log.d(EDoctor.LOG_TAG, "onSuccess")
            }, onError = { _, _, s ->
                EDoctor.showError(s)
            })
        }
    }
}