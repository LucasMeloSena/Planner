package br.dev.lucasena.planner.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.dev.lucasena.planner.R
import br.dev.lucasena.planner.domain.utils.bitmapToString
import br.dev.lucasena.planner.domain.utils.imageUriToBitmap
import br.dev.lucasena.planner.databinding.FragmentUserRegistrationBinding
import br.dev.lucasena.planner.ui.viewmodel.UserRegistrationViewModel
import kotlinx.coroutines.launch

class UserRegistrationFragment : Fragment() {
    private var _binding: FragmentUserRegistrationBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { findNavController() }
    private val userRegistrationViewModel by activityViewModels<UserRegistrationViewModel>()
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val imageBitmap = requireContext().imageUriToBitmap(uri)
            imageBitmap?.let {
                val imageBase64 = bitmapToString(imageBitmap)
                userRegistrationViewModel.updateProfile(image = imageBase64)
                binding.ivAddPhoto.setImageURI(uri)
            }
        } else {
            Toast.makeText(requireContext(), "Nenhuma imagem selecionada!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        with (binding) {
            ivAddPhoto.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            etName.addTextChangedListener { text ->
                userRegistrationViewModel.updateProfile(
                    name = text.toString()
                )
            }
            etPhone.addTextChangedListener { text ->
                userRegistrationViewModel.updateProfile(
                    phone = text.toString()
                )
            }
            etEmail.addTextChangedListener { text ->
                userRegistrationViewModel.updateProfile(
                    email = text.toString()
                )
            }

            btnSaveUser.setOnClickListener {
                userRegistrationViewModel.saveProfile {
                    navController.navigate(R.id.action_userRegistrationFragment_to_homeFragment)
                }
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            userRegistrationViewModel.isProfileValid.collect { isProfileValid ->
                binding.btnSaveUser.isEnabled = isProfileValid
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}