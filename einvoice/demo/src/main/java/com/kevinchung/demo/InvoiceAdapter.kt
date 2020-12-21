package com.kevinchung.demo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemState
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter
import com.kevinchung.einvoice.data.InvoiceDetail
import com.kevinchung.einvoice.data.ProductDetail

/**
 * Extends AbstractExpandableItemAdapter for expand/collapse feature
 */

class InvoiceAdapter(
        private val ctx: Context,
        private val adapterData: ArrayList<InvoiceDetail>
    ):AbstractExpandableItemAdapter<InvoiceAdapter.GroupVH, InvoiceAdapter.ChildVH>() {

    companion object {
        private const val TAG = "InvoiceAdapter"
    }

    override fun getChildCount(groupPosition: Int): Int {
        return if(groupPosition in 0 until adapterData.size) {
            Log.d(TAG, "child count: ${adapterData[groupPosition].details.size} at $groupPosition")
            adapterData[groupPosition].details.size
        } else {
            0
        }
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        val id = adapterData[groupPosition].details[childPosition].hashCode() and 0x0000FFFF
        return id.toLong()
    }

    override fun getGroupCount(): Int {
        return adapterData.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        // only use lower bits, due to limit come from advrecyclerview library
        val id = adapterData[groupPosition].hashCode() and 0x0000FFFF
        return id.toLong()
    }

    override fun onBindChildViewHolder(
        holder: ChildVH,
        groupPosition: Int,
        childPosition: Int,
        viewType: Int
    ) {
        holder.onBindView(adapterData[groupPosition].details[childPosition])
    }

    override fun onBindGroupViewHolder(holder: GroupVH, groupPosition: Int, viewType: Int) {
        holder.onBindView(adapterData[groupPosition])
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): ChildVH {
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.invoice_detail, parent, false)
        return ChildVH(view)
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): GroupVH {
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.invoice_item, parent, false)
        return GroupVH(view)
    }

    override fun onCheckCanExpandOrCollapseGroup(
        holder: GroupVH,
        groupPosition: Int,
        x: Int,
        y: Int,
        expand: Boolean
    ): Boolean {
        return adapterData[groupPosition].details.isNotEmpty()
    }

    init {
        setHasStableIds(true)
    }
    inner class GroupVH(v: View): ExpandableItemViewHolder, RecyclerView.ViewHolder(v) {
        private val mExpandState = ExpandableItemState()

        private val serial = v.findViewById<TextView>(R.id.tvSerial)
        private val time = v.findViewById<TextView>(R.id.tvTime)
        private val sellerName = v.findViewById<TextView>(R.id.tvSeller)
        private val amount = v.findViewById<TextView>(R.id.tvAmount)

        override fun getExpandState(): ExpandableItemState {
            return mExpandState
        }
        override fun getExpandStateFlags(): Int {
            return mExpandState.flags
        }

        override fun setExpandStateFlags(flags: Int) {
            mExpandState.flags = flags
        }
        fun onBindView(data: InvoiceDetail) {
            serial.text = data.invNum
            time.text = data.invDate+" "+data.invoiceTime
            sellerName.text = data.sellerName
            try {
                // paper invoice may not contain amount field, need to calculate from
                // the sum of details list
                if(data.amount.toInt() == 0) {
                    var totalAmount = 0.0
                    for(d in data.details) {
                        totalAmount += d.amount
                    }
                    amount.text = "" + totalAmount.toInt()
                } else
                    amount.text = "" + data.amount.toInt()
            } catch(e:Exception){
                e.printStackTrace()
            }
        }
    }


    inner class ChildVH(v:View): RecyclerView.ViewHolder(v) {
        private val name = v.findViewById<TextView>(R.id.tvName)
        private val qty = v.findViewById<TextView>(R.id.tvQty)
        private val amount = v.findViewById<TextView>(R.id.tvAmount)
        fun onBindView(data: ProductDetail) {
            name.text = data.description
            try {
                qty.text = "" + data.quantity.toInt()
                amount.text = "" + data.amount.toInt()
            } catch(e:Exception) {
                e.printStackTrace()
            }
        }
    }

}