package br.dev.lucasena.planner.ui

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import br.dev.lucasena.planner.R
import br.dev.lucasena.planner.domain.utils.stringToBitmap
import br.dev.lucasena.planner.databinding.FragmentHomeBinding
import br.dev.lucasena.planner.domain.utils.toActivityDate
import br.dev.lucasena.planner.domain.utils.toActivityTime
import br.dev.lucasena.planner.ui.component.ActivityAdapter
import br.dev.lucasena.planner.ui.component.ActivityDatePickerDialogFragment
import br.dev.lucasena.planner.ui.component.ActivityTimePickerDialogFragment
import br.dev.lucasena.planner.ui.extension.hideKeyboard
import br.dev.lucasena.planner.ui.viewmodel.ActivityViewModel
import br.dev.lucasena.planner.ui.viewmodel.SetDate
import br.dev.lucasena.planner.ui.viewmodel.SetTime
import br.dev.lucasena.planner.ui.viewmodel.UserRegistrationViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val userRegistrationViewModel: UserRegistrationViewModel by activityViewModels()
    private val activitiesViewModel: ActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()

        with(binding) {
            activitiesViewModel.fetchActivities()

            clHomeContainer.setOnClickListener{
                etNewActivity.clearFocus()
                requireContext().hideKeyboard(etNewActivity)
            }

            etNewActivity.doOnTextChanged { text, _, _, _ ->
                if (text.toString().isEmpty()) {
                    etNewActivity.clearFocus()
                    requireContext().hideKeyboard(etNewActivity)
                }
                activitiesViewModel.putDataNewActivity(
                    name = text.toString()
                )
            }

            etNewActivityDate.setOnClickListener {
                ActivityDatePickerDialogFragment(
                    onConfirm = { year, month, day ->
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, day)
                        }
                        etNewActivityDate.setText(calendar.toActivityDate())
                        activitiesViewModel.putDataNewActivity(
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

            etNewActivityTime.setOnClickListener {
                ActivityTimePickerDialogFragment(
                    onConfirm = { hour, minute ->
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                        }
                        etNewActivityTime.setText(calendar.toActivityTime())
                        activitiesViewModel.putDataNewActivity(
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

            btnSaveActivity.setOnClickListener {
                activitiesViewModel.saveNewActivity(
                    onSuccess = {
                        clearNewActivityFields()
                    }, onError = {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.new_activity_err_msg),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }
    }

    private fun FragmentHomeBinding.clearNewActivityFields() {
        etNewActivity.setText(null)
        etNewActivityDate.setText(null)
        etNewActivityTime.setText(null)

        etNewActivity.clearFocus()
    }

    private fun setupObservers() {
        lifecycleScope.apply {
            launch {
                userRegistrationViewModel.profile.collect { profile ->
                    with(binding) {
                        tvUserName.text = getString(R.string.ola_usuario, profile.name)
                        stringToBitmap(profile.image)?.let { image ->
                            ivUserPhoto.setImageBitmap(image)
                        }
                    }
                }
            }
            launch {
                userRegistrationViewModel.isTokenValid.distinctUntilChanged { old, new ->
                    old == new
                }.collect { isTokenValid ->
                    if (isTokenValid == false) {
                        showNewTokenSnackBar()
                    }
                }
            }
            launch {
                activitiesViewModel.activities.collect { activities ->
                    if (binding.rvActivities.adapter == null) {
                        binding.rvActivities.adapter = ActivityAdapter(
                            onClickActivity = { selectedActivity ->
                                UpdateActivityDialogFragment(selectedActivity)
                                    .show(
                                        childFragmentManager,
                                        UpdateActivityDialogFragment.TAG
                                    )
                            },
                            onChangeIsCompleted = { isCompleted, selectedActivity ->
                                activitiesViewModel.updateIsConcluded(selectedActivity.uuid, isCompleted)
                            }
                        )
                    }
                    (binding.rvActivities.adapter as ActivityAdapter).submitList(activities)
                }
            }
        }
    }

    private fun showNewTokenSnackBar() {
        Snackbar.make(requireView(), "Token expirado", Snackbar.LENGTH_INDEFINITE)
            .setAction("Obter novo") {
                userRegistrationViewModel.obtainNewToken()
            }
            .setActionTextColor(
                ContextCompat.getColor(requireContext(), R.color.lime_300)
            ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}