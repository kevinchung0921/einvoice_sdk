package com.kevinchung.einvoice.data

import com.kevinchung.einvoice.Const
import org.json.JSONObject

/**
 * JSON response for product detail
 * "rowNum":"<第 1 筆明細編號>",
 * "description":"<品名 1>",
 * "quantity":"<數量 1>",
 * "unitPrice":"<單價 1>",
 * "amount":"<小計 1>"
 */

data class ProductDetail (
    val row         :Int = 0,
    val description :String = "",
    val quantity    :Double = 0.0,
    val unitPrice   :Double = 0.0,
    val amount      :Double = 0.0
    ) {

    constructor(json:JSONObject):this (
        json.optInt(Const.RSP_ROW_NUM, 0),
        json.optString(Const.RSP_DESCRIPTION,""),
        json.optDouble(Const.RSP_QUANTITY, 1.0),
        json.optDouble(Const.RSP_UNIT_PRICE, 0.0),
        json.optDouble(Const.RSP_AMOUNT, 0.0)
    )

    fun toHashMap():HashMap<String,String> {
        val map = HashMap<String,String>()
        map[Const.RSP_ROW_NUM] = row.toString()
        map[Const.RSP_DESCRIPTION] = description
        map[Const.RSP_QUANTITY] = quantity.toString()
        map[Const.RSP_UNIT_PRICE] = unitPrice.toString()
        map[Const.RSP_AMOUNT] = amount.toString()
        return map
    }
}