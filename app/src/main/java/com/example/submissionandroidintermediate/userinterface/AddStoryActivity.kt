package com.example.submissionandroidintermediate.userinterface

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.submissionandroidintermediate.R
import com.example.submissionandroidintermediate.UserPreferences
import com.example.submissionandroidintermediate.databinding.ActivityAddStoryBinding
import com.example.submissionandroidintermediate.viewmodel.AddStoryViewModel
import com.example.submissionandroidintermediate.viewmodel.UserLoginViewModel
import com.example.submissionandroidintermediate.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var token: String

    private var getFile: File? = null

    private val addStoryViewModel: AddStoryViewModel by lazy {
        ViewModelProvider(this)[AddStoryViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.post_users)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ifClicked()

        val preferences = UserPreferences.getInstance(dataStore)
        val userLoginViewModel =
            ViewModelProvider(this, ViewModelFactory(preferences))[UserLoginViewModel::class.java]

        userLoginViewModel.getToken().observe(this) {
            token = it
        }

        userLoginViewModel.getName().observe(this) {
            binding.tvUsers.text = StringBuilder(getString(R.string.post_as)).append(" ").append(it)
        }

        addStoryViewModel.message.observe(this) {
            showToast(it)
        }

        addStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun ifClicked() {
        binding.btnPostStory.setOnClickListener {

            if (getFile == null) {
                showToast(resources.getString(R.string.warningAddImage))
                return@setOnClickListener
            }

            val des = binding.tvDes.text.toString().trim()
            if (des.isEmpty()) {
                binding.tvDes.error = resources.getString(R.string.warningAddDescription)
                return@setOnClickListener
            }

            val file = getFile as File
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val desPart = des.toRequestBody("text/plain".toMediaType())

            addStoryViewModel.upload(imageMultipart, desPart, token)
        }

        binding.cameraButton.setOnClickListener {
            startTakePhoto()
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }
    }

    private var anyPhoto = false
    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            anyPhoto = true
            binding.imageStoryUpload.setImageBitmap(result)
            binding.tvDes.requestFocus()
        }
    }

    private val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                getString(R.string.package_name),
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.imageStoryUpload.setImageURI(selectedImg)
            binding.tvDes.requestFocus()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun showToast(msg: String) {
        Toast.makeText(
            this@AddStoryActivity,
            StringBuilder(getString(R.string.message)).append(msg),
            Toast.LENGTH_SHORT
        ).show()

        if (msg == "Story created successfully") {
            val intent = Intent(this@AddStoryActivity, HomePageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarAddStory.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val FILENAME_FORMAT = "MMddyyyy"
    }
}