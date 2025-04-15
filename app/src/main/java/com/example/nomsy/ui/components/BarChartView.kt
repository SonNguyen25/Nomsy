package com.example.nomsy.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.nomsy.ui.theme.NomsyColors
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun BarChartView(
    values: List<Float>,
    labels: List<String>,
    label: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            BarChart(ctx).apply {
                // for Espresso testing
                tag = "barchart_$label"

                val entries = values.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
                val set = BarDataSet(entries, label).apply {
                    color = NomsyColors.Title.hashCode()
                    valueTextColor = NomsyColors.Texts.hashCode()
                    valueTextSize = 10f
                }
                data = BarData(set).apply {
                    barWidth = 0.7f
                }
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
                    axisMinimum = 0f
                    textColor = NomsyColors.Texts.hashCode()
                    setDrawGridLines(true)
                    gridColor = NomsyColors.Subtitle.hashCode()
                }
                setBackgroundColor(NomsyColors.Background.hashCode())
                setDrawBorders(false)
                setFitBars(true)
                animateY(500)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .testTag("chart-$label")
    )
}