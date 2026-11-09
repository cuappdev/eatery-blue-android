package com.cornellappdev.android.eatery.util

import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Generates the numeric 22-digit GET payment barcode value locally. Port of generate_barcode.py,
 * so the same inputs must produce the same value as the reference implementation.
 *
 * Do not log the seed, patron ID or generated value.
 */
object BarcodeGenerator {
    private const val HMAC_ALGORITHM = "HmacSHA256"
    private const val OTP_MODULUS = 1_000_000_000
    private const val VERSION_DIGIT = 1
    private const val PADDED_LENGTH = 10

    /** Smallest timestamp that no longer fits in [PADDED_LENGTH] digits. */
    private const val MAX_TIMESTAMP_EXCLUSIVE = 10_000_000_000L
    private const val PATRON_BYTE_COUNT = 4
    private const val LONG_BYTE_COUNT = 8

    private val HEX_REGEX = Regex("([0-9a-fA-F]{2})+")

    /** Hardcoded GET garble: base64 NTBGQ0RGM0ZFNDIyQTBBNDY5RkU= decodes to this ASCII hex. */
    private val GARBLE_BYTES = hexToBytes("50FCDF3FE422A0A469FE")

    /**
     * Returns `[10-digit timestamp][version 1][10-digit payload][1 check digit]`.
     *
     * @param seedHex the user's barcodeSeed from GET, as a hex string
     * @param cashlessKey the user's numeric patronId from GET. Only its low [PATRON_BYTE_COUNT]
     * bytes feed the payload, since the ten-digit payload holds at most 2³² − 1; this matches
     * generate_barcode.py and iOS. The check digit uses the full ID string.
     * @param timestamp unix time in seconds; must be non-negative and fit in ten digits
     * @throws IllegalArgumentException if any input is malformed
     */
    fun generateBarcode(seedHex: String, cashlessKey: String, timestamp: Long): String {
        require(timestamp in 0 until MAX_TIMESTAMP_EXCLUSIVE) {
            "Barcode timestamp must be non-negative and fit in ten digits"
        }
        val patronId = requireNotNull(cashlessKey.toULongOrNull()) {
            "Patron ID must be numeric"
        }

        val hmacKey = xorEncrypt(GARBLE_BYTES, hexToBytes(seedHex))
        val otp = generateOtp(hmacKey, timestamp)
        val patronBytes = longToLittleEndianBytes(patronId.toLong()).copyOf(PATRON_BYTE_COUNT)
        val payload = littleEndianBytesToLong(
            xorEncrypt(patronBytes, longToLittleEndianBytes(otp.toLong()))
        )

        return tenDigits(timestamp) + VERSION_DIGIT + tenDigits(payload) +
                generateCheckDigit(cashlessKey)
    }

    /** XORs each byte of [data] with [key], repeating [key] if it is shorter than [data]. */
    private fun xorEncrypt(data: ByteArray, key: ByteArray): ByteArray =
        ByteArray(data.size) { index ->
            (data[index].toInt() xor key[index % key.size].toInt()).toByte()
        }

    /** Returns the Luhn check digit for [cashlessKey], used as the barcode's last digit. */
    private fun generateCheckDigit(cashlessKey: String): Int {
        val remainder = luhnTotal(cashlessKey) % 10
        return if (remainder == 0) 0 else 10 - remainder
    }

    /** Sums the digits of [number] from the right, doubling every other digit, first included. */
    private fun luhnTotal(number: String): Int {
        var total = 0
        var shouldDouble = true
        for (character in number.reversed()) {
            val digit = character.digitToIntOrNull() ?: continue
            total += when {
                !shouldDouble -> digit
                digit * 2 > 9 -> (digit * 2) % 10 + 1
                else -> digit * 2
            }
            shouldDouble = !shouldDouble
        }
        return total
    }

    private fun generateOtp(hmacKey: ByteArray, timestamp: Long): Int {
        val counter = ByteBuffer.allocate(LONG_BYTE_COUNT).putLong(timestamp).array()
        val digest = Mac.getInstance(HMAC_ALGORITHM).run {
            init(SecretKeySpec(hmacKey, HMAC_ALGORITHM))
            doFinal(counter)
        }

        val offset = digest.last().toInt() and 0x0F
        val binary = ByteBuffer.wrap(digest, offset, Int.SIZE_BYTES).int and Int.MAX_VALUE
        return binary % OTP_MODULUS
    }

    private fun longToLittleEndianBytes(value: Long): ByteArray =
        ByteBuffer.allocate(LONG_BYTE_COUNT).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array()

    private fun littleEndianBytesToLong(bytes: ByteArray): Long =
        bytes.foldIndexed(0L) { index, result, byte ->
            result or ((byte.toLong() and 0xFF) shl (Byte.SIZE_BITS * index))
        }

    /**
     * Parses [hex] into bytes, ignoring whitespace.
     *
     * @throws IllegalArgumentException if [hex] is empty, has an odd length or isn't hex
     */
    private fun hexToBytes(hex: String): ByteArray {
        val cleaned = hex.filterNot(Char::isWhitespace)
        require(HEX_REGEX.matches(cleaned)) { "Barcode seed must be valid hex" }
        return ByteArray(cleaned.length / 2) { index ->
            cleaned.substring(index * 2, index * 2 + 2).toInt(16).toByte()
        }
    }

    private fun tenDigits(value: Long): String = value.toString().padStart(PADDED_LENGTH, '0')
}
