package com.kevinchung.einvoice

import android.util.Log
import com.kevinchung.einvoice.data.CarrierHeader
import com.kevinchung.einvoice.data.Invoice
import com.kevinchung.einvoice.data.InvoiceDetail
import com.kevinchung.einvoice.interfaces.CarrierHeadersListener
import com.kevinchung.einvoice.interfaces.InvoiceDetailsListener
import java.text.SimpleDateFormat
import java.util.*

class EInvoice
/**
 * Default constructor
 * @param apiKey API key for access e-invoice, apply at
 *               https://www.einvoice.nat.gov.tw/APCONSUMER/BTC605W/ if not have it yet
 */
    (
        private var apiKey: String  // API key for access e-invoice service
    ) {

    companion object {
        // offset of time stamp, unit in seconds
        const val TIME_STAMP_OFFSET = 120
    }

    var uuid = "00000000"

    // show debug log, default disable
    var enableLog = false
        set(value) {
            LOG.enableLog = value
            field = value
        }


    private fun genTimeStamp(): String {
        // add some time offset to avoid failure by network latency
        return "" + (Date().time / 1000 + TIME_STAMP_OFFSET)
    }

    /**
     * Query user's carrier headers in specified duration. Need to check the code to know is query
     * success or not.
     * @param start start time by unix epoch milliseconds
     * @param end  end time by unix epoch milliseconds
     * @param barcode user's barcode
     * @param password user's password
     * @param onlyWin show win invoice only
     * @param listener listener interface for async call
     * @return void
     */

    fun getCarrierHeader(
        start      :Long,
        end        :Long,
        barcode    :String,
        password   :String,
        onlyWin    :Boolean,
        listener   :CarrierHeadersListener
    ) {
        Thread{
            listener.carrierHeaders(getCarrierHeader(start, end, barcode, password, onlyWin))
        }.start()
    }

    /**
     * Query user's carrier headers in specified duration. Need to check the code to know is query
     * success or not.
     * @param start start time by unix epoch milliseconds
     * @param end  end time by unix epoch milliseconds
     * @param barcode user's barcode
     * @param password user's password
     * @param onlyWin show win invoice only
     * @return a list of CarrierHeader objects
     */

    fun getCarrierHeader(
        start      :Long,
        end        :Long,
        barcode    :String,
        password   :String,
        onlyWin    :Boolean
    ):List<CarrierHeader>? {

        val sdf = SimpleDateFormat("yyyy/MM/dd")
        val strStart = sdf.format(Date(start))
        val strEnd = sdf.format(Date(end))

        LOG.d("query period $strStart to $strEnd")

        val timeStamp: String = genTimeStamp()
        try {

            val apiService = EInvoiceApiService.createService(EInvApiInterface::class.java)

            val details =  apiService.getCarrierHeader(
                startDate = strStart,
                endDate = strEnd,
                cardNo = barcode,
                cardEncrypt = password,
                onlyWin = if(onlyWin) "Y" else "N",
                uuid = uuid,
                appId = apiKey,
                timeStamp = timeStamp
            ).execute().body()?.details
            details?.let {
                Log.d("Debug","detail size: ${it.size}")
                for(d in it) {
                    Log.d("debug","num: ${d.invNum} date: ${d.invDate} no: ${d.cardNo} ")
                }
            }

            return details
        } catch (e: Exception) {
            e.printStackTrace()
            LOG.e("getCarrierHeader Error:$e")
        }
        return null
    }

    /**
     * Get carrier detail information
     * @param header CarrierHeader object for query the detail
     * @param barcode User's barcode
     * @param listener Listener for call back when reuslt got
     * @return void
     */
    fun getCarrierDetail(
        header: CarrierHeader,
        barcode: String,
        password: String,
        listener: InvoiceDetailsListener
    ) {
        Thread{
            listener.invoiceDetails(getCarrierDetail(header, barcode, password))
        }.start()
    }

    /**
     * Get carrier detail information
     * @param header CarrierHeader object for query the detail
     * @param barcode User's barcode
     * @return CarrierDetail or null if query failed
     */
    fun getCarrierDetail(
        header: CarrierHeader,
        barcode: String,
        password: String
    ): InvoiceDetail? {
        LOG.d("Query: " + header.invNum)

        val timeStamp: String = genTimeStamp()
        val sdf = SimpleDateFormat("yyyy/MM/dd")

        try {
            val apiService = EInvoiceApiService.createService(EInvApiInterface::class.java)
            return apiService.getCarrierDetail(
                cardNo = barcode,
                cardEncrypt = password,
                timeStamp = timeStamp,
                invNum = header.invNum,
                invDate = sdf.format(Date(header.invDate.time)),
                appId = apiKey,
                uuid = uuid
            ).execute().body()

        } catch (e: Exception) {
            e.printStackTrace()
            LOG.e("getCarrierDetail error:$e")
        }
        return null
    }



    /**
     * Get invoice detail information
     * @param invoice Invoice object
     * @return InvoiceDetail object or null if fail
     */

    fun getInvDetail(invoice: Invoice): InvoiceDetail? {

        try {
            val apiService = EInvoiceApiService.createService(EInvApiInterface::class.java)
            return apiService.getInvDetail(
                type = invoice.type,
                invNum = invoice.invNum,
                action = invoice.action,
                generation = invoice.generation,
                invTerm = invoice.invTerm,
                invDate = invoice.invDate,
                encrypt = invoice.encrypt,
                sellerId = invoice.sellerId,
                uuid = uuid,
                random = invoice.randomNum,
                appId = apiKey
            ).execute().body()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            LOG.e("GetInvDetail Error :$e")
        }
        return null
    }
}