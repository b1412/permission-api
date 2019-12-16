package com.github.b1412.security

import arrow.core.Success
import arrow.core.Try
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper


class AuthenticationRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    val payload: String


    init {
        val stringBuilder = StringBuilder()
        var bufferedReader: BufferedReader?

        val inputStream = request.inputStream
        if (inputStream != null) {
            bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val charBuffer = CharArray(128)
            var bytesReadTry = Try { bufferedReader.read(charBuffer) }
            when (bytesReadTry) {
                is Success -> {
                    var bytesRead = bytesReadTry.value
                    while (bytesRead > 0) {
                        stringBuilder.append(charBuffer, 0, bytesRead)
                        bytesRead = bufferedReader.read(charBuffer)
                    }
                }
            }
        } else {
            stringBuilder.append("")
        }
        payload = stringBuilder.toString()
    }

    override fun getInputStream(): ServletInputStream {
        val byteArrayInputStream = ByteArrayInputStream(payload.toByteArray())
        return object : ServletInputStream() {
            override fun read(): Int {
                return byteArrayInputStream.read()
            }

            override fun isFinished(): Boolean {
                return false
            }

            override fun isReady(): Boolean {

                return false
            }

            override fun setReadListener(readListener: ReadListener) {}
        }
    }
}