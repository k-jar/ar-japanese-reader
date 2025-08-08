package com.example.arjapanesereader

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var selectImageButton: Button
    private lateinit var previewImageView: ImageView
    private lateinit var resultTextView: TextView

    // Initialize the Text Recognizer for Japanese
    private val recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())

    // Prepare the ActivityResultLauncher to handle the image selection
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            previewImageView.setImageURI(it) // Show the selected image in the ImageView
            processImageForText(it) // Start the OCR process
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Link the UI elements from the XML layout
        selectImageButton = findViewById(R.id.selectImageButton)
        previewImageView = findViewById(R.id.previewImageView)
        resultTextView = findViewById(R.id.resultTextView)

        // Set a click listener for the button
        selectImageButton.setOnClickListener {
            // Launch the image gallery
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun processImageForText(imageUri: Uri) {
        // Clear previous results
        resultTextView.text = "Processing..."

        try {
            // Create an InputImage object from the selected image's URI
            val inputImage = InputImage.fromFilePath(this, imageUri)

            // Process the image using the recognizer
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    // On success, update the TextView with the recognized text
                    resultTextView.text = visionText.text
                }
                .addOnFailureListener { e ->
                    // On failure, show an error message
                    resultTextView.text = "Failed to recognize text."
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: IOException) {
            e.printStackTrace()
            resultTextView.text = "Error loading image."
        }
    }
}