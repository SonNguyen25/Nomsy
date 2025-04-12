package com.example.nomsy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.compose.ui.viewinterop.AndroidView
import com.example.nomsy.ui.theme.NomsyColors
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis

@Composable
fun LineChartView(
    values: List<Float>,
    labels: List<String>,
    label: String
) {
    AndroidView(
        factory = { ctx ->
            LineChart(ctx).apply {
                val entries = values.mapIndexed { i, v -> Entry(i.toFloat(), v) }
                val set = LineDataSet(entries, label).apply {
                    lineWidth = 2f
                    setDrawCircles(true)
                    color = NomsyColors.Title.hashCode()
                    circleColors = listOf(NomsyColors.Title.hashCode())
                    valueTextColor = NomsyColors.Texts.hashCode()
                    valueTextSize = 10f
                }
                data = LineData(set)
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
                    textColor = NomsyColors.Texts.hashCode()
                    setDrawGridLines(true)
                    gridColor = NomsyColors.Subtitle.hashCode()
                }
                setBackgroundColor(NomsyColors.Background.hashCode())
                setDrawBorders(false)
                description.isEnabled = false
                legend.isEnabled = false
                animateX(500)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}