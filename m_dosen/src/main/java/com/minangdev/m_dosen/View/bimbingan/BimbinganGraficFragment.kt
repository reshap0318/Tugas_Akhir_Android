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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
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

        transkripViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(TranskripViewModel::class.java)

        loadingDialog = LoadingDialog(activity!!)
        loadingDialog.showLoading()
        transkripViewModel.setDataStaticA(token, nim!!)
        transkripViewModel.getDataStaticA().observe(this, Observer { datas ->
            setListData(datas)
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

    fun setListData(datas: JSONArray){
        val showData = ArrayList<String>()
        for (i in 0 until datas.length()){
            val mData = datas.getJSONObject(i).getString("krsdtKodeNilai") + " : " + datas.getJSONObject(i).getString("total")
            showData.add(mData)
        }
        val listAdapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, showData)
        root.list_nilai_grafik_bimbingan.adapter = listAdapter
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
        val color = Color.WHITE

        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(false)
        lineChart.description.isEnabled = false
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
        xAxis.gridColor = Color.WHITE
        xAxis.setDrawGridLines(true)

        lineDataSet.fillAlpha = 110
        lineDataSet.setColor(color)
        lineDataSet.lineWidth = 3f
        lineDataSet.valueTextSize = 10f

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