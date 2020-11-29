package com.minangdev.m_mahasiswa.View.krs

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.ViewModel.TranskripViewModel
import kotlinx.android.synthetic.main.fragment_grafik.*
import kotlinx.android.synthetic.main.fragment_grafik.view.*
import org.json.JSONArray


class GrafikFragment : Fragment() {

    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var transkripViewModel : TranskripViewModel
    lateinit var token: String
    private val barLabel = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        root = inflater.inflate(R.layout.fragment_grafik, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()

        transkripViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            TranskripViewModel::class.java
        )
        transkripViewModel.setDataStaticA(token)
        transkripViewModel.getDataStaticA().observe(this, Observer { datas ->
            showChartA(convertDataA(datas))
        })

        transkripViewModel.setDataStaticB(token)
        transkripViewModel.getDataStaticB().observe(this, Observer { datas ->
            showChartB(convertDataB(datas))
        })
        return root
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

        root.grafikA.data = data
        root.grafikA.legend.isEnabled = false
//        root.grafikA.centerText = "NILAI"
//        root.grafikA.setHighlightPerTapEnabled(true);
//        root.grafikA.isHighlightPerTapEnabled = true;
//        root.grafikA.setTouchEnabled(true);
//        root.grafikA.isDrawHoleEnabled = true
//        root.grafikA.isClickable = true;
        root.grafikA.setHoleRadius(0f);
        root.grafikA.setTransparentCircleRadius(0f);
        root.grafikA.setRotationAngle(360 - 135f);
        root.grafikA.setRotationEnabled(false);
        root.grafikA.setHorizontalScrollBarEnabled(true);
        root.grafikA.description.isEnabled = false
        root.grafikA.setEntryLabelColor(Color.BLACK)
        root.grafikA.animateY(3000)
        root.grafikA.invalidate()

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

        val xAxis = root.grafikB.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.gridColor = Color.WHITE
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.valueFormatter = (MyValueFormatter(barLabel))

//        xAxis.setDrawGridLines(false) // hilangkan garis bawah
//        root.grafikB.axisLeft.setDrawGridLines(false) //hilangkan garis kiri
        root.grafikB.axisLeft.gridColor = Color.WHITE // rubah warna kiri jadi putih
        root.grafikB.axisRight.isEnabled = false // hilangkan garis dan label kanan

        root.grafikB.data = data
        root.grafikB.setFitBars(true)
        root.grafikB.description.isEnabled = false
        root.grafikB.animateY(3000)
        root.grafikB.invalidate()
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