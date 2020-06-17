package com.kevinchung.einvoice.data

import com.kevinchung.einvoice.Const
import org.json.JSONObject
import java.util.*

data class CarrierHeader(
    var version     :String = "",
    var code        :Int = Const.RSP_OK,
    var msg         :String = "",
    val number      :String = "",
    val cardType    :String = "",
    val cardNo      :String = "",
    val seller      :String = "",
    val status      :String = "",
    val donatable   :String = "",
    val donateMark  :String = "",
    val date        :Long = 0
    ) {


    constructor(json:JSONObject) :
        this(
            json.optString(Const.RSP_VERSION),
            Const.RSP_OK,
            json.optString(Const.RSP_MSG,""),
            json.optString(Const.RSP_INV_NUM,""),
            json.optString(Const.RSP_CARD_TYPE,""),
            json.optString(Const.RSP_CARD_NO,""),
            json.optString(Const.RSP_SELLER_NAME,""),
            json.optString(Const.RSP_INV_STATUS,""),
            json.optString(Const.RSP_DONATABLE,""),
            json.optString(Const.RSP_DONATE_MARK,""),
            json.optJSONObject(Const.RSP_INV_DATE).getLong(
                Const.RSP_TIME
            )
        )


    fun toHashMap():HashMap<String,String> {
        val map = HashMap<String,String>()
        map[Const.RSP_VERSION] = version
        map[Const.RSP_INV_NUM] = number
        map[Const.RSP_CARD_TYPE] = cardType
        map[Const.RSP_CARD_NO] = cardNo
        map[Const.RSP_SELLER_NAME] = seller
        map[Const.RSP_INV_STATUS] = status
        map[Const.RSP_DONATABLE] = donatable
        map[Const.RSP_DONATE_MARK] = donateMark
        map[Const.RSP_INV_DATE] = date.toString()
        return map
    }
}