package br.dev.lucasena.planner.ui

import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import br.dev.lucasena.planner.databinding.FragmentUpdateActivityDialogBinding
import br.dev.lucasena.planner.domain.model.Activity
import br.dev.lucasena.planner.domain.utils.createCalendarFromTimeInMillis
import br.dev.lucasena.planner.domain.utils.toActivityDate
import br.dev.lucasena.planner.domain.utils.toActivityTime
import br.dev.lucasena.planner.ui.component.ActivityDatePickerDialogFragment
import br.dev.lucasena.planner.ui.component.ActivityTimePickerDialogFragment
import br.dev.lucasena.planner.ui.extension.hideKeyboard
import br.dev.lucasena.planner.ui.viewmodel.ActivityViewModel
import br.dev.lucasena.planner.ui.viewmodel.SetDate
import br.dev.lucasena.planner.ui.viewmodel.SetTime
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UpdateActivityDialogFragment(
    private val selectedActivity: Activity
) : BottomSheetDialogFragment() {
    private var _binding: FragmentUpdateActivityDialogBinding? = null
    private val binding get() = _binding!!
    private val activitiesViewModel: ActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateActivityDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activitiesViewModel.setSelectedActivity(selectedActivity)

        with (binding) {
            val selectedActivityDateTimeCalendar = createCalendarFromTimeInMillis(
                selectedActivity.datetime
            )
            binding.eActivityName.setText(selectedActivity.name)
            etActivityDate.setText(selectedActivityDateTimeCalendar.toActivityDate())
            etActivityTime.setText(selectedActivityDateTimeCalendar.toActivityTime())

            binding.eActivityName.doOnTextChanged { text, _, _, _ ->
                if (text.toString().isEmpty()) {
                    binding.eActivityName.clearFocus()
                    requireContext().hideKeyboard(binding.eActivityName)
                }
                activitiesViewModel.updateSelectedActivity(
                    name = text.toString()
                )
            }

            etActivityDate.setOnClickListener {
                ActivityDatePickerDialogFragment(
                    initialValue = createCalendarFromTimeInMillis(selectedActivity.datetime),
                    onConfirm = { year, month, day ->
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, day)
                        }
                        etActivityDate.setText(calendar.toActivityDate())
                        activitiesViewModel.updateSelectedActivity(
                            date = SetDate(
                                year = year,
                                month = month,
                                day = day
                            )
                        )
                    },
                    onCancel = {}
                ).show(
                    childFragmentManager,
                    ActivityDatePickerDialogFragment.TAG
                )
            }

            etActivityTime.setOnClickListener {
                ActivityTimePickerDialogFragment(
                    initialValue = createCalendarFromTimeInMillis(selectedActivity.datetime),
                    onConfirm = { hour, minute ->
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                        }
                        etActivityTime.setText(calendar.toActivityTime())
                        activitiesViewModel.updateSelectedActivity(
                            time = SetTime(
                                hour = hour,
                                minute = minute
                            )
                        )
                    },
                    onCancel = {}
                ).show(
                    childFragmentManager,
                    ActivityTimePickerDialogFragment.TAG
                )
            }

            tvDeleteActivity.setOnClickListener {
                activitiesViewModel.delete(selectedActivity.uuid)
                dialog?.dismiss()
            }

            btnUpdateActivity.setOnClickListener {
                activitiesViewModel.updateActivity()
                dialog?.dismiss()
            }
        }
    }

    companion object {
        const val TAG = "UpdateActivityDialogFragment"
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        activitiesViewModel.clearSelectedActivity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}