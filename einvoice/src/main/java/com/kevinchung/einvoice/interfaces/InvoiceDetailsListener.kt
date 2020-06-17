package com.kevinchung.einvoice.interfaces

import com.kevinchung.einvoice.data.InvoiceDetail

interface InvoiceDetailsListener {
    fun invoiceDetails(detail: InvoiceDetail?)
}