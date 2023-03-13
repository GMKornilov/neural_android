package com.example.tensorflowrawexample

import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.tensorflowrawexample.databinding.FragmentSimpleModelBinding
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class SimpleModelFragment : Fragment() {
    private var _binding: FragmentSimpleModelBinding? = null
    private val binding get() = _binding!!

    private lateinit var tflite: Interpreter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimpleModelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val buffer = loadModelFile(requireActivity().assets, "simple_model.tflite")
            tflite = Interpreter(buffer)
        } catch (e: Exception) {
            Log.e("Tflite", "Error initializing Interpreter", e)
        }

        binding.computeButton.setOnClickListener {
            calculate()
        }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): ByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun calculate() {
        binding.resultText.text = getString(R.string.compute_loading_label)

        val x1 = binding.x1Input.editText!!.text.toString().toFloat()
        val x2 = binding.x2Input.editText!!.text.toString().toFloat()

        val inputValue = floatArrayOf(x1, x2)

        val outputValue = ByteBuffer.allocateDirect(4)
        outputValue.order(ByteOrder.nativeOrder())

        tflite.run(inputValue, outputValue)

        outputValue.rewind()
        val result = outputValue.float

        AlertDialog.Builder(requireContext())
            .setTitle("Calculation result")
            .setMessage(
                "Your inputs:\n" +
                        "x1 = $x1\n" +
                        "x2 = $x2\n" +
                        "Your result is: $result"
            )
            .setNeutralButton("Ok") { dialog, _ ->
                dialog?.cancel()
                binding.resultText.text = ""
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}