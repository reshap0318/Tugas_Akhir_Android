package com.minangdev.m_dosen.View.bimbingan

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.ViewModel.TranskripViewModel
import kotlinx.android.synthetic.main.fragment_bimbingan_grafic.view.*
import org.json.JSONArray


class BimbinganGraficFragment : Fragment() {
    private lateinit var root : View
    private var nim: String? = null
    private lateinit var token : String
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var transkripViewModel : TranskripViewModel
    private val barLabel = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nim = it.getString("nim")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_bimbingan_grafic, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()

        transkripViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(TranskripViewModel::class.java)

        transkripViewModel.setDataStaticA(token, nim!!)
        transkripViewModel.getDataStaticA().observe(this, Observer { datas ->
            showChartA(convertDataA(datas))
        })

        transkripViewModel.setDataStaticB(token, nim!!)
        transkripViewModel.getDataStaticB().observe(this, Observer { datas ->
            showChartB(convertDataB(datas))
        })
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(nim: String) =
            BimbinganGraficFragment().apply {
                arguments = Bundle().apply {
                    putString("nim", nim)
                }
            }
    }

    fun convertDataA(datas: JSONArray): ArrayList<PieEntry> {
        var mDataA = ArrayList<PieEntry>()
        for (i in 0 until datas.length()){
            val dataJson = datas.getJSONObject(i)
            val data = dataJson.getString("total")
            val label =  dataJson.getString("krsdtKodeNilai")
            val pieData = PieEntry(data.toFloat(), label)
            mDataA.add(pieData)
        }
        return mDataA
    }

    fun showChartA(mData: ArrayList<PieEntry>){
        val pieDataSet = PieDataSet(mData, "")
        val colors = ColorTemplate.VORDIPLOM_COLORS
        pieDataSet.colors = colors.toList()
        pieDataSet.setValueTextColor(Color.BLACK)
        pieDataSet.valueTextSize = 13f
        pieDataSet.setSliceSpace(0f)
        pieDataSet.setSelectionShift(5f)
        val data = PieData(pieDataSet)

        root.grafikABimbingan.data = data
        root.grafikABimbingan.legend.isEnabled = false
//        root.grafikA.centerText = "NILAI"
//        root.grafikA.setHighlightPerTapEnabled(true);
//        root.grafikA.isHighlightPerTapEnabled = true;
//        root.grafikA.setTouchEnabled(true);
//        root.grafikA.isDrawHoleEnabled = true
//        root.grafikA.isClickable = true;
        root.grafikABimbingan.setHoleRadius(0f);
        root.grafikABimbingan.setTransparentCircleRadius(0f);
        root.grafikABimbingan.setRotationAngle(360 - 135f);
        root.grafikABimbingan.setRotationEnabled(false);
        root.grafikABimbingan.setHorizontalScrollBarEnabled(true);
        root.grafikABimbingan.description.isEnabled = false
        root.grafikABimbingan.setEntryLabelColor(Color.BLACK)
        root.grafikABimbingan.animateY(3000)
        root.grafikABimbingan.invalidate()

    }

    fun convertDataB(datas: JSONArray): ArrayList<BarEntry> {
        var mDataB = ArrayList<BarEntry>()
        for (i in 0 until datas.length()){
            val dataJson = datas.getJSONObject(i)
            val x = dataJson.getString("ip_semester")
            val label =  dataJson.getString("semester_nama")
            val barData = BarEntry(i.toFloat(), x.toFloat())
            mDataB.add(barData)
            barLabel.add(label)
        }
        return mDataB
    }

    fun showChartB(mData: ArrayList<BarEntry>){
        val barDataSet = BarDataSet(mData, "")
        val colors = ColorTemplate.VORDIPLOM_COLORS
        barDataSet.colors = colors.toList()
        barDataSet.setValueTextColor(Color.BLACK)
        barDataSet.valueTextSize = 14f
        val data = BarData(barDataSet)

        val xAxis = root.grafikBBimbingan.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.gridColor = Color.WHITE
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.valueFormatter = (MyValueFormatter(barLabel))

//        xAxis.setDrawGridLines(false) // hilangkan garis bawah
//        root.grafikB.axisLeft.setDrawGridLines(false) //hilangkan garis kiri
        root.grafikBBimbingan.axisLeft.gridColor = Color.WHITE // rubah warna kiri jadi putih
        root.grafikBBimbingan.axisRight.isEnabled = false // hilangkan garis dan label kanan

        root.grafikBBimbingan.data = data
        root.grafikBBimbingan.setFitBars(true)
        root.grafikBBimbingan.description.isEnabled = false
        root.grafikBBimbingan.animateY(3000)
        root.grafikBBimbingan.invalidate()
    }

    class MyValueFormatter(private val xValsDateLabel: ArrayList<String>) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return value.toString()
        }

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            if (value.toInt() >= 0 && value.toInt() <= xValsDateLabel.size - 1) {
                return xValsDateLabel[value.toInt()]
            } else {
                return ("").toString()
            }
        }
    }
}