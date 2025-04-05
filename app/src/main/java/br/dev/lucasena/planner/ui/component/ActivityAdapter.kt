package br.dev.lucasena.planner.ui.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.dev.lucasena.planner.databinding.ItemActivityBinding
import br.dev.lucasena.planner.domain.model.Activity
import br.dev.lucasena.planner.R

class ActivityAdapter(
    private val onClickActivity: (selectedActivity: Activity) -> Unit,
    private val onChangeIsCompleted: (isCompleted: Boolean, selectedActivity: Activity) -> Unit
) : ListAdapter<Activity, ActivityAdapter.ViewHolder>(
    ActivityDiffCallback()
) {
    class ViewHolder(
        private val binding: ItemActivityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            activity: Activity,
            onClickActivity: (selectedActivity: Activity) -> Unit,
            onChangeIsCompleted: (isCompleted: Boolean, selectedActivity: Activity) -> Unit) {
            with(binding) {
                clActivityContainer.setOnClickListener {
                    onClickActivity(activity)
                }
                tvName.text = activity.name
                tvDatetime.text = activity.datetimeString

                ivStatus.setImageResource(
                    if (activity.isConcluded) {
                        ivStatus.setColorFilter(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.lime_300
                            )
                        )
                        R.drawable.ic_circle_check
                    } else {
                        ivStatus.clearColorFilter()
                        R.drawable.ic_circle_dashed
                    }
                )

                ivStatus.setOnClickListener {
                    onChangeIsCompleted(!activity.isConcluded, activity)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActivityBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = getItem(position)
        holder.bind(activity, onClickActivity, onChangeIsCompleted)
    }
}

class ActivityDiffCallback : DiffUtil.ItemCallback<Activity>() {
    override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
        return oldItem.uuid == newItem.uuid
    }

    override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
        return oldItem == newItem
    }

}