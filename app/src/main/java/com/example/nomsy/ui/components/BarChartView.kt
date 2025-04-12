package com.example.nomsy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.compose.ui.viewinterop.AndroidView
import com.example.nomsy.ui.theme.NomsyColors
import com.github.mikephil.charting.components.XAxis

@Composable
fun BarChartView(
    values: List<Float>,
    labels: List<String>,
    label: String
) {
    AndroidView(
        factory = { ctx ->
            BarChart(ctx).apply {
                val entries = values.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
                val set = BarDataSet(entries, label).apply {
                    color = NomsyColors.Title.hashCode()
                    valueTextColor = NomsyColors.Texts.hashCode()
                    valueTextSize = 10f
                }
                data = BarData(set).apply {
                    barWidth = 0.7f
                }
                // X-axis
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(labels)
                    granularity = 1f
                    setDrawGridLines(false)
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = NomsyColors.Texts.hashCode()
                }
                // Y-axis
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
//                legend.isEnabled = false
//                description.isEnabled = false
                animateY(500)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}