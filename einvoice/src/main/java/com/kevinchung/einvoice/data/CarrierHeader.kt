package com.kevinchung.einvoice.data

import com.kevinchung.einvoice.Const
import org.json.JSONObject
import java.util.*

data class CarrierHeader(
    val rowNum          :Int = 0,
    val invNum          :String = "",
    val cardType        :String = "",
    val cardNo          :String = "",
    val sellerName      :String = "",
    val invStatus       :String = "",
    val invDonatable    :String = "",
    val amount          :Double = 0.0,
    val invPeriod       :String = "",
    val sellerBan       :String = "",
    val sellerAddress   :String = "",
    val invoiceTime     :String = "",
    val buyerBan        :String = "",
    val currency        :String = "",
    val invDate         : InvDate,
    val donateMark      :String = ""
    )