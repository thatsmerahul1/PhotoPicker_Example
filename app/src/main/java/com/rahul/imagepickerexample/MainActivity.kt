package com.rahul.imagepickerexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.rahul.imagepickerexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private var isImageOnlySelected: Boolean = false
    private var isVideoOnlySelected: Boolean = false
    private var isImageAndVideoSelected: Boolean = true
    private var maxMultiMediaCount: Int = 5
    private lateinit var pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var pickSingleMedia: ActivityResultLauncher<PickVisualMediaRequest>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSinglePicker.setOnClickListener {
            getUpdatedCheckboxes()
            launchMediaPicker(isImageOnlySelected, isVideoOnlySelected, isImageAndVideoSelected, pickSingleMedia)
        }

        binding.btnMultiPicker.setOnClickListener {
            getUpdatedCheckboxes()
            launchMediaPicker(isImageOnlySelected, isVideoOnlySelected, isImageAndVideoSelected, pickMultipleMedia)
        }
        if (!ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(applicationContext)) {
            Toast.makeText(applicationContext, "Photo Picker is not available", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onStart() {
        super.onStart()
        pickSingleMedia = registerMultiMediaPickerContracts(binding, 1, false)
        pickMultipleMedia = registerMultiMediaPickerContracts(binding, maxMultiMediaCount, true)
    }

    private fun getUpdatedCheckboxes() {
        isImageOnlySelected = binding.checkBoxOnlyPhoto.isChecked
        isVideoOnlySelected = binding.checkBoxOnlyVideo.isChecked
        isImageAndVideoSelected = binding.checkBoxImageAndVideo.isChecked
    }

    private fun launchMediaPicker(isImageOnlySelected: Boolean, isVideoOnlySelected: Boolean, isImageAndVideoSelected: Boolean, mediaPickerContract: ActivityResultLauncher<PickVisualMediaRequest>) {
        if (isImageAndVideoSelected) {
            mediaPickerContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        } else if (isImageOnlySelected) {
            mediaPickerContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else if (isVideoOnlySelected) {
            mediaPickerContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        }

//        // Launch the photo picker and let the user choose only images/videos of a
//        // specific MIME type, such as GIFs.
//        val mimeType = "image/gif"
//        pickSingleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType)))
    }


    private fun registerMultiMediaPickerContracts(binding: ActivityMainBinding, maxMultiMediaCount: Int, isMultiMediaSelectionEnabled:Boolean): ActivityResultLauncher<PickVisualMediaRequest> {
        return when(isMultiMediaSelectionEnabled) {

                true -> registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxMultiMediaCount)) { uris ->
                    if (uris.isNotEmpty()) {
                        val stringBuffer = StringBuffer()
                        uris.forEach {
                            stringBuffer.append("${it.path} \n")
                        }
                        binding.textView.text = "Selected URI: $stringBuffer"
                    } else {
                        binding.textView.text = "No media selected"
                    }
                }
                else -> registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: $uri")
                        binding.textView.text = "Selected URI: ${uri.path}"
                    } else {
                        Log.d("PhotoPicker", "No media selected")
                        binding.textView.text = "No media selected"
                    }
                }
            }
    }

    override fun onStop() {
        pickSingleMedia.unregister()
        pickMultipleMedia.unregister()
        super.onStop()
    }
}