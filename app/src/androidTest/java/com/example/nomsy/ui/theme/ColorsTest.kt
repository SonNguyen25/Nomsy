package com.example.nomsy.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class NomsyColorsTest {

    @Test
    fun testBackgroundColor() {
        assertEquals(Color(0xFF000000), NomsyColors.Background)
    }

    @Test
    fun testTitleColor() {
        assertEquals(Color(0xFF1DCD9F), NomsyColors.Title)
    }

    @Test
    fun testSubtitleColor() {
        assertEquals(Color(0xFF169976), NomsyColors.Subtitle)
    }

    @Test
    fun testHighlightColor() {
        assertEquals(Color(0xFF000000), NomsyColors.Highlight)
    }

    @Test
    fun testTextsColor() {
        assertEquals(Color(0xFF1DCD9F), NomsyColors.Texts)
    }

    @Test
    fun testWaterColor() {
        assertEquals(Color(0xFF123458), NomsyColors.Water)
    }

    @Test
    fun testPictureBackgroundColor() {
        assertEquals(Color(0xFF222222), NomsyColors.PictureBackground)
    }
}
