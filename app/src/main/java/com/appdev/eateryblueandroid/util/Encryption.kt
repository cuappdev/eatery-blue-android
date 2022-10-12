package com.appdev.eateryblueandroid.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private val cipher = Cipher.getInstance("AES/GCM/NoPadding")
private val keyGenerator =
    KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

private val utf8 by lazy {
    Charsets.UTF_8
}

private fun makeSecretKey(alias: String): SecretKey {
    return keyGenerator.apply {
        init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
    }.generateKey()

}

private fun getSecretKey(alias: String) =
    (keyStore.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey

fun encryptData(alias: String, data: String): String {
    cipher.init(Cipher.ENCRYPT_MODE, makeSecretKey(alias))
    Log.i("CipherTest", "My IV: " + String(cipher.iv, utf8))
    // Encodes the data to string, then returns.
    return Base64.encodeToString(
        cipher.iv,
        Base64.NO_WRAP
    ) + "||" + Base64.encodeToString(cipher.doFinal(data.toByteArray(utf8)), Base64.NO_WRAP)
}

fun decryptData(alias: String, data: String): String {
    // Takes in data, gets the IV as a ByteArray.
    val ivString = Base64.decode(data.substring(0, data.indexOf("||")), Base64.NO_WRAP)

    // Get the actually encrypted part as a ByteArray.
    val encryptedData = Base64.decode(data.substring(data.indexOf("||") + 2), Base64.NO_WRAP)

    cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), GCMParameterSpec(128, ivString))
    return cipher.doFinal(encryptedData).toString(utf8)
}
