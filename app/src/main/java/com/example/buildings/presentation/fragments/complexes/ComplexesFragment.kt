package com.example.buildings.presentation.fragments.complexes

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.buildings.R
import com.example.buildings.databinding.FragmentComplexesBinding
import com.example.buildings.domain.data.Complex
import com.example.buildings.presentation.fragments.complexes.adapterComplexes.ComplexesAdapter
import com.example.buildings.presentation.fragments.complexes.adapterComplexes.ComplexesListener
import com.example.buildings.utils.Result
import com.example.buildings.utils.extension.longToast
import com.example.buildings.utils.toMoney
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.RangeSlider
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.roundToInt


class ComplexesFragment: Fragment(), ComplexesListener {

    private var _binding: FragmentComplexesBinding? = null
    private val binding get() = _binding!!

    private val vm: ComplexesViewModel by viewModel()

    private var adapter: ComplexesAdapter? = null

    private var rangeSeekBarMin = 0.1
    private var rangeSeekBarMax = 10.0
    private var rangeSeekBarPercent = (rangeSeekBarMax - rangeSeekBarMin) / 100F

    private var areasList = mutableListOf<String>()
    private var positionSelectedArea = -1

    private var filterCostMin = -1.0
    private var filterCostMax = -1.0
    private var filterArea: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentComplexesBinding.inflate(inflater, container, false)
        val root = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.error.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                longToast(it)
            }
        }
        vm.areasLoaded.observe(viewLifecycleOwner) {
            areasList.clear()
            areasList.add(getString(R.string.filter_area_label))
            areasList.addAll(it)
            if (filterArea.isNotEmpty() && !areasList.contains(filterArea)) {
                filterArea = ""
                checkFilters()
            }
        }
        vm.minCost.observe(viewLifecycleOwner) {
            rangeSeekBarMin = it
            rangeSeekBarPercent = (rangeSeekBarMax - rangeSeekBarMin) / 100F
            if (filterCostMin < rangeSeekBarMin) {
                filterCostMin = -1.0
                checkFilters()
            }
        }
        vm.maxCost.observe(viewLifecycleOwner) {
            rangeSeekBarMax = it
            rangeSeekBarPercent = (rangeSeekBarMax - rangeSeekBarMin) / 100F
            if (filterCostMax > rangeSeekBarMax) {
                filterCostMax = -1.0
                checkFilters()
            }
        }
        vm.complexesLoaded.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Result.Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    result.data?.let {
                        updateComplexes(it)
                    }
                }
                Result.Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    result.error?.let {
                        it.message?.let { it1 -> longToast(it1) }
                    }
                }
                Result.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }

        }

        adapter = ComplexesAdapter(
            requireContext(),
            this@ComplexesFragment,
            emptyList()
        )

        binding.apply {
            rvItems.apply {
                isNestedScrollingEnabled = false
            }.adapter = adapter

            rvItems.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy > 10 && fabAdd.isShown) {
                        fabAdd.hide()
                    }
                    if (dy < -10 && !fabAdd.isShown) {
                        fabAdd.show()
                    }

                    if (!rvItems.canScrollVertically(-1)) {
                        fabAdd.show()
                    }
                }
            })

            rlBtnFilter.setOnClickListener {
                if (areasList.isNotEmpty()) {
                    openFilterDialog()
                }
            }

            fabAdd.setOnClickListener {
                findNavController().navigate(R.id.action_complexesFragment_to_complexFragment)
            }
        }
        checkFilters()
    }

    private fun openFilterDialog() {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.dialog_filter,null)

        val spinner: AppCompatSpinner = view.findViewById(R.id.spinner_areas)
        val tvPriceStart: TextView = view.findViewById(R.id.tv_price_start)
        val tvPriceEnd: TextView = view.findViewById(R.id.tv_price_end)
        val tvPriceRange: TextView = view.findViewById(R.id.price_range)
        val rangeSeekBar: RangeSlider = view.findViewById(R.id.range_seek_bar)
        val positiveButton: MaterialButton = view.findViewById(R.id.btn_done)
        val negativeButton: MaterialButton = view.findViewById(R.id.btn_clear)

        dialog.setView(view)

        tvPriceStart.text = getString(R.string.filter_cost_start, rangeSeekBarMin.toMoney())
        tvPriceEnd.text = getString(R.string.filter_cost_end, rangeSeekBarMax.toMoney())

        initAreasSpinner(spinner)
        initCostRange(rangeSeekBar, tvPriceRange)

        positiveButton.setOnClickListener {
            filterCostMin = if (formatSliderValue(rangeSeekBar.values[0]) == rangeSeekBarMin) -1.0
                else formatSliderValue(rangeSeekBar.values[0])

            filterCostMax = if (formatSliderValue(rangeSeekBar.values[1]) == rangeSeekBarMax) -1.0
                else formatSliderValue(rangeSeekBar.values[1])

            filterArea = if (positionSelectedArea <= 0) ""
                else areasList[positionSelectedArea]

            positionSelectedArea = -1

            checkFilters()
            vm.getComplexesList()
            dialog.dismiss()
        }

        negativeButton.setOnClickListener {
            filterCostMin = -1.0
            filterCostMax = -1.0
            filterArea = ""
            positionSelectedArea = -1

            checkFilters()
            vm.getComplexesList()
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun initCostRange(rangeSeekBar: RangeSlider, tvPriceRange: TextView) {
        val value1 = if (filterCostMin == -1.0) 0F
            else ((filterCostMin - rangeSeekBarMin) / rangeSeekBarPercent).toFloat()

        val value2 = if (filterCostMax == -1.0) 100F
            else ((filterCostMax - rangeSeekBarMin) / rangeSeekBarPercent).toFloat()

        rangeSeekBar.values = listOf(value1, value2)
        filterCostMin = formatSliderValue(rangeSeekBar.values[0])
        filterCostMax = formatSliderValue(rangeSeekBar.values[1])

        tvPriceRange.text = "${filterCostMin.toMoney()} - ${filterCostMax.toMoney()} " +
                getString(R.string.currency_rub)

        rangeSeekBar.addOnChangeListener { s, v, _ ->
            val priceFrom = formatSliderValue(s.values[0])
            val priceTo = formatSliderValue(s.values[1])

            tvPriceRange.text = "${priceFrom.toMoney()} - ${priceTo.toMoney()} " +
                    getString(R.string.currency_rub)
        }
    }

    private fun initAreasSpinner(spinner: AppCompatSpinner)  {
        val adapterAreas = AreasAdapter(requireContext(), areasList)

        with(spinner) {
            adapter = adapterAreas
            if (filterArea.isEmpty()) {
                setSelection(0, false)
            } else {
                setSelection(areasList.indexOf(filterArea), false)
                positionSelectedArea = areasList.indexOf(filterArea)
            }

            onItemSelectedListener = spinnerListener
        }
    }

    private val spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            positionSelectedArea = position
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

    }

    private fun checkFilters() {
        if (filterArea.isNotEmpty() || filterCostMin != -1.0 || filterCostMax != -1.0) {
            binding.ivFullFilter.visibility = View.VISIBLE
        } else {
            binding.ivFullFilter.visibility = View.GONE
        }
        vm.filterArea = filterArea
        vm.filterCostMin = filterCostMin
        vm.filterCostMax = filterCostMax
    }

    private fun formatSliderValue(value: Float): Double {
        return (((value * rangeSeekBarPercent) + rangeSeekBarMin) * 10.0).roundToInt() / 10.0
    }

    private fun updateComplexes(list: List<Complex>) {
        adapter?.clearAndAddAll(list)

        binding.apply {
            if (list.isEmpty()) {
                if (filterArea.isEmpty() && filterCostMin != rangeSeekBarMin && filterCostMax != rangeSeekBarMax) {
                    tvEmpty.text = getString(R.string.complexes_full_empty)
                } else {
                    tvEmpty.text = getString(R.string.complexes_filter_empty)
                }
                llEmpty.visibility = View.VISIBLE
            } else {
                llEmpty.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun openComplex(item: Complex) {
        val bundle = Bundle()
        with(bundle) {
            item.id?.let { putInt("complex_id", it) }
        }
        findNavController().navigate(R.id.action_complexesFragment_to_complexFragment, bundle)
    }
}