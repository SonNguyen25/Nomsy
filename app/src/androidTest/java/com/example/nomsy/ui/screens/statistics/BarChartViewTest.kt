// File: BarChartViewTest.kt
package com.example.nomsy.ui.screens.statistics

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.assertion.ViewAssertions
import com.example.nomsy.ui.components.BarChartView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class BarChartViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val app: Application = ApplicationProvider.getApplicationContext()

    @Test
    fun barChartViewIsDisplayed() {
        // Sample data for testing
        val testValues = listOf(10f, 20f, 30f)
        val testLabels = listOf("A", "B", "C")
        val chartLabel = "TestChart"

        composeTestRule.setContent {
            BarChartView(
                values = testValues,
                labels = testLabels,
                label = chartLabel
            )
        }

        composeTestRule.onNodeWithTag("chart-$chartLabel").assertIsDisplayed()
    }

    @Test
    fun barChartViewDataConfigurationIsCorrect() {
        // Sample data for testing
        val testValues = listOf(5f, 15f, 25f)
        val testLabels = listOf("Jan", "Feb", "Mar")
        val chartLabel = "Sales"

        composeTestRule.setContent {
            BarChartView(
                values = testValues,
                labels = testLabels,
                label = chartLabel
            )
        }



        Espresso.onView(ViewMatchers.withTagValue(CoreMatchers.equalTo("barchart_$chartLabel")))
            .check { view, _ ->
                val barChart = view as BarChart

                val data = barChart.data
                assertNotNull("Chart data should not be null", data)

                assertEquals("There should be exactly 1 dataset", 1, data.dataSetCount)
                val dataSet = data.getDataSetByIndex(0) as BarDataSet

                assertEquals("Dataset label should match", chartLabel, dataSet.label)

                assertEquals("Number of entries should match", testValues.size, dataSet.entryCount)

                for (i in 0 until dataSet.entryCount) {
                    val entry = dataSet.getEntryForIndex(i)
                    assertEquals("Entry value at index $i should match", testValues[i], entry.y)
                }

                assertEquals("Bar width should be 0.7f", 0.7f, data.barWidth)

                val xAxis = barChart.xAxis
                val formatter = xAxis.valueFormatter as? IndexAxisValueFormatter
                assertNotNull("XAxis formatter should be an IndexAxisValueFormatter", formatter)

                assertEquals("XAxis labels should match", testLabels.toTypedArray().toList(), formatter!!.values.toList())

                assertEquals("Left axis minimum should be 0", 0f, barChart.axisLeft.axisMinimum)
            }
    }
}