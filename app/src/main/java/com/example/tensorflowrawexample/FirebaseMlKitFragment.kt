package com.example.tensorflowrawexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.tensorflowrawexample.databinding.FragmentSimpleModelBinding
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.google.firebase.ml.modeldownloader.ktx.customModelDownloadConditions
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FirebaseMlKitFragment : Fragment() {
    private var _binding: FragmentSimpleModelBinding? = null
    private val binding get() = _binding!!

    private lateinit var interpreter: Interpreter

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

        downloadModel()

        binding.computeButton.setOnClickListener {
            calculate()
        }
    }

    private fun downloadModel() {
        val conditions = customModelDownloadConditions { requireWifi() }

        FirebaseModelDownloader.getInstance()
            .getModel("sample_model", DownloadType.LATEST_MODEL, conditions)
            .addOnSuccessListener { model ->
                val modelFile = model?.file
                modelFile?.let {
                    interpreter = Interpreter(it)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error downloadinig model", Toast.LENGTH_SHORT).show()
            }

    }

    private fun calculate() {
        binding.resultText.text = getString(R.string.compute_loading_label)

        val x1 = binding.x1Input.editText!!.text.toString().toFloat()
        val x2 = binding.x2Input.editText!!.text.toString().toFloat()

        val inputValue = floatArrayOf(x1, x2)

        val outputValue = ByteBuffer.allocateDirect(4)
        outputValue.order(ByteOrder.nativeOrder())

        interpreter.run(inputValue, outputValue)

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