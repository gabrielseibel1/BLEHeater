package br.com.embs.bleheater.deviceList

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.embs.bleheater.R


import br.com.embs.bleheater.deviceList.BTDeviceListFragment.OnListFragmentInteractionListener
import br.com.embs.bleheater.deviceList.dummy.DummyContent.DummyItem

import kotlinx.android.synthetic.main.fragment_btdevice.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class BTDeviceListAdapter(
        private val devices: MutableList<BluetoothDevice>,
        private val listener: OnListFragmentInteractionListener)
    : RecyclerView.Adapter<BTDeviceListAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as BluetoothDevice
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            listener.onDeviceSelected(item)
        }
    }

    fun clearDevices() {
        devices.clear()
    }

    fun addDevice(bluetoothDevice: BluetoothDevice?) {
        bluetoothDevice?.run {
            if (!devices.contains(bluetoothDevice)) devices.add(bluetoothDevice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_btdevice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = devices[position]

        if (item.name != null && item.name.isNotBlank())
            holder.mIdView.text = item.name
        else holder.mIdView.text = "?"

        holder.mContentView.text = item.uuids?.toString()

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = devices.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.device_name
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
