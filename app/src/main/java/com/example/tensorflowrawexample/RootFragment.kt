package com.example.tensorflowrawexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tensorflowrawexample.databinding.FragmentRootBinding

class RootFragment : Fragment() {
    private var _binding: FragmentRootBinding? = null
    private val binding get() =  _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSimpleModel.setOnClickListener {
            findNavController().navigate(R.id.action_RootFragment_to_SimpleModelFragment)
        }

        binding.buttonDigitRecognition.setOnClickListener {
            findNavController().navigate(R.id.action_RootFragment_to_DigitCanvasFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}