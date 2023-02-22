package com.rizki.substoryapp.ui.main.createstories

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.rizki.substoryapp.R
import com.rizki.substoryapp.databinding.ActivityCreateStoriesBinding
import com.rizki.substoryapp.helper.ViewsModelFactory
import com.rizki.substoryapp.helper.reduceFileImages
import com.rizki.substoryapp.helper.rotateBitmap
import com.rizki.substoryapp.helper.uriToFiles
import com.rizki.substoryapp.preference.UsersPreference
import com.rizki.substoryapp.response.ResponseUploadFile
import com.rizki.substoryapp.retrofit.ConfigApi
import com.rizki.substoryapp.ui.main.CameraActivity
import com.rizki.substoryapp.ui.main.MainViewModel
import com.rizki.substoryapp.ui.main.stories.StoriesActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


private val Context.DataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class CreateStoriesActivity : AppCompatActivity() {

    private lateinit var createStoryViewModel: MainViewModel
    private lateinit var binding: ActivityCreateStoriesBinding
    private var getFiles: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.create_story)

        setViewModel()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener {
            startMyCamera()
        }

        binding.btnGallery.setOnClickListener {
            startMyGallery()
        }

        binding.btnUploadFile.setOnClickListener {
            uploadImg()
        }
    }

    private fun setViewModel() {
        createStoryViewModel = ViewModelProvider(
            this,
            ViewsModelFactory(UsersPreference.getInstances(DataStore))
        )[MainViewModel::class.java]
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startMyCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startMyGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val choose = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(choose)
    }

    private fun uploadImg() {
        showsLoading(true)

        if (getFiles != null) {
            val files = reduceFileImages(getFiles as File)

            val desc = binding.etDesc.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImgFile = files.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                files.name,
                requestImgFile
            )

            createStoryViewModel.getUsers().observe(this) {
                if(it != null) {
                    val client = ConfigApi.getServiceApi().uploadFile("Bearer " + it.Token, imageMultipart, desc)
                    client.enqueue(object: Callback<ResponseUploadFile> {
                        override fun onResponse(
                            call: Call<ResponseUploadFile>,
                            response: Response<ResponseUploadFile>
                        ) {
                            showsLoading(false)
                            val responsesBody = response.body()
                            Log.d(TAGS, "onResponse: $responsesBody")
                            if(response.isSuccessful && responsesBody?.Message == "Story created successfully") {
                                Toast.makeText(this@CreateStoriesActivity, getString(R.string.success_upload), Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@CreateStoriesActivity, StoriesActivity::class.java)
                                startActivity(intent)
                            } else {
                                Log.e(TAGS, "onFailure1: ${response.message()}")
                                Toast.makeText(this@CreateStoriesActivity, getString(R.string.failed_upload), Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseUploadFile>, t: Throwable) {
                            showsLoading(false)
                            Log.e(TAGS, "onFailure2: ${t.message}" )
                            Toast.makeText(this@CreateStoriesActivity, getString(R.string.failed_upload), Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

        }

    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFiles = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFiles = myFiles

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFiles.path),
                isBackCamera
            )

            binding.imgShow.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val myFiles = uriToFiles(selectedImage, this@CreateStoriesActivity)
            getFiles = myFiles
            binding.imgShow.setImageURI(selectedImage)
        }
    }

    private fun showsLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val TAGS = "CreateStoriesActivity"
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

    }
}