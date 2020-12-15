package com.kevinchung.einvoice

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.kevinchung.einvoice.data.CarrierHeader
import com.kevinchung.einvoice.data.Invoice
import com.kevinchung.einvoice.data.InvoiceDetail
import com.kevinchung.einvoice.interfaces.CarrierHeadersListener
import com.kevinchung.einvoice.interfaces.InvoiceDetailsListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class EInvoice {

    companion object {
        // offset of time stamp, unit in seconds
        const val TIME_STAMP_OFFSET = 120
    }

    // API key for access e-invoice service
    private var apiKey  : String = ""

    var uuid = "00000000"

    // show debug log, default disable
    var enableLog = false
        set(value) {
            LOG.enableLog = value
            field = value
        }


    /**
     * Default constructor
     * @param apiKey API key for access e-invoice, apply at
     *               https://www.einvoice.nat.gov.tw/APCONSUMER/BTC605W/ if not have it yet
     */
    constructor(apiKey:String) {
        this.apiKey = apiKey
    }

    private fun genTimeStamp(): String {
        // add some time offset to avoid failure by network latency
        return "" + (Date().time / 1000 + TIME_STAMP_OFFSET)
    }

    private fun kv(key:String, value:String):String {
        return "$key=$value&"
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

            return apiService.getCarrierHeader(
                startDate = strStart,
                endDate = strEnd,
                cardNo = URLEncoder.encode(barcode,"UTF-8"),
                cardEncrypt = URLEncoder.encode(password, "UTF-8"),
                onlyWin = if(onlyWin) "Y" else "N",
                uuid = uuid,
                appId = apiKey,
                timeStamp = timeStamp
            ).execute().body()?.details

            val list = ArrayList<CarrierHeader>()
            val body = StringBuilder()
            /*
             * version=0.1&
             * cardType=3J0002&
             * cardNo=/ABCDEFG&
             * expTimeStamp=1390718268&
             * action=carrierInvChk&
             * timeStamp=1390812157&
             * startDate=2013/12/01&
             * endDate=2013/12/30&
             * onlyWinningInv=N&
             * uuid=00000001&
             * appID=EINV8201401046705&
             * cardEncrypt=xxxxxx
             * */
            body.append(kv(Const.EINV_VERSION, Const.EINV_CARRIER_HEADER_VERSION))
            body.append(kv(Const.EINV_CARD_TYPE, Const.EINV_CARD_CARRIER_BARCODE))
            body.append(kv(Const.EINV_CARD_NO, URLEncoder.encode(barcode,"UTF-8")))
            body.append(kv(Const.EINV_EXP_TIME_STAMP,"2147483647"))
            body.append(kv(Const.EINV_ACTION,Const.EINV_ACTION_CARRIER_HEADER))
            body.append(kv(Const.EINV_TIME_STAMP, timeStamp))
            body.append(kv(Const.EINV_START_DATE,strStart))
            body.append(kv(Const.EINV_END_DATE,strEnd))
            body.append(kv(Const.EINV_ONLY_WIN_LIST,if(onlyWin) "Y" else "N"))
            body.append(kv(Const.EINV_UUID_0_5,uuid))
            body.append(kv(Const.EINV_APP_ID,apiKey))
            body.append("${Const.EINV_CARD_ENCRYPT}=${URLEncoder.encode(password, "UTF-8")}")
            
            LOG.d("body=${Const.API_EINV_CARRIER}?$body")
            var res = Utils.httpPost(Const.API_EINV_CARRIER, body.toString())
            LOG.d("Query header result: $res ")

            if (res == null || res.isEmpty())
                return null

            val json = JSONObject(res)
            json.getString(Const.RSP_MSG)
            if (json.getInt(Const.RSP_CODE) == Const.RSP_OK) {
                val array = json.getJSONArray(Const.RSP_DETAILS)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(CarrierHeader(obj))
                }
            } else {
                val s = res.split("\n").toTypedArray()
                val obj = if (s.size > 1) JSONObject(s[1]) else JSONObject(s[0])
                LOG.e("查詢發票表頭發生錯誤:${obj.getString(Const.RSP_MSG)} [${obj.getInt(Const.RSP_CODE)}]")
                val header = CarrierHeader()
                header.code = obj.getInt(Const.RSP_CODE)
                header.msg = obj.getString(obj.getString(Const.RSP_MSG))
                list.add(header)
                return list
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LOG.e("getCarrierHeader Error:$e")
            return null
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
        LOG.d("Query: " + header.number)
        var body = StringBuilder()
        val timeStamp: String = genTimeStamp()

        try {

            body.append(kv(Const.EINV_VERSION, Const.EINV_CARRIER_DETAIL_VERSION))
            // always use carrier bar code to query the detail, otherwise
            // server will report error when type other than carrier bar code type.
            body.append(kv(Const.EINV_CARD_TYPE,Const.EINV_CARD_CARRIER_BARCODE))
            body.append(kv(Const.EINV_CARD_NO,URLEncoder.encode(barcode,"UTF-8")))
            body.append(kv(Const.EINV_EXP_TIME_STAMP,Const.EXP_TIME))
            body.append(kv(Const.EINV_ACTION, Const.EINV_ACTION_CARRIER_DETAIL))
            body.append(kv(Const.EINV_TIME_STAMP, timeStamp))
            body.append(kv(Const.EINV_INV_NUM, header.number))

            val sdf = SimpleDateFormat("yyyy/MM/dd")
            body.append(kv(Const.EINV_DATE, sdf.format(Date(header.date))))

            body.append(kv(Const.EINV_UUID_0_5, uuid))
            body.append(kv(Const.EINV_APP_ID,apiKey))
            body.append("${Const.EINV_CARD_ENCRYPT}=${URLEncoder.encode(password,"UTF-8")}")
            LOG.d("body=${Const.API_EINV_CARRIER}?$body")
            val res = Utils.httpPost(Const.API_EINV_CARRIER, body.toString())

            LOG.d("query carrier detail response:$res")

            if (res == null || res.trim { it <= ' ' } == "") return null
            val json = JSONObject(res)
            json.getString(Const.RSP_MSG)
            if (json.getInt(Const.RSP_CODE) == Const.RSP_OK) { // save content
                return InvoiceDetail(json)
            } else {
                val s = res.split("\n").toTypedArray()
                val obj = if (s.size > 1) JSONObject(s[1]) else JSONObject(s[0])
                LOG.e(
                    "查詢發票明細發生錯誤:" + obj.getString(Const.RSP_MSG) + " [" + obj.getInt(
                        Const.RSP_CODE
                    ) + "]"
                )
            }
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
        val body = StringBuilder()

        body.append(kv(Const.EINV_VERSION, Const.EINV_DETAIL_VERSION))
        body.append(kv(Const.EINV_TYPE, invoice.type))
        body.append(kv(Const.EINV_INV_NUM, invoice.invNum))
        body.append(kv(Const.EINV_ACTION, invoice.action))
        body.append(kv(Const.EINV_GENERATION, invoice.generation))
        body.append(kv(Const.EINV_INV_TERM, invoice.invTerm))
        if (invoice.type == Const.EINV_QRCODE) {
            body.append(kv(Const.EINV_DATE, invoice.invDate))
            body.append(kv(Const.EINV_ENCRYPT, invoice.encrypt))
            body.append(kv(Const.EINV_SELLER_ID, invoice.sellerId))
        } else {
            body.append(kv(Const.EINV_DATE, ""))
            body.append(kv(Const.EINV_ENCRYPT, ""))
            body.append(kv(Const.EINV_SELLER_ID, ""))
        }
        body.append(kv(Const.EINV_UUID_0_4, uuid))
        body.append(kv(Const.EINV_RANDOM, invoice.randomNum))
        body.append("${Const.EINV_APP_ID}=${apiKey}")
        LOG.d(Const.API_EINV_PAPER + "?" + body)
        try {
            var result = Utils.httpPost(Const.API_EINV_PAPER, body.toString())
            LOG.d("response:$result")
            return if (result == "") null
            else InvoiceDetail(JSONObject(result))

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            LOG.e("GetInvDetail Error :$e")
        }
        return null
    }
}