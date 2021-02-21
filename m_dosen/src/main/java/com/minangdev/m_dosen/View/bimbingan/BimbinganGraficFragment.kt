package com.minangdev.m_dosen.View.bimbingan

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.minangdev.m_dosen.Adapter.TotalNilaiAdapter
import com.minangdev.m_dosen.Helper.LoadingDialog
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
    private lateinit var totalNilaiAdapter: TotalNilaiAdapter
    private lateinit var transkripViewModel : TranskripViewModel
    lateinit var loadingDialog: LoadingDialog

    private var labelForA = ArrayList<String>()

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

        totalNilaiAdapter = TotalNilaiAdapter { }
        root.rv_list_nilai_grafik_bimbingan.adapter = totalNilaiAdapter
        root.rv_list_nilai_grafik_bimbingan.layoutManager = GridLayoutManager(activity, 3)

        transkripViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(TranskripViewModel::class.java)

        loadingDialog = LoadingDialog(activity!!)
        loadingDialog.showLoading()
        transkripViewModel.setDataStaticA(token, nim!!)
        transkripViewModel.getDataStaticA().observe(this, Observer { datas ->
            totalNilaiAdapter.setData(datas)
            loadingDialog.hideLoading()
        })

        loadingDialog.showLoading()
        transkripViewModel.setDataStaticB(token, nim!!)
        transkripViewModel.getDataStaticB().observe(this, Observer { datas ->
            showChartA(datas, root.grafikABimbingan)
            root.grafikABimbingan.invalidate()
            loadingDialog.hideLoading()
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
        upper_limit.lineColor = Color.BLUE
        upper_limit.textSize = 15f

        val lower_limit = LimitLine(2.1F, "Too Low" )
        lower_limit.lineWidth = 4f
        lower_limit.enableDashedLine(10f, 10f, 0f)
        lower_limit.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        lower_limit.lineColor = Color.RED
        lower_limit.textSize = 15f

        val leftAxis = lineChart.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.addLimitLine(upper_limit)
        leftAxis.addLimitLine(lower_limit)
        leftAxis.axisMaximum = 4f
//        leftAxis.axisMinimum = 3f
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