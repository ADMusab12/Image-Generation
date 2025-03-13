package com.codetech.texttoimage.presentation

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.codetech.texttoimage.R
import com.codetech.texttoimage.abstraction.HuggingFaceImageGenerator
import com.codetech.texttoimage.abstraction.saveImageToGallery
import com.codetech.texttoimage.databinding.ActivityFluxBinding
import com.codetech.texttoimage.util.Extension.gone
import com.codetech.texttoimage.util.Extension.loadImage
import com.codetech.texttoimage.util.Extension.showMessage
import com.codetech.texttoimage.util.Extension.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FluxActivity : AppCompatActivity() {
    private val binding by lazy { ActivityFluxBinding.inflate(layoutInflater) }
    private lateinit var imageGenerator: HuggingFaceImageGenerator
    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 100
    private val TAG = "FluxActivityInfo"
    private val prompts = listOf(
        "Futuristic cyberpunk cityscape at dusk",
        "A magical forest with glowing mushrooms",
        "A post-apocalyptic wasteland with a lone wanderer",
        "A spaceship landing on an alien planet",
        "A surreal desert with floating islands",
        "A cozy cabin in the woods during winter"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        imageGenerator = HuggingFaceImageGenerator()

        addPromptsToUI()
        checkPermission()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: Permission granted")
            } else {
                Log.d(TAG, "onRequestPermissionsResult: Permission denied")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.buttonGenerate.setOnClickListener { onButtonClickGenerate() }
        binding.imageFlux.setOnClickListener { showDownloadDialog(binding.imageFlux,"Flux${System.currentTimeMillis()}") }
        binding.imageDiffusion.setOnClickListener { showDownloadDialog(binding.imageDiffusion,"Diffusion${System.currentTimeMillis()}") }
        binding.imagePresident.setOnClickListener { showDownloadDialog(binding.imagePresident,"President${System.currentTimeMillis()}") }
    }

    private fun addPromptsToUI() {
        for (prompt in prompts) {
            val textView = TextView(this).apply {
                text = prompt
                setPadding(16, 10, 16, 10)
                maxLines = 1
                setBackgroundResource(R.drawable.prompt_background)
                setOnClickListener {
                    binding.etPrompt.setText(prompt)
                }
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(5, 5, 5, 5)
                }
            }
            binding.promptContainer.addView(textView)
        }
    }

    private fun onButtonClickGenerate() {
        val prompt = binding.etPrompt.text.toString()
        if (prompt.isNotEmpty()) {
            binding.buttonGenerate.gone()
            generateImage(prompt)
        } else {
            showMessage("Please enter prompt")
        }
    }

    private fun generateImage(prompt: String) {
        lifecycleScope.launch {
            try {
                progressVisible()

                val image1Flux = async { imageGenerator.generateImage(prompt = prompt) }
                val image2President = async { imageGenerator.generatePresidentImage(prompt = prompt) }
                val image3Diffusion = async { imageGenerator.generateStableDiffusionImage(prompt = prompt) }

                val image1 = image1Flux.await()
                val image2 = image2President.await()
                val image3 = image3Diffusion.await()

                image1.let {
                    if (it != null) {
                        loadImage(
                            imageView = binding.imageFlux,
                            imageUrl = it,
                            progressBar = binding.progressFlux,
                            textView = binding.textFlux,
                            view = binding.view
                        )
                    }
                }
                image2.let {
                    if (it != null) {
                        loadImage(
                            imageView = binding.imagePresident,
                            imageUrl = it,
                            progressBar = binding.progressPresident,
                            textView = binding.textPresident,
                            view = binding.view1
                        )
                    }
                }
                image3.let {
                    if (it != null) {
                        loadImage(
                            imageView = binding.imageDiffusion,
                            imageUrl = it,
                            progressBar = binding.progressDiffusion,
                            textView = binding.textDiffusion,
                            view = binding.view2
                        )
                    }
                }

            }catch (e:Exception){
                Log.e(TAG, "generateImage: Exception ${e.message}")
            }finally {
                binding.buttonGenerate.visible()
            }
        }
    }

    private fun progressVisible(){
        binding.progressFlux.visible()
        binding.progressDiffusion.visible()
        binding.progressPresident.visible()
    }

    private fun showDownloadDialog(imageView: ImageView, title: String) {
        val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap
        if (bitmap != null) {
            AlertDialog.Builder(this)
                .setTitle("Download Image")
                .setMessage("Do you want to download this image in HD?")
                .setPositiveButton("Download") { _, _ ->
                    val savedUri = saveImageToGallery(this, bitmap, title)
                    if (savedUri != null) {
                        showSnackBar(savedUri)
                    } else {
                        showMessage("Failed to save image")
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            showMessage("No image to download")
        }
    }

    private fun showSnackBar(imageUri: Uri) {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            "Image saved to gallery",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("View") {
            openImageInGallery(imageUri)
        }
        snackBar.show()
    }

    private fun openImageInGallery(imageUri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(imageUri, "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(intent, "Open image with...")

            try {
                startActivity(chooserIntent)
            } catch (e: ActivityNotFoundException) {
                showMessage("No app found to open the image.")
            }
        } catch (e: Exception) {
            Log.e("ImageViewer", "Error opening image", e)
            showMessage("Error opening image: ${e.message}")
        }
    }
}