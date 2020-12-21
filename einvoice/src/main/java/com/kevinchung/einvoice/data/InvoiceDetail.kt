package com.kevinchung.einvoice.data

import com.kevinchung.einvoice.Const

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
    val v               :String = "",
    val code            :Int = Const.RSP_OK,
    val msg             :String = "",
    val invNum          :String = "",
    val invDate         :String = "",
    val sellerName      :String = "",
    val invStatus       :String = "",
    val invPeriod       :String = "",
    val sellerBan       :String = "",
    val sellerAddress   :String = "",
    val invoiceTime     :String = "",
    val buyerBan        :String = "",
    val currency        :String = "",
    val amount          :Double = 0.0, // only for carrier invoice
    val details         :List<ProductDetail>
) {

    /**
     * Generate the Map type data structure
     * @return carrier detail information
     */
    fun toHashMap():HashMap<String,String> {
        val map = HashMap<String,String>()
        map[Const.RSP_VERSION] = v
        map[Const.RSP_CODE] = ""+code
        map[Const.RSP_MSG] = msg
        map[Const.RSP_INV_NUM] = invNum
        map[Const.RSP_INV_DATE] = invDate
        map[Const.RSP_INVOICE_TIME] = invoiceTime
        map[Const.RSP_SELLER_NAME] = sellerName
        map[Const.RSP_SELLER_BAN] = sellerBan
        map[Const.RSP_SELLER_ADDRESS] = sellerAddress
        map[Const.RSP_BUYER_BAN] = buyerBan
        map[Const.RSP_AMOUNT] = amount.toString()
        map[Const.RSP_INV_STATUS] = invStatus
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