package com.brightnest.app.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brightnest.app.Prefs
import com.brightnest.app.R
import com.brightnest.app.data.AlarmItem
import com.brightnest.app.data.AlarmStore
import com.brightnest.app.databinding.FragmentAlarmBinding
import com.brightnest.app.databinding.ItemAlarmRowBinding
import com.brightnest.app.databinding.ViewStepperBinding
import java.util.Calendar

class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: Prefs
    private val handler = Handler(Looper.getMainLooper())

    private var setHour = 7
    private var setMin = 0
    private var setPm = false

    private var addHour = 7
    private var addMin = 0
    private var addPm = false

    private val tick = object : Runnable {
        override fun run() {
            if (_binding == null) return
            val now = AlarmStore.now(prefs)
            binding.tvNow.text =
                AlarmStore.to12h(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = Prefs(requireContext())

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        bindStepper(binding.stepSetHour, "Hour", 1, 12, false, { setHour }, { setHour = it })
        bindStepper(binding.stepSetMin, "Min", 0, 59, true, { setMin }, { setMin = it })
        bindStepper(binding.stepAddHour, "Hour", 1, 12, false, { addHour }, { addHour = it })
        bindStepper(binding.stepAddMin, "Min", 0, 59, true, { addMin }, { addMin = it })

        binding.btnSetAm.setOnClickListener { setPm = false; renderAmPm(binding.btnSetAm, binding.btnSetPm, setPm) }
        binding.btnSetPm.setOnClickListener { setPm = true; renderAmPm(binding.btnSetAm, binding.btnSetPm, setPm) }
        binding.btnAddAm.setOnClickListener { addPm = false; renderAmPm(binding.btnAddAm, binding.btnAddPm, addPm) }
        binding.btnAddPm.setOnClickListener { addPm = true; renderAmPm(binding.btnAddAm, binding.btnAddPm, addPm) }
        renderAmPm(binding.btnSetAm, binding.btnSetPm, setPm)
        renderAmPm(binding.btnAddAm, binding.btnAddPm, addPm)

        binding.btnSetClock.setOnClickListener {
            AlarmStore.setManualTime(prefs, to24(setHour, setPm), setMin)
            renderClockState()
        }
        binding.btnResetClock.setOnClickListener {
            AlarmStore.clearManual(prefs)
            renderClockState()
        }

        binding.btnAddAlarm.setOnClickListener {
            val list = AlarmStore.load(prefs)
            list.add(
                AlarmItem(
                    id = System.currentTimeMillis().toString(),
                    hour = to24(addHour, addPm),
                    minute = addMin,
                    label = binding.inputLabel.text.toString().trim(),
                    enabled = true
                )
            )
            AlarmStore.save(prefs, list)
            binding.inputLabel.setText("")
            renderAlarms()
        }

        renderClockState()
        renderAlarms()
    }

    private fun to24(h12: Int, pm: Boolean) = if (pm) (h12 % 12) + 12 else h12 % 12

    private fun bindStepper(
        s: ViewStepperBinding,
        label: String,
        min: Int,
        max: Int,
        pad: Boolean,
        get: () -> Int,
        set: (Int) -> Unit
    ) {
        s.stepLabel.text = label
        fun render() {
            val v = get()
            s.stepValue.text = if (pad) v.toString().padStart(2, '0') else v.toString()
        }
        s.btnUp.setOnClickListener {
            val v = get() + 1
            set(if (v > max) min else v)
            render()
        }
        s.btnDown.setOnClickListener {
            val v = get() - 1
            set(if (v < min) max else v)
            render()
        }
        render()
    }

    private fun renderAmPm(am: TextView, pm: TextView, isPm: Boolean) {
        val ctx = requireContext()
        am.setBackgroundResource(if (!isPm) R.drawable.pill_primary else R.drawable.pill_muted)
        am.setTextColor(ContextCompat.getColor(ctx, if (!isPm) R.color.on_primary else R.color.foreground))
        pm.setBackgroundResource(if (isPm) R.drawable.pill_primary else R.drawable.pill_muted)
        pm.setTextColor(ContextCompat.getColor(ctx, if (isPm) R.color.on_primary else R.color.foreground))
    }

    private fun renderClockState() {
        val manual = prefs.clockOffsetMs != 0L
        binding.tvNowSub.text = if (manual) "Manual time (set by you)" else "Current time"
        binding.manualChip.visibility = if (manual) View.VISIBLE else View.GONE
        binding.btnResetClock.visibility = if (manual) View.VISIBLE else View.GONE
        val now = AlarmStore.now(prefs)
        binding.tvNow.text =
            AlarmStore.to12h(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))
    }

    private fun renderAlarms() {
        val ctx = requireContext()
        val list = AlarmStore.load(prefs)
        binding.alarmsContainer.removeAllViews()
        binding.emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        for (a in list) {
            val row = ItemAlarmRowBinding.inflate(layoutInflater, binding.alarmsContainer, false)
            row.rowTime.text = AlarmStore.to12h(a.hour, a.minute)
            row.rowTime.setTextColor(
                ContextCompat.getColor(ctx, if (a.enabled) R.color.foreground else R.color.muted_foreground)
            )
            if (a.label.isNotEmpty()) {
                row.rowLabel.text = a.label
                row.rowLabel.visibility = View.VISIBLE
            }
            row.rowSwitch.isChecked = a.enabled
            row.rowSwitch.setOnCheckedChangeListener { _, checked ->
                val updated = AlarmStore.load(prefs)
                    .map { if (it.id == a.id) it.copy(enabled = checked) else it }
                AlarmStore.save(prefs, updated)
                row.rowTime.setTextColor(
                    ContextCompat.getColor(ctx, if (checked) R.color.foreground else R.color.muted_foreground)
                )
            }
            row.rowDelete.setOnClickListener {
                AlarmStore.save(prefs, AlarmStore.load(prefs).filter { it.id != a.id })
                renderAlarms()
            }
            binding.alarmsContainer.addView(row.root)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(tick)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(tick)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(tick)
        _binding = null
    }
}
