package com.example.nomsy.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.nomsy.ui.theme.NomsyColors
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun MultiLineChartView(
    series: List<Pair<String, List<Float>>>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        NomsyColors.Title.hashCode(),
        NomsyColors.Subtitle.hashCode(),
        Color.parseColor("#FFA726")
    )

    AndroidView(
        factory = { ctx ->
            LineChart(ctx).apply {
                tag = "multilinechart"
                val dataSets = series.mapIndexed { index, (name, values) ->
                    val entries = values.mapIndexed { i, v -> Entry(i.toFloat(), v) }
                    LineDataSet(entries, name).apply {
                        lineWidth = 2f
                        setDrawCircles(false)
                        color = colors[index % colors.size]
                        valueTextColor = NomsyColors.Texts.hashCode()
                        valueTextSize = 10f
                    }
                }
                data = LineData(dataSets)
                // X
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(labels)
                    granularity = 1f
                    setDrawGridLines(false)
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = NomsyColors.Texts.hashCode()
                }
                // Y
                axisRight.isEnabled = false
                axisLeft.apply {
                    textColor = NomsyColors.Texts.hashCode()
                    setDrawGridLines(true)
                    gridColor = NomsyColors.Subtitle.hashCode()
                }
                // Legend
                legend.apply {
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    textColor = NomsyColors.Texts.hashCode()
                    form = Legend.LegendForm.CIRCLE
                }
                setBackgroundColor(NomsyColors.Background.hashCode())
                setDrawBorders(false)
                description.isEnabled = false
                animateX(500)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .testTag("macronutrients-chart")
    )
}