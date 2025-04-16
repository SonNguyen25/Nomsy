package nom.nom.nomsy.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import nom.nom.nomsy.ui.theme.NomsyColors
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun LineChartView(
    values: List<Float>,
    labels: List<String>,
    label: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            LineChart(ctx).apply {
                tag = "linechart_$label"
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
                setBackgroundColor(NomsyColors.Background.hashCode())
                setDrawBorders(false)
                description.isEnabled = false
                legend.isEnabled = false
                animateX(500)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .testTag("chart-$label")
    )
}