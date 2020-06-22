package com.kevinchung.demo

/**
 * Used for EventBus which pass barcode and password data from dialog to activity
 */
data class BarcodeSetEvent(val barcode:String, val pass:String)