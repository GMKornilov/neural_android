package com.example.tensorflowrawexample.digits

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class DigitClassifierHelper(
    private val context: Context,
    private val digitClassifierListener: DigitClassifierListener?,
    private val threshold: Float = 0.5f,
    private val numThreads: Int = 2,
    private val maxResults: Int = 3,
    private val currentDelegate: Int = DELEGATE_NNAPI,
) {

    private var digitClassifier: ImageClassifier? = null

    init {
        setupDigitClassifier()
    }

    private fun setupDigitClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        when (currentDelegate) {
            DELEGATE_CPU -> {
                // Default
            }
            DELEGATE_GPU -> {
                if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                    baseOptionsBuilder.useGpu()
                } else {
                    digitClassifierListener?.onError(
                        "GPU is not supported on this device"
                    )
                }
            }
            DELEGATE_NNAPI -> {
                baseOptionsBuilder.useNnapi()
            }
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            digitClassifier =
                ImageClassifier.createFromFileAndOptions(
                    context,
                    "mnist.tflite",
                    optionsBuilder.build()
                )
        } catch (e: IllegalStateException) {
            digitClassifierListener?.onError(
                "Image classifier failed to initialize. See error logs for " +
                        "details"
            )
            Log.e(TAG, "TFLite failed to load model with error: ", e)
        }
    }

    fun classify(image: Bitmap) {
        if (digitClassifier == null) {
            setupDigitClassifier()
        }

        var inferenceTime = SystemClock.uptimeMillis()

        val tensorImage = TensorImage.fromBitmap(image)
        val results = digitClassifier?.classify(tensorImage)

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        digitClassifierListener?.onResults(results, inferenceTime)
    }

    interface DigitClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long
        )
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DELEGATE_NNAPI = 2

        private const val TAG = "DigitClassifierHelper"
    }
}
