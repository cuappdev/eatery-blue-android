package com.cornellappdev.android.eatery.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Expected values came from generate_barcode.py, by way of the iOS BarcodeGeneratorTests. All seeds
 * and patron IDs are fake keys, not a live GET account.
 */
class BarcodeGeneratorTest {
    @Test
    fun matchesPythonForFixedTimestamp() {
        assertEquals(
            "1700000000102731570695",
            BarcodeGenerator.generateBarcode("0123456789abcdef", "12345", 1_700_000_000)
        )
    }

    @Test
    fun matchesPythonFiveSecondsLater() {
        assertEquals(
            "1700000005104570632015",
            BarcodeGenerator.generateBarcode("0123456789abcdef", "12345", 1_700_000_005)
        )
    }

    @Test
    fun matchesPythonWithTenByteSeed() {
        assertEquals(
            "1741034134114538783673",
            BarcodeGenerator.generateBarcode("00112233445566778899", "1234567890", 1_741_034_134)
        )
    }

    @Test
    fun matchesPythonWithSingleDigitPatronId() {
        assertEquals(
            "1600000000106227967348",
            BarcodeGenerator.generateBarcode("aabbccddeeff00112233", "1", 1_600_000_000)
        )
    }

    @Test
    fun barcodeHasExpectedShape() {
        val barcode = BarcodeGenerator.generateBarcode("0123456789abcdef", "12345", 1_700_000_000)

        assertEquals(22, barcode.length)
        assertTrue(barcode.all { it in '0'..'9' })
        assertEquals("1700000000", barcode.take(10))
        assertEquals('1', barcode[10])
    }

    @Test
    fun seedIsNotCaseSensitive() {
        assertEquals(
            BarcodeGenerator.generateBarcode("aabbccddeeff00112233", "1", 1_600_000_000),
            BarcodeGenerator.generateBarcode("AABBCCDDEEFF00112233", "1", 1_600_000_000)
        )
    }

    @Test
    fun rejectsInvalidSeed() {
        listOf("", "abc", "not hex!").forEach { seed ->
            assertThrows(IllegalArgumentException::class.java) {
                BarcodeGenerator.generateBarcode(seed, "12345", 1_700_000_000)
            }
        }
    }

    @Test
    fun rejectsInvalidPatronId() {
        listOf("", "abc", "-1").forEach { patronId ->
            assertThrows(IllegalArgumentException::class.java) {
                BarcodeGenerator.generateBarcode("0123456789abcdef", patronId, 1_700_000_000)
            }
        }
    }

    @Test
    fun rejectsTimestampOutsideTenDigits() {
        listOf(-1L, 10_000_000_000L).forEach { timestamp ->
            assertThrows(IllegalArgumentException::class.java) {
                BarcodeGenerator.generateBarcode("0123456789abcdef", "12345", timestamp)
            }
        }
    }

    @Test
    fun largestTenDigitTimestampKeepsBarcodeShape() {
        val barcode = BarcodeGenerator.generateBarcode("0123456789abcdef", "12345", 9_999_999_999L)

        assertEquals(22, barcode.length)
        assertEquals("9999999999", barcode.take(10))
    }
}
