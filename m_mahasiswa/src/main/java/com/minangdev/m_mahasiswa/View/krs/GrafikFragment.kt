package com.minangdev.m_mahasiswa.View.krs

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.minangdev.m_mahasiswa.Adapter.TotalNilaiAdapter
import com.minangdev.m_mahasiswa.Helper.LoadingDialog
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.ViewModel.TranskripViewModel
import kotlinx.android.synthetic.main.fragment_grafik.*
import kotlinx.android.synthetic.main.fragment_grafik.view.*
import org.json.JSONArray


class GrafikFragment : Fragment() {

    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var totalNilaiAdapter: TotalNilaiAdapter
    private lateinit var transkripViewModel : TranskripViewModel
    lateinit var loadingDialog: LoadingDialog
    lateinit var token: String
    private val labelForA= ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        root = inflater.inflate(R.layout.fragment_grafik, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()
        loadingDialog = LoadingDialog(activity!!)

        totalNilaiAdapter = TotalNilaiAdapter { }
        root.rv_list_nilai_grafik.adapter = totalNilaiAdapter
        root.rv_list_nilai_grafik.layoutManager = GridLayoutManager(activity, 3)

        transkripViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            TranskripViewModel::class.java
        )

        loadingDialog.showLoading()
        transkripViewModel.setDataStaticA(token)
        transkripViewModel.getDataStaticA().observe(this, Observer { datas ->
            totalNilaiAdapter.setData(datas)
            loadingDialog.hideLoading()
        })

        loadingDialog.showLoading()
        transkripViewModel.setDataStaticB(token)
        transkripViewModel.getDataStaticB().observe(this, Observer { datas ->
            showChartA(datas, root.grafikA)
            root.grafikA.invalidate()
            loadingDialog.hideLoading()
        })
        return root
    }

    fun convertDataA(datas: JSONArray): ArrayList<Entry> {
        var mDataB = ArrayList<Entry>()
        labelForA.clear()
        for (i in 0 until datas.length()){
            val dataJson = datas.getJSONObject(i)
            val x = dataJson.getString("ip_semester")
            val label =  dataJson.getString("semester_nama")
            val data = Entry(i.toFloat(), x.toFloat())
            mDataB.add(data)
            labelForA.add(label)
        }
        return mDataB
    }

    fun showChartA(datas: JSONArray, lineChart: LineChart){
        val mData = convertDataA(datas)
        val lineDataSet = LineDataSet(mData, "")
        val color = Color.GREEN

        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(false)
        lineChart.description.isEnabled = false
        lineChart.setNoDataText("No Data Found")
//        lineChart.setDrawGridBackground(true)
        lineChart.setDrawBorders(true)
        lineChart.setBorderColor(R.color.green_sencond)
        lineChart.setBorderWidth(2f)
        lineChart.animateY(300)

        val upper_limit = LimitLine(3.6F, "Good" )
        upper_limit.lineWidth = 4f
        upper_limit.enableDashedLine(10f, 10f, 0f)
        upper_limit.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        upper_limit.textSize = 15f

        val lower_limit = LimitLine(2.1F, "Too Low" )
        lower_limit.lineWidth = 4f
        lower_limit.enableDashedLine(10f, 10f, 0f)
        lower_limit.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        lower_limit.textSize = 15f

        val leftAxis = lineChart.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.addLimitLine(upper_limit)
        leftAxis.addLimitLine(lower_limit)
        leftAxis.axisMaximum = 4f
//        leftAxis.axisMinimum = 0f
        leftAxis.gridColor = color
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawLimitLinesBehindData(true)

        lineChart.axisRight.isEnabled = false

        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = (MyValueFormatter(labelForA))
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.gridColor = Color.BLACK
        xAxis.setDrawGridLines(true)

        lineDataSet.fillAlpha = 110
        lineDataSet.setColor(color)
        lineDataSet.lineWidth = 3f
        lineDataSet.valueTextSize = 12f
        lineDataSet.setDrawCircleHole(true)
        lineDataSet.setCircleColor(Color.BLACK)
        lineDataSet.circleHoleColor = Color.BLACK
        lineDataSet.circleRadius = 4f
        lineDataSet.circleHoleRadius = 3f

        val legend = lineChart.legend
        legend.isEnabled = false

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(lineDataSet)

        val finalData = LineData(dataSets)
        lineChart.data = finalData


    }

    class MyValueFormatter(private val mValue: ArrayList<String>) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toString()
        }

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            if (value.toInt() >= 0 && value.toInt() <= mValue.size - 1) {
                return mValue[value.toInt()]
            } else {
                return ("").toString()
            }
        }
    }
}