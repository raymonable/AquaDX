package icu.samnyan.aqua.sega.util

import java.util.*
import kotlin.text.Charsets.UTF_8

object AllNetBillingDecoder {
    /**
     * Decode the input byte array from Base64 MIME encoding and decompress the decoded byte array
     */
    fun decode(src: ByteArray, base64: Boolean, nowrap: Boolean): Map<String, String> {
        // Decode the input byte array from Base64 MIME encoding
        val bytes = if (!base64) src else Base64.getMimeDecoder().decode(src)

        // Decompress the decoded byte array
        val output = ZLib.decompress(bytes, nowrap).toString(UTF_8).trim()

        // Split the string by '&' symbol to separate key-value pairs
        return output.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
    }

    fun encode(src: Map<String, String>, base64: Boolean): ByteArray {
        // Join the key-value pairs with '&' symbol
        val output = src.map { "${it.key}=${it.value}" }.joinToString("&")

        // Compress the joined string
        val bytes = ZLib.compress(output.toByteArray(UTF_8))

        // Encode the compressed byte array to Base64 MIME encoding
        return if (!base64) bytes else Base64.getMimeEncoder().encode(bytes)
    }

    @JvmStatic
    fun decodeAllNet(src: ByteArray) = decode(src, base64 = true, nowrap = false)
    fun encodeAllNet(src: Map<String, String>) = encode(src, base64 = true)

    @JvmStatic
    fun decodeBilling(src: ByteArray) = decode(src, base64 = false, nowrap = true)
}
