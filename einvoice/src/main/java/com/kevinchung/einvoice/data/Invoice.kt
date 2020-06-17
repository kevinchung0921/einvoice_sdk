package com.kevinchung.einvoice.data

import com.kevinchung.einvoice.Const
import com.kevinchung.einvoice.LOG
import java.util.*

data class Invoice(
    var type        :String = Const.EINV_QRCODE
) {
    val version     :String = "0.5"
    var invNum      :String = ""
    val action      :String = "qryInvDetail"
    val generation  :String = "V2"
    var invTerm     :String = ""
    var invDate     :String = ""
    var amount      :Int = 0
    var taxAmount   :Int = 0
    var encrypt     :String = ""
    var buyerId     :String = ""
    var sellerId    :String = ""
    var randomNum   :String = ""
    var date        :Date = Date()

    constructor(
        type :String,
        barcode :String
    ):this() {
        if(type == Const.FORMAT_QR_CODE) {
            parseQRCode(barcode)
        } else if(type == Const.FORMAT_CODE_39) {
            parseCode39(barcode)
        }
    }

    private fun parseQRCode(contents: String) {
        var code = contents
        if (code.length < Const.QR_TOTAL_LEN) {
            LOG.e("code len:" + code.length + " : " + code)
            return
        }


        code = code.trim { it <= ' ' }
        // move to first valid character
        while (code.length >= Const.QR_TOTAL_LEN) {
            val c = code[0]
            code = if (c in 'A'..'Z' || c in 'a'..'z'
            ) break else code.substring(1)
        }

        type = Const.EINV_QRCODE
        try { // extract invoice header
            val serial = code.substring(
                Const.QR_SERIAL_START,
                Const.QR_SERIAL_END
            )

            invNum = serial

            val dateString = code.substring(
                Const.QR_DATE_START,
                Const.QR_DATE_END
            )
            invDate = convertDate(dateString)

            invTerm = convertTerm(dateString)

            randomNum = code.substring(
                Const.QR_RANDOM_START,
                Const.QR_RANDOM_END
            )

            amount = Integer.parseInt(code.substring(
                Const.QR_AMOUNT_START,
                Const.QR_AMOUNT_END
            ),16)

            taxAmount = Integer.parseInt(code.substring(
                Const.QR_TAX_AMOUNT_START,
                Const.QR_TAX_AMOUNT_END
            ),16)

            buyerId = code.substring(
                Const.QR_BUYER_ID_START,
                Const.QR_BUYER_ID_END
            )

            sellerId = code.substring(
                Const.QR_SELLER_ID_START,
                Const.QR_SELLER_ID_END
            )
            encrypt = code.substring(
                Const.QR_HASH_START,
                Const.QR_HASH_END
            )



        } catch (e: java.lang.Exception) {
            LOG.e( "Error parsing QR Code:$code")
            LOG.e( "Cause:$e")
        }
    }

    private fun convertDate(dateString:String):String {

        val year = Integer.valueOf(dateString.substring(0, 3))+11
        val month = Integer.valueOf(dateString.substring(3, 5))-1
        val day = Integer.valueOf(dateString.substring(5, 7))
        date = Date(year, month, day)
        return String.format("%d/%02d/%02d", date.getYear()+1900,date.getMonth()+1,date.getDate())
    }

    private fun convertTerm(date:String):String {
        val year = Integer.valueOf(date.substring(0, 3))
        var month = Integer.valueOf(date.substring(3, 5))
        month += month%2
        return String.format("%d%02d",year, month)
    }

    private fun parseCode39(code: String) {
        if (code.length < Const.CODE_39_TOTAL_LEN) {
            LOG.e(
                "code len:" + code.length + " : " + code
            )
            return
        }

        type = Const.EINV_BARCODE

        try {

            invTerm = code.substring(
                Const.CODE_39_DATE_START,
                Const.CODE_39_DATE_END
            )

            invNum = code.substring(
                Const.CODE_39_SERIAL_START,
                Const.CODE_39_SERIAL_END
            )
            randomNum = code.substring(
                Const.CODE_39_RANDOM_START,
                Const.CODE_39_RANDOM_END
            )

        } catch (e: java.lang.Exception) {
            LOG.e( "Error parsing bar code:$code")
        }
    }
}