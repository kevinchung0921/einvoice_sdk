package com.kevinchung.einvoice.data

data class CarrierHeaderRsp (
    val v: String,
    val msg: String,
    val onlyWinningInv: String,
    val details: List<CarrierHeader>
)