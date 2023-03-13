package com.example.tensorflowrawexample.digits

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tensorflowrawexample.databinding.ItemClassificationResultBinding
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.util.Locale

class ClassificationResultsAdapter : RecyclerView.Adapter<ClassificationResultsAdapter.ViewHolder>() {

    private var categories: MutableList<Category?> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateResults(listClassifications: List<Classifications>?) {
        listClassifications?.get(0)?.categories?.let {
            categories = it
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reset() {
        categories = mutableListOf()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemClassificationResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        categories[position]?.let { category ->
            holder.bind(category.label, category.score)
        }
    }

    override fun getItemCount(): Int = categories.size

    inner class ViewHolder(private val binding: ItemClassificationResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(label: String, score: Float) {
            with(binding) {
                tvLabel.text = label
                tvScore.text = String.format(
                    Locale.US,
                    "%.4f",
                    score
                )
            }
        }
    }
}
