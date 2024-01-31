package com.example.sciflare_interview.model.utills

import android.R
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class Utils {
    companion object{
        lateinit var  progressAlertDialog: AlertDialog

        fun encryptAES(plainText: String, key: String,context: Context): String? {
            return try {
                val cipher = Cipher.getInstance(getGradleProperty(context, context.resources.getString(
                    com.example.sciflare_interview.R.string.encryption_mode)))
                val secretKeySpec = SecretKeySpec(key.toByteArray(), getGradleProperty(context, context.resources.getString(
                    com.example.sciflare_interview.R.string.algorithm)))
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
                val encryptedBytes = cipher.doFinal(plainText.toByteArray())
                return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
            } catch (e: Exception){
                null
            }
        }

        fun decryptAES(encryptedText: String, key: String,context: Context): String? {
            return try {
                val cipher = Cipher.getInstance(getGradleProperty(context, context.resources.getString(
                    com.example.sciflare_interview.R.string.encryption_mode)))
                val secretKeySpec = SecretKeySpec(key.toByteArray(), getGradleProperty(context, context.resources.getString(
                    com.example.sciflare_interview.R.string.algorithm)))
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
                val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
                val decryptedBytes = cipher.doFinal(encryptedBytes)
                return String(decryptedBytes)
            }catch (e: Exception){
                null
            }
        }

        fun createProgressDialog(currentActivity: AppCompatActivity): AlertDialog {
            val vLayout = LinearLayout(currentActivity)
            vLayout.orientation = LinearLayout.VERTICAL
            vLayout.setPadding(50, 50, 50, 50)
            vLayout.addView(ProgressBar(currentActivity, null, R.attr.progressBarStyleSmallTitle))
            return AlertDialog.Builder(currentActivity)
                .setCancelable(false)
                .setView(vLayout)
                .create()
        }

        fun displayProgressDialog() {
            if (!progressAlertDialog.isShowing) {
                progressAlertDialog.show()
            }
        }

        fun hideProgressDialog() {
            progressAlertDialog.dismiss()
        }

        private fun getApplicationInfo(context: Context): ApplicationInfo {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getApplicationInfo(
                    context.packageName,
                    PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
                )
            } else {
                context.packageManager.getApplicationInfo(
                    context.packageName,
                    PackageManager.GET_META_DATA
                )
            }
        }

        fun getGradleProperty(context: Context, propertyKey: String): String {
            return try {
                val applicationInfo = getApplicationInfo(context)
                applicationInfo.metaData.getString(propertyKey) ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }
}