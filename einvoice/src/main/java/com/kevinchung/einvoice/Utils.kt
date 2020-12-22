package com.kevinchung.einvoice

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

object Utils {



    private fun trustAllHosts() { // Create a trust manager that does not validate certificate chains
        val trustAllCerts =
            arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>, authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate>, authType: String
                ) {
                }
            })
        // Install the all-trusting trust manager
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection
                .setDefaultSSLSocketFactory(sc.socketFactory)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    @Throws(IOException::class)
    fun okhttpPost(endpoint:String, queryString:String): String? {
        try {
            val client = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
            val req: Request = Request.Builder().url("$endpoint?$queryString").post(RequestBody.create(
                null, byteArrayOf())).build()
            return client.newCall(req).execute().body()?.string()
        } catch (e: java.lang.Exception) {
            LOG.e("Error on send stuff data to server:$e")
        }
        return null
    }

    /**
     *  Http post function
     *  @param endpoint api path
     *  @param queryString query string
     *  @return server response string or null when exception
     */

    @Throws(IOException::class)
    fun httpPost(endpoint: String, queryString: String): String? {

        val url = try {
            URL(endpoint)
        } catch (e: MalformedURLException) {
            throw IllegalArgumentException("invalid url: $endpoint")
        }
        val bytes = queryString.toByteArray()
        var conn: HttpsURLConnection? = null
        try {
            trustAllHosts()
            conn = url.openConnection() as HttpsURLConnection
            conn.hostnameVerifier = HostnameVerifier { _, _ -> true }
            conn!!.doOutput = true
            conn.useCaches = false
            conn.setFixedLengthStreamingMode(bytes.size)
            conn.requestMethod = "POST"
            conn.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded;charset=UTF-8"
            )
            // post the request
            val out = conn.outputStream
            out.write(bytes)
            out.close()
            /* handle the response */
            val status = conn.responseCode
            if (status != 200) {
                LOG.e("Error Code:$status")
                throw IOException("Post failed with error code $status")
            }
            val r =
                BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
            val total = StringBuilder()
            var line: String?
            while (r.readLine().also { line = it } != null) {
                total.append(line).append('\n')
            }
            return total.toString()
        } catch (e: Exception) {
            LOG.e("Error:$e")
        } finally {
            conn?.disconnect()
        }
        return null
    }

}