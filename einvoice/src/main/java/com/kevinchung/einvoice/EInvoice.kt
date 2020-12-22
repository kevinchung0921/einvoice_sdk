package com.kevinchung.einvoice

import android.util.Log
import com.kevinchung.einvoice.Const.API_EINV_HOST
import com.kevinchung.einvoice.Const.API_EINV_PAPER
import com.kevinchung.einvoice.Const.EINV_ACTION
import com.kevinchung.einvoice.Const.EINV_ACTION_WIN_LIST
import com.kevinchung.einvoice.Const.EINV_APP_ID
import com.kevinchung.einvoice.Const.EINV_INV_TERM
import com.kevinchung.einvoice.Const.EINV_UUID
import com.kevinchung.einvoice.Const.EINV_VERSION
import com.kevinchung.einvoice.Const.EINV_WINLIST_VERSION
import com.kevinchung.einvoice.Const.RSP_CODE
import com.kevinchung.einvoice.Const.RSP_OK
import com.kevinchung.einvoice.data.CarrierHeader
import com.kevinchung.einvoice.data.Invoice
import com.kevinchung.einvoice.data.InvoiceDetail
import com.kevinchung.einvoice.data.WinList
import com.kevinchung.einvoice.interfaces.CarrierHeadersListener
import com.kevinchung.einvoice.interfaces.InvoiceDetailsListener
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class EInvoice
/**
 * Default constructor
 * @param apiKey API key for access e-invoice, apply at
 *               https://www.einvoice.nat.gov.tw/APCONSUMER/BTC605W/ if not have it yet
 */
    (
        private var apiKey: String,  // API key for access e-invoice service
        private var uuid: String = "00000000"
    ) {

    companion object {
        // offset of time stamp, unit in seconds
        const val TIME_STAMP_OFFSET = 120
    }


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

    /**
     * Get win list
     * @param term of win list
     * @return InvoiceDetail object or null if fail
     */

    fun getWinList(term:String): WinList? {
        try {
            val apiService = EInvoiceApiService.createService(EInvApiInterface::class.java)
            return apiService.getWinList(
                term = term,
                uuid = uuid,
                appId = apiKey
            ).execute().body()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            LOG.e("GetInvDetail Error :$e")
        }
        return null
    }

    private fun processWinListRsp(rsp: String?): WinList? {
        if (rsp == null || rsp.trim { it <= ' ' } == "") return null
        val json = JSONObject(rsp)
        LOG.d("msg:$json")
        if (json.getInt(RSP_CODE) == RSP_OK) {
            return WinList(
                v = json.optString("v"),
                code = json.optInt("code"),
                msg = json.optString("msg"),
                invoYm = json.optString("invoYm"),
                superPrizeNo = json.optString("superPrizeNo"),
                spcPrizeNo = json.optString("spcPrizeNo"),
                spcPrizeNo2 = json.optString("spcPrizeNo2"),
                spcPrizeNo3 = json.optString("spcPrizeNo3"),
                firstPrizeNo1 = json.optString("firstPrizeNo1"),
                firstPrizeNo2 = json.optString("firstPrizeNo2"),
                firstPrizeNo3 = json.optString("firstPrizeNo3"),
                firstPrizeNo4 = json.optString("firstPrizeNo4"),
                firstPrizeNo5 = json.optString("firstPrizeNo5"),
                firstPrizeNo6 = json.optString("firstPrizeNo6"),
                firstPrizeNo7 = json.optString("firstPrizeNo7"),
                firstPrizeNo8 = json.optString("firstPrizeNo8"),
                firstPrizeNo9 = json.optString("firstPrizeNo9"),
                firstPrizeNo10 = json.optString("firstPrizeNo10"),
                sixthPrizeNo1 = json.optString("sixthPrizeNo1"),
                sixthPrizeNo2 = json.optString("sixthPrizeNo2"),
                sixthPrizeNo3 = json.optString("sixthPrizeNo3"),
                superPrizeAmt = json.optString("superPrizeAmt"),
                spcPrizeAmt = json.optString("spcPrizeAmt"),
                firstPrizeAmt = json.optString("firstPrizeAmt"),
                secondPrizeAmt = json.optString("secondPrizeAmt"),
                thirdPrizeAmt = json.optString("thirdPrizeAmt"),
                fourthPrizeAmt = json.optString("fourthPrizeAmt"),
                fifthPrizeAmt = json.optString("fifthPrizeAmt"),
                sixthPrizeNo4 = json.optString("sixthPrizeNo4"),
                sixthPrizeNo5 = json.optString("sixthPrizeNo5"),
                sixthPrizeNo6 = json.optString("sixthPrizeNo6")
            )
        }
        return null
    }

    fun getWinList1(term:String): WinList? {
        var res:String?
        var queryString = ""

        queryString += "$EINV_VERSION=$EINV_WINLIST_VERSION&"
        queryString += "$EINV_ACTION=$EINV_ACTION_WIN_LIST&"
        queryString += "$EINV_INV_TERM=$term&"
        queryString += "$EINV_UUID=$uuid&"
        queryString += "$EINV_APP_ID=$apiKey"

        try {
            res = Utils.httpPost(
                API_EINV_HOST+API_EINV_PAPER,
                queryString
            )
            LOG.d("result:$res")
            return processWinListRsp(res)
        } catch (e: java.lang.Exception) {
            LOG.e("Error :$e")
        }

        return null
    }

}
