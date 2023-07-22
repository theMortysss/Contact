package com.example.library.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.library.R
import com.example.library.databinding.FragmentOptionsBinding
import com.example.library.di.HasAppComponent
import com.example.library.utils.injectViewModel
import com.example.library.viewmodel.OptionsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import javax.inject.Inject

class OptionsFragment : Fragment(R.layout.fragment_options) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var optionsViewModel: OptionsViewModel

    private var _optionsFrag: FragmentOptionsBinding? = null
    private val optionsFrag: FragmentOptionsBinding get() = _optionsFrag!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
            ?.getAppComponent()
            ?.plusOptionsContainer()
            ?.inject(this)
        optionsViewModel = injectViewModel(viewModelFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _optionsFrag = FragmentOptionsBinding.inflate(inflater, container, false)
        return optionsFrag.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        turnDarkMode()

        switchNightMode(optionsFrag.switchNightMode)

        optionsFrag.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun turnDarkMode() {
        lifecycleScope.launch {
            optionsViewModel.dataStore.darkModeEnabled.collect { enabled ->
                optionsFrag.switchNightMode.isChecked = enabled
                enableNightMode(enabled)
            }
        }
    }

    private fun switchNightMode(switchNightMode: SwitchMaterial) {
        switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            optionsViewModel.setDarkMode(isChecked)
        }
    }

    private fun enableNightMode(enable: Boolean) {
        if (enable) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onDestroyView() {
        _optionsFrag = null
        super.onDestroyView()
    }
}
