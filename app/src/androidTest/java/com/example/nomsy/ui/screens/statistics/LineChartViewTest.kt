package com.example.nomsy.ui.screens.statistics

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.assertion.ViewAssertions.matches
import com.example.nomsy.ui.components.LineChartView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class LineChartViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val app: Application = ApplicationProvider.getApplicationContext()

    @Test
    fun lineChartViewIsDisplayed() {
        // Sample data for testing.
        val testValues = listOf(10f, 20f, 30f)
        val testLabels = listOf("A", "B", "C")
        val chartLabel = "TestLineChart"

        composeTestRule.setContent {
            LineChartView(
                values = testValues,
                labels = testLabels,
                label = chartLabel
            )
        }

        composeTestRule.onNodeWithTag("chart-$chartLabel").assertIsDisplayed()
    }

    @Test
    fun lineChartViewDataConfigurationIsCorrect() {
        // Sample data for testing.
        val testValues = listOf(5f, 15f, 25f)
        val testLabels = listOf("Jan", "Feb", "Mar")
        val chartLabel = "Temperature"

        composeTestRule.setContent {
            LineChartView(
                values = testValues,
                labels = testLabels,
                label = chartLabel
            )
        }

//        composeTestRule.waitUntil(timeoutMillis = 5000) {
//            composeTestRule.onAllNodesWithTag("linechart_$chartLabel")
//                .fetchSemanticsNodes().isNotEmpty()
//        }

        Espresso.onView(withTagValue(equalTo("linechart_$chartLabel")))
            .check { view, _ ->
                val lineChart = view as? LineChart
                assertNotNull("LineChart should not be null", lineChart)
                lineChart?.let { chart ->
                    val data = chart.data
                    assertNotNull("Chart data should not be null", data)

                    assertEquals("There should be exactly 1 dataset", 1, data.dataSetCount)
                    val dataSet = data.getDataSetByIndex(0) as? LineDataSet
                    assertNotNull("Dataset should be a LineDataSet", dataSet)
                    dataSet?.let {
                        assertEquals("Dataset label should match", chartLabel, it.label)
                        assertEquals("Number of entries should match", testValues.size, it.entryCount)
                        for (i in 0 until it.entryCount) {
                            val entry = it.getEntryForIndex(i)
                            assertEquals("Entry value at index $i should match", testValues[i], entry.y)
                        }
                    }


                    val xAxis = chart.xAxis
                    val formatter = xAxis.valueFormatter as? IndexAxisValueFormatter
                    assertNotNull("XAxis formatter should be an IndexAxisValueFormatter", formatter)
                    assertEquals("XAxis labels should match", testLabels.toTypedArray().toList(), formatter!!.values.toList())

                    assertEquals("Left axis minimum should be 3", 3f, chart.axisLeft.axisMinimum)
                }
            }
    }
}
