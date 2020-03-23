package com.example.flutter_onfido

import android.content.Intent
import android.widget.Toast
import androidx.annotation.NonNull;
import com.onfido.android.sdk.capture.ExitCode
import com.onfido.android.sdk.capture.Onfido
import com.onfido.android.sdk.capture.OnfidoConfig
import com.onfido.android.sdk.capture.OnfidoFactory
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.upload.Captures
import com.onfido.api.client.token.mobile.MobileToken
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import java.lang.Exception

class MainActivity: FlutterActivity()  {

    private var onfidoClient: Onfido? = null

    companion object {
        const val CHANNEL = "com.example.flutter_onfido/init"
    }

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler{
            call, result ->  when {
            call.method == "init" -> init(result)
            call.method == "start" -> start(call.argument("mobileToken")!!, call.argument("applicantId")!!, result)
        }
        }
    }


    private fun init(result: MethodChannel.Result) {
        try {
            onfidoClient = OnfidoFactory.create(context).client

            result.success(true)
        } catch (e: Exception) {
            result.error("OnfidoInitializingFailed", "Failed to initialize the onfido!", e)
        }

    }

    private fun start(mobileToken: String, applicantId: String, result: MethodChannel.Result) {
        try {
            val config = OnfidoConfig.Builder(this)
                    .withToken(mobileToken)
                    .withApplicant(applicantId)

            onfidoClient?.startActivityForResult(this, 1, config.build())
            result.success(true)
        } catch (e: Exception) {
            result.error("OpeningFlowFailed", "Failed to open the onfido flow!", e)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onfidoClient?.handleActivityResult(resultCode, data, object : Onfido.OnfidoResultListener {
            override fun userCompleted(captures: Captures) {
                Toast.makeText(context, "Complete!", Toast.LENGTH_LONG)
            }

            override fun userExited(exitCode: ExitCode) {
                Toast.makeText(context, "User exits!", Toast.LENGTH_LONG)
            }

            override fun onError(exception: OnfidoException) {
                exception.printStackTrace()
                Toast.makeText(context, "Unknown Error!", Toast.LENGTH_LONG)
            }
        })
    }
}
