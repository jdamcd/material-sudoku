package com.jdamcd.sudoku.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TimeFormatTest {

    @Test
    fun noTime() {
        val formatted = Strings.formatTime(0)

        assertThat(formatted).isEqualTo("0:00")
    }

    @Test
    fun lessThanASecond() {
        val formatted = Strings.formatTime(900)

        assertThat(formatted).isEqualTo("0:00")
    }

    @Test
    fun seconds() {
        val formatted = Strings.formatTime(6000)

        assertThat(formatted).isEqualTo("0:06")
    }

    @Test
    fun minutes() {
        val formatted = Strings.formatTime(60000)

        assertThat(formatted).isEqualTo("1:00")
    }

    @Test
    fun tensOfMinutes() {
        val formatted = Strings.formatTime(1800000)

        assertThat(formatted).isEqualTo("30:00")
    }

    @Test
    fun hours() {
        val formatted = Strings.formatTime(3600000)

        assertThat(formatted).isEqualTo("1:00:00")
    }

    @Test
    fun hoursWithSingleMinutes() {
        val formatted = Strings.formatTime(3660000)

        assertThat(formatted).isEqualTo("1:01:00")
    }
}
