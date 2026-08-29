package com.brightnest.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brightnest.app.Prefs
import com.brightnest.app.databinding.FragmentTablesBinding
import com.brightnest.app.databinding.ItemNumberChipBinding

class TablesFragment : Fragment() {

    private var _binding: FragmentTablesBinding? = null
    private val binding get() = _binding!!
    private var backCallback: OnBackPressedCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTablesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.numberGrid.layoutManager = GridLayoutManager(requireContext(), 5)
        binding.numberGrid.adapter = NumberAdapter((1..100).toList()) { showTable(it) }

        val langCode = Prefs(requireContext()).language
        binding.pickNumberHeader.text = when (langCode) {
            "ur" -> "نمبر منتخب کریں (1–100)"
            "ar" -> "اختر رقماً (1–100)"
            "hi" -> "एक नंबर चुनें (1–100)"
            else -> "Pick a number (1–100)"
        }

        binding.btnBack.setOnClickListener { hideTable() }

        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.detailRoot.visibility == View.VISIBLE) {
                    hideTable()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback!!)

        binding.listRoot.visibility = View.VISIBLE
        binding.detailRoot.visibility = View.GONE
    }

    private fun showTable(n: Int) {
        val langCode = Prefs(requireContext()).language
        val titleText = when (langCode) {
            "ur" -> "پہاڑا $n"
            "ar" -> "جدول $n"
            "hi" -> "पहाड़ा $n"
            else -> "Table of $n"
        }
        binding.tableTitle.text = titleText

        val sb = StringBuilder()
        for (i in 1..12) {
            sb.append("$n  ×  $i  =  ${n * i}\n")
        }
        binding.tableText.text = sb.toString().trimEnd()

        binding.listRoot.visibility = View.GONE
        binding.detailRoot.visibility = View.VISIBLE
    }

    private fun hideTable() {
        binding.detailRoot.visibility = View.GONE
        binding.listRoot.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backCallback?.remove()
        backCallback = null
        _binding = null
    }

    private class NumberAdapter(
        val nums: List<Int>,
        val onClick: (Int) -> Unit
    ) : RecyclerView.Adapter<NumberAdapter.VH>() {
        inner class VH(val b: ItemNumberChipBinding) : RecyclerView.ViewHolder(b.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val b = ItemNumberChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return VH(b)
        }

        override fun getItemCount() = nums.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val n = nums[position]
            holder.b.chipNumber.text = n.toString()
            holder.b.root.setOnClickListener { onClick(n) }
        }
    }
}
