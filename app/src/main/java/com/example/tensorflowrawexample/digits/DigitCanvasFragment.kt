package com.example.tensorflowrawexample.digits

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tensorflowrawexample.R
import com.example.tensorflowrawexample.databinding.FragmentDigitCanvasBinding
import org.tensorflow.lite.task.vision.classifier.Classifications


class DigitCanvasFragment : Fragment(), DigitClassifierHelper.DigitClassifierListener {
    private var _fragmentDigitCanvasBinding: FragmentDigitCanvasBinding? = null
    private val fragmentDigitCanvasBinding get() = _fragmentDigitCanvasBinding!!

    private val digitClassifierHelper: DigitClassifierHelper by lazy {
        DigitClassifierHelper(requireContext(), this)
    }
    private lateinit var classificationResultAdapter: ClassificationResultsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentDigitCanvasBinding = FragmentDigitCanvasBinding.inflate(
            inflater,
            container, false
        )
        return fragmentDigitCanvasBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDigitCanvas()
        setupClassificationResultAdapter()

        fragmentDigitCanvasBinding.btnClear.setOnClickListener {
            fragmentDigitCanvasBinding.digitCanvas.clearCanvas()
            classificationResultAdapter.reset()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _fragmentDigitCanvasBinding = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDigitCanvas() {
        with(fragmentDigitCanvasBinding.digitCanvas) {
            setStrokeWidth(70f)
            setColor(Color.WHITE)
            setBackgroundColor(Color.BLACK)
            setOnTouchListener { _, event ->
                // As we have interrupted DrawView's touch event, we first
                // need to pass touch events through to the instance for the drawing to show up
                onTouchEvent(event)

                // Then if user finished a touch event, run classification
                if (event.action == MotionEvent.ACTION_UP) {
                    classifyDrawing()
                }
                true
            }
        }
    }

    private fun setupClassificationResultAdapter() {
        classificationResultAdapter = ClassificationResultsAdapter()
        with(fragmentDigitCanvasBinding.recyclerViewResults) {
            adapter = classificationResultAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun classifyDrawing() {
        val bitmap = fragmentDigitCanvasBinding.digitCanvas.getBitmap()
        digitClassifierHelper.classify(bitmap)
    }

    override fun onError(error: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            classificationResultAdapter.reset()
        }
    }

    override fun onResults(
        results: List<Classifications>?,
        inferenceTime: Long
    ) {
        requireActivity().runOnUiThread {
            classificationResultAdapter.updateResults(results)
            fragmentDigitCanvasBinding.tvInferenceTime.text = getString(R.string.inference_time, inferenceTime.toString())
        }
    }
}
