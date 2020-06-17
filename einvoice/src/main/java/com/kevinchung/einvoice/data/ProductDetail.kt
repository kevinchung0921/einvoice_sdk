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
        json.getInt(Const.RSP_ROW_NUM),
        json.getString(Const.RSP_DESCRIPTION),
        json.getDouble(Const.RSP_QUANTITY),
        json.getDouble(Const.RSP_UNIT_PRICE),
        json.getDouble(Const.RSP_AMOUNT)
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