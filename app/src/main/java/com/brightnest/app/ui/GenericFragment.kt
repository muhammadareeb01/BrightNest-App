package com.brightnest.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.brightnest.app.R
import com.brightnest.app.databinding.FragmentGenericBinding

class GenericFragment : Fragment() {

    private var _binding: FragmentGenericBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGenericBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments?.getString("title") ?: "BrightNest"
        binding.title.text = title
        binding.icon.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
