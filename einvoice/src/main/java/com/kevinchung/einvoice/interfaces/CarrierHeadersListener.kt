package com.kevinchung.einvoice.interfaces

import com.kevinchung.einvoice.data.CarrierHeader

interface CarrierHeadersListener {
    fun carrierHeaders(headers:List<CarrierHeader>?)
}