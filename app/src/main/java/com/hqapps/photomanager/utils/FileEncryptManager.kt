package com.hqapps.photomanager.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Environment
import android.util.Base64
import java.io.*
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object FileEncryptManager {

    private const val sharedPrefKey = "PREF_KEY"
    private const val encryptedFilesFolderName = "ENCRYPTED"
    private const val tempFileName = "TEMP.jpg"

    fun getEncryptedFiles(context: Context): Array<File> {
        return getFile(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
                    + File.separator + encryptedFilesFolderName + File.separator
        ).listFiles() ?: arrayOf()
    }

    fun decrypt(filePath: String, context: Context): ByteArray {
        val fileData = readFile(filePath)
        val secretKey = getSecretKey(getSharedPreferencesForEncryptManager(context))
        return decrypt(secretKey, fileData)
    }

    @Throws(Exception::class)
    fun encryptBitmap(
        bitmap: Bitmap, filePath: String,
        pathToFiles: String,
        context: Context
    ) {
        //Override file content to get rid of exif data
        val fileOutputStream = FileOutputStream(filePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()

        var fileData = readFile(filePath)
        val secretKey = getSecretKey(getSharedPreferencesForEncryptManager(context))
        val encodedData = encrypt(secretKey, fileData)

        saveFile(encodedData, filePath)

        copyFileToEncryptedFolder(filePath, pathToFiles)

        deleteFile(filePath)


    }

    @Throws(IOException::class)
    fun getTempFile(context: Context): File {
        val tempFile =
            getFile(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
                        + File.separator.toString() + tempFileName
            )
        if (tempFile.exists()) {
            tempFile.delete()
        }
        tempFile.createNewFile()

        return tempFile
    }


    private fun deleteFile(filePath: String) {
        val file = File(filePath)
        file.delete()
    }

    private fun copyFileToEncryptedFolder(pathToFile: String, pathToEncryptedFolder: String) {
        val file = File(pathToFile)
        val outputFilePath = pathToEncryptedFolder + File.separator + encryptedFilesFolderName
        //create output directory if it doesn't exist
        val dir = File(outputFilePath)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())

        var inputStream: FileInputStream = file.inputStream()
        var outputStream: OutputStream =
            FileOutputStream(outputFilePath + File.separator + "PHOTO" + timeStamp + ".jpg")
        val buffer = ByteArray(1024)

        var length = inputStream.read(buffer)

        while (length > 0) {
            outputStream.write(buffer, 0, length)
            length = inputStream.read(buffer)
        }

        inputStream.close()

        outputStream.flush()
        outputStream.close()
    }

    @Throws(Exception::class)
    private fun saveFile(fileData: ByteArray, path: String) {
        val file = File(path)
        val bos = BufferedOutputStream(FileOutputStream(file, false))
        bos.write(fileData)
        bos.flush()
        bos.close()
    }

    @SuppressLint("GetInstance")
    @Throws(Exception::class)
    private fun encrypt(yourKey: SecretKey, fileData: ByteArray): ByteArray {
        val data = yourKey.encoded
        val skeySpec = SecretKeySpec(data, 0, data.size, "AES")
        val cipher = Cipher.getInstance("AES", "BC")
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, IvParameterSpec(ByteArray(cipher.blockSize)))
        return cipher.doFinal(fileData)
    }


    private fun getFile(fromPath: String?): File {
        return File(fromPath)
    }

    private fun getSecretKey(sharedPref: SharedPreferences): SecretKey {

        val key = sharedPref.getString("AppConstants.secretKeyPref", null)

        if (key == null) {
            //generate secure random
            val secretKey = generateSecretKey()
            saveSecretKey(sharedPref, secretKey!!)
            return secretKey
        }

        val decodedKey = Base64.decode(key, Base64.NO_WRAP)

        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }

    @Throws(Exception::class)
    private fun readFile(filePath: String): ByteArray {
        val file = File(filePath)
        val fileContents = file.readBytes()
        val inputBuffer = BufferedInputStream(
            FileInputStream(file)
        )

        inputBuffer.read(fileContents)
        inputBuffer.close()

        return fileContents
    }


    @Throws(Exception::class)
    private fun generateSecretKey(): SecretKey? {
        val secureRandom = SecureRandom()
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator?.init(128, secureRandom)
        return keyGenerator?.generateKey()
    }

    @SuppressLint("GetInstance")
    @Throws(Exception::class)
    private fun decrypt(yourKey: SecretKey, fileData: ByteArray): ByteArray {
        val decrypted: ByteArray
        val cipher = Cipher.getInstance("AES", "BC")
        cipher.init(Cipher.DECRYPT_MODE, yourKey, IvParameterSpec(ByteArray(cipher.blockSize)))
        decrypted = cipher.doFinal(fileData)
        return decrypted
    }

    private fun saveSecretKey(sharedPref: SharedPreferences, secretKey: SecretKey): String {
        val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.NO_WRAP)
        sharedPref.edit().putString("AppConstants.secretKeyPref", encodedKey).apply()
        return encodedKey
    }

    private fun getSharedPreferencesForEncryptManager(context: Context): SharedPreferences {
        return context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
    }


}