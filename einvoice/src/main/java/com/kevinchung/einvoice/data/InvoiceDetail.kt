package com.kevinchung.einvoice.data

import com.kevinchung.einvoice.Const
import org.json.JSONObject
/*
 * "v":"<版本號碼>",
 * "code":"<訊息回應碼>",
 * "msg":"<系統回應訊息>",
 * "invNum":"<發票號碼>",
 * "invDate":"<發票開立日期>",
 * "sellerName":"<賣方名稱>",
 * "invStatus":"<發票狀態>",
 * "invPeriod ":"<對獎發票期別>",
 * "sellerBan":"<賣方營業人統編(文字)>" ,
 * "sellerAddress":"<賣方營業人地址(文字)>" ,
 * "invoiceTime":"<發票開立時間(HH:mm:ss)>" ,
 * "buyerBan":"<買方營業人統編(文字)>" ,
 * "currency":"<幣別>" ,
* */

data class InvoiceDetail(
    val version     :String = "",
    val code        :Int = Const.RSP_OK,
    val msg         :String = "",
    val invNumber   :String = "",
    val date        :String = "",
    val sellerName  :String = "",
    val status      :String = "",
    val invPeriod   :String = "",
    val sellerBan   :String = "",
    val sellerAddr  :String = "",
    val invTime     :String = "",
    val buyerBan    :String = "",
    val currency    :String = "",
    val amount      :Double = 0.0 // only for carrier invoice
) {
    val details: ArrayList<ProductDetail> = ArrayList()

    constructor(json:JSONObject):this(
        json.optString(Const.RSP_VERSION),
        json.getInt(Const.RSP_CODE),
        json.getString(Const.RSP_MSG),
        json.optString(Const.RSP_INV_NUM,""),
        json.optString(Const.RSP_INV_DATE,""),
        json.optString(Const.RSP_SELLER_NAME,""),
        json.optString(Const.RSP_INV_STATUS,""),
        json.optString(Const.RSP_INV_PERIOD, ""),
        json.optString(Const.RSP_SELLER_BAN, ""),
        json.optString(Const.RSP_SELLER_ADDRESS, ""),
        json.optString(Const.RSP_INVOICE_TIME, ""),
        json.optString(Const.RSP_BUYER_BAN, ""),
        json.optString(Const.RSP_CURRENCY, ""),
        json.optDouble(Const.RSP_AMOUNT, 0.0)

    ) {
        if(json.has(Const.RSP_DETAILS)) {
            val array = json.getJSONArray(Const.RSP_DETAILS)
            for (detail in 0 until array.length()) {
                details.add(
                    ProductDetail(
                        array.getJSONObject(detail)
                    )
                )
            }
        }
    }

    /**
     * Generate the Map type data structure
     * @return carrier detail information
     */
    fun toHashMap():HashMap<String,String> {
        val map = HashMap<String,String>()
        map[Const.RSP_VERSION] = version
        map[Const.RSP_CODE] = ""+code
        map[Const.RSP_MSG] = msg
        map[Const.RSP_INV_NUM] = invNumber
        map[Const.RSP_INV_DATE] = date
        map[Const.RSP_INVOICE_TIME] = invTime
        map[Const.RSP_SELLER_NAME] = sellerName
        map[Const.RSP_SELLER_BAN] = sellerBan
        map[Const.RSP_SELLER_ADDRESS] = sellerAddr
        map[Const.RSP_BUYER_BAN] = buyerBan
        map[Const.RSP_AMOUNT] = amount.toString()
        map[Const.RSP_INV_STATUS] = status
        map[Const.RSP_CURRENCY] = currency
        return map
    }

    /**
     * Get product detail information
     * @return List of purchased product information in Map format
     */
    private fun getDetailList():ArrayList<HashMap<String,String>> {
        val list = ArrayList<HashMap<String,String>>()
        details?.run {
            for(product in this)
                list.add(product.toHashMap())
        }
        return list
    }
}