package com.shushly.app.domain.engine

import kotlin.random.Random

data class DnsQuestion(
    val name: String,
    val qType: Int,
    val qClass: Int
)

data class DnsResponseSummary(
    val id: Int,
    val rcode: Int,
    val isBlocked: Boolean,
    val answerIps: List<String>
)

object DnsPacketParser {

    /**
     * Builds standard DNS query wire format for a given domain and query type (default A record = 1)
     */
    fun buildQuery(domain: String, qType: Int = 1, id: Int = Random.nextInt(1, 0xFFFF)): ByteArray {
        val out = mutableListOf<Byte>()
        // ID (2 bytes)
        out.add(((id shr 8) and 0xFF).toByte())
        out.add((id and 0xFF).toByte())

        // Flags: RD = 1 (standard query with recursion desired) -> 0x0100
        out.add(0x01.toByte())
        out.add(0x00.toByte())

        // QDCOUNT: 1 question
        out.add(0x00.toByte())
        out.add(0x01.toByte())

        // ANCOUNT, NSCOUNT, ARCOUNT: 0
        repeat(6) { out.add(0x00.toByte()) }

        // Question: QNAME
        val labels = domain.trim().trimEnd('.').split('.')
        for (label in labels) {
            val bytes = label.encodeToByteArray()
            out.add((bytes.size and 0xFF).toByte())
            for (b in bytes) {
                out.add(b)
            }
        }
        out.add(0x00.toByte()) // Root zero label

        // QTYPE (2 bytes)
        out.add(((qType shr 8) and 0xFF).toByte())
        out.add((qType and 0xFF).toByte())

        // QCLASS: 1 (IN)
        out.add(0x00.toByte())
        out.add(0x01.toByte())

        return out.toByteArray()
    }

    /**
     * Extracts questions and requested domain from incoming DNS query wire bytes.
     */
    fun parseQuestion(packet: ByteArray): DnsQuestion? {
        if (packet.size < 12) return null
        var offset = 12
        val nameBuilder = StringBuilder()

        try {
            var encounteredZeroLabel = false
            while (offset < packet.size) {
                val len = packet[offset].toInt() and 0xFF
                offset++
                if (len == 0) {
                    encounteredZeroLabel = true
                    break
                }
                // Question names must not use compression pointers
                if ((len and 0xC0) == 0xC0) {
                    return null
                }
                if (offset + len > packet.size) return null
                if (nameBuilder.isNotEmpty()) nameBuilder.append('.')
                nameBuilder.append(packet.decodeToString(offset, offset + len))
                offset += len
            }

            if (!encounteredZeroLabel || nameBuilder.isEmpty()) return null
            if (offset + 4 > packet.size) return null
            val qType = ((packet[offset].toInt() and 0xFF) shl 8) or (packet[offset + 1].toInt() and 0xFF)
            offset += 2
            val qClass = ((packet[offset].toInt() and 0xFF) shl 8) or (packet[offset + 1].toInt() and 0xFF)

            return DnsQuestion(
                name = nameBuilder.toString(),
                qType = qType,
                qClass = qClass
            )
        } catch (_: Exception) {
            return null
        }
    }

    /**
     * Parses a DNS response packet to check response code and whether the query was blocked (NXDOMAIN or 0.0.0.0)
     */
    fun parseResponse(packet: ByteArray): DnsResponseSummary? {
        if (packet.size < 12) return null
        val id = ((packet[0].toInt() and 0xFF) shl 8) or (packet[1].toInt() and 0xFF)
        val flags = ((packet[2].toInt() and 0xFF) shl 8) or (packet[3].toInt() and 0xFF)
        val rcode = flags and 0x0F
        val anCount = ((packet[6].toInt() and 0xFF) shl 8) or (packet[7].toInt() and 0xFF)

        // If NXDOMAIN (rcode == 3), it was blocked or nonexistent
        if (rcode == 3) {
            return DnsResponseSummary(id = id, rcode = rcode, isBlocked = true, answerIps = emptyList())
        }

        // Try reading answer IPs
        val ips = mutableListOf<String>()
        var isBlocked = false

        try {
            var offset = 12
            // Skip Question Section
            val qdCount = ((packet[4].toInt() and 0xFF) shl 8) or (packet[5].toInt() and 0xFF)
            for (q in 0 until qdCount) {
                while (offset < packet.size) {
                    val len = packet[offset].toInt() and 0xFF
                    offset++
                    if (len == 0) break
                    if ((len and 0xC0) == 0xC0) {
                        offset++
                        break
                    }
                    offset += len
                }
                offset += 4 // QTYPE + QCLASS
            }

            // Read Answers
            for (a in 0 until anCount) {
                if (offset >= packet.size) break
                // Name (could be compression pointer)
                val len = packet[offset].toInt() and 0xFF
                if ((len and 0xC0) == 0xC0) {
                    offset += 2
                } else {
                    while (offset < packet.size) {
                        val l = packet[offset].toInt() and 0xFF
                        offset++
                        if (l == 0) break
                        offset += l
                    }
                }
                if (offset + 10 > packet.size) break
                val type = ((packet[offset].toInt() and 0xFF) shl 8) or (packet[offset + 1].toInt() and 0xFF)
                offset += 8 // TYPE (2) + CLASS (2) + TTL (4)
                val rdLength = ((packet[offset].toInt() and 0xFF) shl 8) or (packet[offset + 1].toInt() and 0xFF)
                offset += 2

                if (type == 1 && rdLength == 4 && offset + 4 <= packet.size) {
                    // IPv4
                    val ip = "${packet[offset].toInt() and 0xFF}.${packet[offset + 1].toInt() and 0xFF}.${packet[offset + 2].toInt() and 0xFF}.${packet[offset + 3].toInt() and 0xFF}"
                    ips.add(ip)
                    if (ip == "0.0.0.0" || ip == "127.0.0.1") {
                        isBlocked = true
                    }
                } else if (type == 28 && rdLength == 16 && offset + 16 <= packet.size) {
                    // IPv6
                    val isAllZero = (0 until 16).all { packet[offset + it] == 0.toByte() }
                    if (isAllZero) {
                        isBlocked = true
                        ips.add("::")
                    }
                }
                offset += rdLength
            }
        } catch (_: Exception) {
            // Safe fallback
        }

        return DnsResponseSummary(
            id = id,
            rcode = rcode,
            isBlocked = isBlocked,
            answerIps = ips
        )
    }
}
