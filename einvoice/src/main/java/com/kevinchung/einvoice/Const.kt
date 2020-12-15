package com.kevinchung.einvoice

object Const {
    const val INV_CONFIRMED = "已確認"
    const val INV_NO_RECORD = "該筆發票並無開立"
    const val CURRENT_IMPLEMENT_SPEC_VERSION = "1.7.0"
    const val EINV_DETAIL_VERSION = "0.5"
    const val EINV_CARRIER_HEADER_VERSION = "0.5"
    const val EINV_CARRIER_DETAIL_VERSION = "0.5"


    const val API_EINV_HOST = "https://api.einvoice.nat.gov.tw"
    const val API_EINV_PAPER = "/PB2CAPIVAN/invapp/InvApp"
    const val API_EINV_CARRIER = "/PB2CAPIVAN/invServ/InvServ"

    const val API_EINV_QUERY_BARCODE = "$API_EINV_HOST/PB2CAPIVAN/Carrier/AppGetBarcode"

    const val API_EINV_REGISTER_BLANK =
        "/PB2CAPIVAN/APIService/generalCarrierRegBlank"
    const val API_EINV_CARRIER_BLANK =
        "/PB2CAPIVAN/APIService/carrierLinkBlank"
    const val API_EINV_BANK_BLINK =
        "/PB2CAPIVAN/APIService/carrierBankAccBlank"

    const val EINV_ACT_GET_BARCODE = "getBarcode"

    const val EINV_VERSION = "version"
    const val EINV_ACTION = "action"
    const val EINV_APP_ID = "appID"
    const val EINV_ACTION_DETAIL = "qryInvDetail"
    const val EINV_INV_TERM = "invTerm"
    const val EINV_INV_NUM = "invNum"
    const val EINV_ENCRYPT = "encrypt"
    const val EINV_TYPE = "type"
    const val EINV_DATE = "invDate"
    const val EINV_SELLER_ID = "sellerID"

    const val EINV_UUID = "UUID"
    const val EINV_UUID_0_4 = "UUID"
    const val EINV_UUID_0_5 = "uuid"
    const val EINV_RANDOM = "randomNumber"
    const val EINV_SIGNATURE = "signature"
    const val EINV_PHONE_NUMBER = "phoneNo"
    const val EINV_VERIFICATION_CODE = "verificationCode" // for get barcode API

    const val EINV_VERIFY_CODE = "verifyCode"

    const val EINV_GENERATION = "generation"
    const val EINV_GENERATION_VAL = "V2"
    const val EINV_QRCODE = "QRCode"
    const val EINV_BARCODE = "Barcode"
    const val EINV_CARRIER = "Carrier"




    const val EINV_ONLY_WIN_LIST = "onlyWinningInv"
    const val EINV_CARD_TYPE = "cardType"
    const val EINV_CARD_NO = "cardNo"
    const val EINV_CARD_CODE = "cardCode"
    const val EINV_EXP_TIME_STAMP = "expTimeStamp"
    const val EINV_TIME_STAMP = "timeStamp"
    const val EINV_START_DATE = "startDate"
    const val EINV_END_DATE = "endDate"
    const val EINV_CARD_ENCRYPT = "cardEncrypt"

    // 2015/03/20, new in V1.4.1
    const val EINV_SELLER_NAME = "sellerName"
    const val EINV_AMOUNT = "amount"


    const val EINV_ACTION_CARRIER_HEADER = "carrierInvChk"
    const val EINV_ACTION_CARRIER_DETAIL = "carrierInvDetail"

    const val EINV_CARD_CARRIER_BARCODE = "3J0002"
    const val EINV_CARD_CARRIER_EASYCARD = "1K0001"
    const val EINV_CARD_CARRIER_ICASH = "2G0001"
    const val EINV_CARD_CARRIER_ONEPASS = "1H0001"
    const val RSP_DONATABLE = "invDonatable"
    const val RSP_DONATE_MARK = "donateMark"

    const val RSP_TIME = "time"


    const val RSP_VERSION = "v"
    const val RSP_OK = 200
    const val RSP_CODE = "code"
    const val RSP_MSG = "msg"
    const val RSP_INV_NUM = "invNum"
    const val RSP_INV_DATE = "invDate"
    const val RSP_SELLER_NAME = EINV_SELLER_NAME
    const val RSP_INV_STATUS = "invStatus"
    const val RSP_INV_PERIOD = "invPeriod"
    const val RSP_DETAILS = "details"
    const val RSP_ROW_NUM = "rowNum"
    const val RSP_CARD_TYPE = EINV_CARD_TYPE
    const val RSP_CARD_NO = EINV_CARD_NO

    // 2017/02/10, new in V1.4.4
    const val RSP_SELLER_BAN = "sellerBan"
    const val RSP_SELLER_ADDRESS = "sellerAddress"
    const val RSP_INVOICE_TIME = "invoiceTime"


    const val RSP_DESCRIPTION = "description"
    const val RSP_QUANTITY = "quantity"
    const val RSP_UNIT_PRICE = "unitPrice"
    const val RSP_AMOUNT = EINV_AMOUNT

    // 2018/10/01
    const val RSP_BUYER_BAN = "buyerBan"
    const val RSP_CURRENCY = "currency"
    // just a huge time never expired
    const val EXP_TIME = "2147483647"

    const val FORMAT_CODE_39 = "CODE_39"
    const val FORMAT_QR_CODE = "QR_CODE"

    const val CODE_39_DATE_START = 0
    const val CODE_39_DATE_END = CODE_39_DATE_START + 5
    const val CODE_39_SERIAL_START = CODE_39_DATE_END
    const val CODE_39_SERIAL_END = CODE_39_SERIAL_START + 10
    const val CODE_39_RANDOM_START = CODE_39_SERIAL_END
    const val CODE_39_RANDOM_END = CODE_39_RANDOM_START + 4
    const val CODE_39_TOTAL_LEN = CODE_39_RANDOM_END

    const val QR_SERIAL_START = 0
    const val QR_SERIAL_END = QR_SERIAL_START + 10
    const val QR_DATE_START = QR_SERIAL_END
    const val QR_DATE_END = QR_DATE_START + 7
    const val QR_RANDOM_START = QR_DATE_END
    const val QR_RANDOM_END = QR_RANDOM_START + 4
    const val QR_AMOUNT_START = QR_RANDOM_END
    const val QR_AMOUNT_END = QR_AMOUNT_START + 8
    const val QR_TAX_AMOUNT_START = QR_AMOUNT_END
    const val QR_TAX_AMOUNT_END = QR_TAX_AMOUNT_START + 8
    const val QR_BUYER_ID_START = QR_TAX_AMOUNT_END
    const val QR_BUYER_ID_END = QR_BUYER_ID_START + 8
    const val QR_SELLER_ID_START = QR_BUYER_ID_END
    const val QR_SELLER_ID_END = QR_SELLER_ID_START + 8
    const val QR_HASH_START = QR_SELLER_ID_END
    const val QR_HASH_END = QR_HASH_START + 24

    const val QR_RESERVE_START = QR_HASH_END
    const val QR_RESERVE_END = QR_RESERVE_START + 10

    const val QR_STUFF_START = QR_RESERVE_END
    const val QR_TOTAL_LEN = QR_HASH_END
}