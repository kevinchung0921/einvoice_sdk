package com.kevinchung.einvoice

import com.kevinchung.einvoice.Const.API_EINV_CARRIER
import com.kevinchung.einvoice.Const.API_EINV_PAPER
import com.kevinchung.einvoice.Const.EINV_ACTION
import com.kevinchung.einvoice.Const.EINV_ACTION_CARRIER_DETAIL
import com.kevinchung.einvoice.Const.EINV_ACTION_CARRIER_HEADER
import com.kevinchung.einvoice.Const.EINV_APP_ID
import com.kevinchung.einvoice.Const.EINV_CARD_CARRIER_BARCODE
import com.kevinchung.einvoice.Const.EINV_CARD_ENCRYPT
import com.kevinchung.einvoice.Const.EINV_CARD_NO
import com.kevinchung.einvoice.Const.EINV_CARD_TYPE
import com.kevinchung.einvoice.Const.EINV_CARRIER_DETAIL_VERSION
import com.kevinchung.einvoice.Const.EINV_CARRIER_HEADER_VERSION
import com.kevinchung.einvoice.Const.EINV_DATE
import com.kevinchung.einvoice.Const.EINV_DETAIL_VERSION
import com.kevinchung.einvoice.Const.EINV_ENCRYPT
import com.kevinchung.einvoice.Const.EINV_END_DATE
import com.kevinchung.einvoice.Const.EINV_EXP_TIME_STAMP
import com.kevinchung.einvoice.Const.EINV_GENERATION
import com.kevinchung.einvoice.Const.EINV_INV_NUM
import com.kevinchung.einvoice.Const.EINV_INV_TERM
import com.kevinchung.einvoice.Const.EINV_ONLY_WIN_LIST
import com.kevinchung.einvoice.Const.EINV_RANDOM
import com.kevinchung.einvoice.Const.EINV_SELLER_ID
import com.kevinchung.einvoice.Const.EINV_START_DATE
import com.kevinchung.einvoice.Const.EINV_TIME_STAMP
import com.kevinchung.einvoice.Const.EINV_TYPE
import com.kevinchung.einvoice.Const.EINV_UUID_0_4
import com.kevinchung.einvoice.Const.EINV_UUID_0_5
import com.kevinchung.einvoice.Const.EINV_VERSION
import com.kevinchung.einvoice.data.CarrierHeader
import com.kevinchung.einvoice.data.CarrierHeaderRsp
import com.kevinchung.einvoice.data.InvoiceDetail
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

    @POST(API_EINV_CARRIER)
    fun getCarrierDetail(
        @Query(EINV_VERSION) ver: String = EINV_CARRIER_DETAIL_VERSION,
        @Query(EINV_CARD_TYPE) cardType: String = EINV_CARD_CARRIER_BARCODE,
        @Query(EINV_CARD_NO) cardNo: String,
        @Query(EINV_EXP_TIME_STAMP) expTime: String = "2147483647",
        @Query(EINV_ACTION) action: String = EINV_ACTION_CARRIER_DETAIL,
        @Query(EINV_TIME_STAMP) timeStamp: String,
        @Query(EINV_INV_NUM) invNum: String,
        @Query(EINV_DATE) invDate: String,
        @Query(EINV_UUID_0_5) uuid: String,
        @Query(EINV_APP_ID) appId: String,
        @Query(EINV_CARD_ENCRYPT) cardEncrypt: String
    ):Call<InvoiceDetail>

    @POST(API_EINV_PAPER)
    fun getInvDetail(
        @Query(EINV_VERSION) ver: String = EINV_DETAIL_VERSION,
        @Query(EINV_TYPE) type: String,
        @Query(EINV_INV_NUM) invNum: String,
        @Query(EINV_ACTION) action: String,
        @Query(EINV_GENERATION) generation: String,
        @Query(EINV_INV_TERM) invTerm: String,
        @Query(EINV_DATE) invDate: String,
        @Query(EINV_ENCRYPT) encrypt: String,
        @Query(EINV_SELLER_ID) sellerId: String,
        @Query(EINV_UUID_0_4) uuid: String,
        @Query(EINV_RANDOM) random: String,
        @Query(EINV_APP_ID) appId: String
    ):Call<InvoiceDetail>
}