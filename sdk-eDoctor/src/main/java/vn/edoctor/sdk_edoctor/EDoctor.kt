package vn.edoctor.sdk_edoctor

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import org.json.JSONObject

class EDoctor {
    private val eDoctorWebView = EDoctorWebView()
    companion object {
        val LOG_TAG = "EDOCTOR_SDK"
        internal lateinit var context: Context
        internal lateinit var fragmentManager: FragmentManager
        fun showError(message: String?) {
            if (message != null && message != "null" && message != "") {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    constructor(
        context: Context,
        fragmentManager: FragmentManager
        ) {
        EDoctor.context = context
        EDoctor.fragmentManager = fragmentManager
    }

    constructor() {

    }

    fun showError(message: String?) {
        if (message != null && message != "null" && message != "") {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    fun openWebView(
        onSuccess: (JSONObject?) -> Unit,
        onError: (JSONObject?, Int, String?) -> Unit
    ) {
        eDoctorWebView.show(fragmentManager, null)
    }
}