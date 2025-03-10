package icu.samnyan.aqua.sega.aimedb

import icu.samnyan.aqua.sega.aimedb.AimeDbClient.Companion.sendAimePacket
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import java.net.Socket

class AimeDbClient(val gameId: String, val keychipShort: String) {
    // https://sega.bsnk.me/allnet/aimedb/common/#packet-header
    fun createRequest(type: UShort, writer: ByteBuf.() -> Unit): ByteBuf
        = AimeDbEncryption.encrypt(Unpooled.buffer(1024).clear().run {
            writeShortLE(0xa13e)            // 00  2b: Magic
            writeShortLE(0x3087)            // 02  2b: Version
            writeShortLE(type.toInt())      // 04  2b: Type
            writeShortLE(0)                 // 06  2b: Length
            writeShortLE(0)                 // 08  2b: Result
            writeAscii(gameId, 6)           // 0A  6b: Game ID
            writeIntLE(0)                   // 10  4b: Store ID (Place ID)
            writeAscii(keychipShort, 12)    // 14 12b: Keychip ID
            writer()                        // Write Payload
            setShortLE(6, writerIndex())    // Update Length
            copy(0, writerIndex())          // Trim unused bytes
        })

    private fun ByteBuf.writeAscii(value: String, length: Int)
        = writeBytes(value.toByteArray(Charsets.US_ASCII).copyOf(length))

    fun createReqLookupV2(accessCode: String)
        = createRequest(0x0fu) {
            // Access code is a 20-digit number, should be converted to a 10-byte array
            writeBytes(ByteBufUtil.decodeHexDump(accessCode.padStart(20, '0')))
            writeByte(0)  // 0A  1b: Company code
            writeByte(0)  // 0B  1b: R/W Firmware version
            writeIntLE(0) // 0C  4b: Serial number
        }

    fun createReqFelicaLookupV2(felicaIdm: String)
        = createRequest(0x11u) {
            writeBytes(ByteArray(16))   // 00 16b: Random Challenge
            // 10  8b: Felica IDm
            writeBytes(ByteBufUtil.decodeHexDump(felicaIdm.padStart(16, '0')))
            writeBytes(ByteArray(8))    // 18  8b: Felica PMm
            writeBytes(ByteArray(16))   // 20 16b: Card key version
            writeBytes(ByteArray(16))   // 30 16b: Write count
            writeBytes(ByteArray(8))    // 40  8b: MACA
            writeByte(0)                // 48  1b: Company code
            writeByte(0)                // 49  1b: R/W Firmware version
            writeShortLE(0)             // 4A  2b: DFC
            writeIntLE(0)               // 4C  4b: Unknown padding
        }

    companion object {
        fun ByteBuf.sendAimePacket(server: String): ByteBuf
            = Unpooled.wrappedBuffer(Socket(server, 22345).use {
                it.getOutputStream().write(array())
                it.getInputStream().readBytes()
            })
    }
}
