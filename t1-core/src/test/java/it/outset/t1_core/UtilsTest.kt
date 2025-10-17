package it.outset.t1_core

import it.outset.t1_core.utils.Utils
import org.junit.Assert
import org.junit.Test

class UtilsTest {

    @Test
    fun `calculateNmeaCrc with valid NMEA string including dollar and asterisk`() {
        // GPGGA sentence example
        val nmea = "\$PING,0016A4A49AB3,,,0,35,940586709,1*0D"
        val result = Utils.Companion.calculateNmeaCrc(nmea)
        Assert.assertEquals("0D", result)
    }

    @Test
    fun `calculateNmeaCrc with string without dollar sign`() {
        val nmea = "GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*"
        val result = Utils.Companion.calculateNmeaCrc(nmea)
        // Expected CRC for the entire string up to *
        Assert.assertEquals("47", result)
    }

    @Test
    fun `calculateNmeaCrc with string without asterisk`() {
        val nmea = "\$GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,"
        val result = Utils.Companion.calculateNmeaCrc(nmea)
        // Should process entire string after $
        Assert.assertEquals("47", result)
    }

    @Test
    fun `calculateNmeaCrc with simple test string`() {
        val nmea = "\$GPGLL,4916.45,N,12311.12,W,225444,A*"
        val result = Utils.Companion.calculateNmeaCrc(nmea)
        Assert.assertEquals("31", result)
    }

    @Test
    fun `calculateNmeaCrc with GPRMC sentence`() {
        val nmea = "\$GPRMC,123519,A,4807.038,N,01131.000,E,022.4,084.4,230394,003.1,W*"
        val result = Utils.Companion.calculateNmeaCrc(nmea)
        Assert.assertEquals("6A", result)
    }

    @Test
    fun `calculateNmeaCrc with minimal string`() {
        val nmea = "\$TEST*"
        val result = Utils.Companion.calculateNmeaCrc(nmea)
        // XOR of T, E, S, T
        // T=0x54, E=0x45, S=0x53, T=0x54
        // 0x54 XOR 0x45 = 0x11
        // 0x11 XOR 0x53 = 0x42
        // 0x42 XOR 0x54 = 0x16
        Assert.assertEquals("16", result)
    }

    @Test
    fun `calculateNmeaCrc with empty content between dollar and asterisk`() {
        val nmea = "\$*"
        val result = Utils.Companion.calculateNmeaCrc(nmea)
        Assert.assertEquals("00", result)
    }

    @Test
    fun `calculateNmeaCrc with plain string no delimiters`() {
        val nmea = "TEST"
        val result = Utils.Companion.calculateNmeaCrc(nmea)
        // XOR of T, E, S, T
        Assert.assertEquals("16", result)
    }
}