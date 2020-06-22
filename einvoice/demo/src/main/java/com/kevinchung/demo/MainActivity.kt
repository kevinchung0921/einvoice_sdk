package com.kevinchung.demo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager
import com.journeyapps.barcodescanner.CaptureActivity
import com.kevinchung.einvoice.EInvoice
import com.kevinchung.einvoice.data.Invoice
import com.kevinchung.einvoice.data.InvoiceDetail
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "demo"
        private const val API_KEY = ""  // fill API key before use
        private const val MSG_ADD_ITEM = 0
        private const val MSG_SHOW_PROGRESS = 1
        private const val MSG_HIDE_PROGRESS = 2
    }

    lateinit var invSdk: EInvoice

    private val invoiceList = ArrayList<InvoiceDetail>()
    private var adapter = InvoiceAdapter(this, invoiceList)
    private var wrappedAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    private var barcode:String? = null
    private var password:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            startScan()
        }

        setupEInvSdk()

        setupListView()

        EventBus.getDefault().register(this)
    }

    private fun setupListView() {
        rvInvoice.layoutManager = LinearLayoutManager(this)
        val rvExpandableItemManager = RecyclerViewExpandableItemManager(null)
        wrappedAdapter = rvExpandableItemManager.createWrappedAdapter(adapter)
        rvInvoice.adapter = wrappedAdapter
        rvExpandableItemManager.attachRecyclerView(rvInvoice)
    }

    private fun startScan() {
        val integrator = IntentIntegrator(this)
        integrator.captureActivity = CaptureActivity::class.java
        val supportList: Collection<String> = listOf("QR_CODE", "CODE_39")
        integrator.setDesiredBarcodeFormats(supportList)
        integrator.setPrompt("請掃描電子發票左方的QR Code")
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }

    private fun setupEInvSdk() {
        invSdk = EInvoice(API_KEY)
        if(invSdk == null)
            Log.e(TAG, "Error!!")
        invSdk.enableLog = true

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                CarrierSetupDialog(this).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            IntentIntegrator.REQUEST_CODE -> {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if(result.contents == null)
                    return

                val invoice = Invoice(result.formatName, result.contents)
                invSdk?.run {
                    Thread {
                        Looper.prepare()

                        getInvDetail(invoice!!)?.run{
                            handler.sendEmptyMessage(MSG_SHOW_PROGRESS)
                            Log.d(TAG,"adding serial $invNumber")
                            if(isInvoiceListNotExisted(invNumber)) {
                                Log.d(TAG,"serial not exist, add it")
                                invoiceList.add(this)
                                Log.d(TAG,"list size:${invoiceList.size}")
                                val msg = Message()
                                msg.what = MSG_ADD_ITEM
                                msg.arg1 = invoiceList.size
                                handler.sendMessage(msg)
                            }
                            handler.sendEmptyMessage(MSG_HIDE_PROGRESS)
                        }


                    }.start()
                }

            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // check if the invoice serial already in invoiceList
    private fun isInvoiceListNotExisted(serial: String):Boolean {
        for(detail in invoiceList) {
            if(serial == detail.invNumber)
                return false
        }
        return true
    }

    private val handler = object:Handler() {
        override fun handleMessage(msg: Message) {
            when(msg.what) {
                MSG_ADD_ITEM -> {
                    adapter.notifyItemInserted(msg.arg1)
                    wrappedAdapter?.notifyItemInserted(msg.arg1)
                }
                MSG_SHOW_PROGRESS -> progressBar.visibility = View.VISIBLE
                MSG_HIDE_PROGRESS -> progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(event:BarcodeSetEvent) {
        barcode = event.barcode
        password = event.pass
        handler.sendEmptyMessage(MSG_SHOW_PROGRESS)
        Thread{
            // set invoice check duration as recent 30 days
            val now = Date()
            val start = Date(now.year, now.month, now.date-30)
            val headers = invSdk.getCarrierHeader(start.time, now.time, barcode!!, password!!, false)
            headers?.let{
                for(header in it) {
                    val detail = invSdk.getCarrierDetail(header, barcode!!, password!!)
                    if(detail != null && isInvoiceListNotExisted(detail.invNumber)) {
                        invoiceList.add(detail)
                        val msg = Message()
                        msg.what = MSG_ADD_ITEM
                        msg.arg1 = invoiceList.size
                        handler.sendMessage(msg)
                    }
                }
                handler.sendEmptyMessage(MSG_HIDE_PROGRESS)
            }
        }.start()
    }
}
