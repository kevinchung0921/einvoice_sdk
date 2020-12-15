package com.kevinchung.einvoice

import com.kevinchung.einvoice.Const.API_EINV_CARRIER
import com.kevinchung.einvoice.Const.EINV_ACTION
import com.kevinchung.einvoice.Const.EINV_ACTION_CARRIER_HEADER
import com.kevinchung.einvoice.Const.EINV_APP_ID
import com.kevinchung.einvoice.Const.EINV_CARD_CARRIER_BARCODE
import com.kevinchung.einvoice.Const.EINV_CARD_ENCRYPT
import com.kevinchung.einvoice.Const.EINV_CARD_NO
import com.kevinchung.einvoice.Const.EINV_CARD_TYPE
import com.kevinchung.einvoice.Const.EINV_CARRIER_HEADER_VERSION
import com.kevinchung.einvoice.Const.EINV_END_DATE
import com.kevinchung.einvoice.Const.EINV_EXP_TIME_STAMP
import com.kevinchung.einvoice.Const.EINV_ONLY_WIN_LIST
import com.kevinchung.einvoice.Const.EINV_START_DATE
import com.kevinchung.einvoice.Const.EINV_TIME_STAMP
import com.kevinchung.einvoice.Const.EINV_UUID_0_5
import com.kevinchung.einvoice.Const.EINV_VERSION
import com.kevinchung.einvoice.data.CarrierHeader
import com.kevinchung.einvoice.data.CarrierHeaderRsp
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface EInvApiInterface {
    @POST(API_EINV_CARRIER)
    fun getCarrierHeader (
        @Query(EINV_VERSION) ver: String = EINV_CARRIER_HEADER_VERSION,
        @Query(EINV_CARD_TYPE) cardType: String = EINV_CARD_CARRIER_BARCODE,
        @Query(EINV_CARD_NO) cardNo: String,
        @Query(EINV_EXP_TIME_STAMP) expTime: String = "2147483647",
        @Query(EINV_ACTION) action: String = EINV_ACTION_CARRIER_HEADER,
        @Query(EINV_TIME_STAMP) timeStamp: String,
        @Query(EINV_START_DATE) startDate: String,
        @Query(EINV_END_DATE) endDate: String,
        @Query(EINV_ONLY_WIN_LIST) onlyWin: String,
        @Query(EINV_UUID_0_5) uuid: String,
        @Query(EINV_APP_ID) appId: String,
        @Query(EINV_CARD_ENCRYPT) cardEncrypt: String
    ): Call<CarrierHeaderRsp>
}