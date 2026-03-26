package com.horob1.doc_service.util

import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest

object FileHashUtil {

    fun sha256(inputStream: InputStream): Pair<DigestInputStream, () -> String> {
        val digest = MessageDigest.getInstance("SHA-256")
        val digestInputStream = DigestInputStream(inputStream, digest)
        val getHash: () -> String = {
            digest.digest().joinToString("") { "%02x".format(it) }
        }
        return Pair(digestInputStream, getHash)
    }
}
