package com.kevinchung.demo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.carrier_setup_dialog.*
import org.greenrobot.eventbus.EventBus

/**
 * Dialog for user setup carrier barcode and password
 */
class CarrierSetupDialog(private val ctx: Context): Dialog(ctx) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carrier_setup_dialog)
        btnCancel.setOnClickListener { dismiss() }
        btnOk.setOnClickListener {
            val barcode = etBarcode.text.toString().trim()
            val pass = etPass.text.toString().trim()
            if(barcode != "" && pass != "") {
                EventBus.getDefault().post(BarcodeSetEvent(barcode,pass))
                dismiss()
            } else
                Toast.makeText(ctx, "載具條碼或密碼未設定",Toast.LENGTH_SHORT).show()
        }
    }
}