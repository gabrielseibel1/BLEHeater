package br.com.embs.bleheater

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.embs.bleheater.utils.BluetoothLeService

import kotlinx.android.synthetic.main.activity_device_control.*
import kotlinx.android.synthetic.main.content_device_control.*
import android.content.IntentFilter


class DeviceControlActivity : AppCompatActivity() {

    private var connectedToGATT = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_control)
        setSupportActionBar(toolbar)

        //connect to device
        val device : BluetoothDevice = intent.extras.getParcelable("device")
        val serviceIntent = Intent(this, BluetoothLeService::class.java)
        serviceIntent.putExtra(BluetoothLeService.DEVICE_KEY, device)
        startService(serviceIntent)

        //register broadcast receiver
        val filter = IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            addAction(BluetoothLeService.ACTION_DATA_WRITE_SUCCESS)
            addAction(BluetoothLeService.ACTION_DATA_WRITE_FAILED)
        }
        registerReceiver(GATTUpdateReceiver, filter)

        //set UI
        device_name.text = device.name
        device_connected.text = getString(R.string.disconnected)

        temperature_picker.apply {
            maxValue = 30
            minValue = 20
            value = 25
            wrapSelectorWheel = false
        }

        fab.setOnClickListener { _ ->
            val writeTemperatureIntent = Intent(this, BluetoothLeService::class.java)
            writeTemperatureIntent.putExtra(
                    BluetoothLeService.TEMPERATURE_KEY,
                    temperature_picker.value.toString()
            )
            startService(writeTemperatureIntent)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(GATTUpdateReceiver)
        super.onDestroy()
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    val GATTUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            when (action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    connectedToGATT = true
                    updateConnectionState(R.string.connected)
                    invalidateOptionsMenu()
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    connectedToGATT = false
                    updateConnectionState(R.string.disconnected)
                    invalidateOptionsMenu()
                    clearUI()
                }
                BluetoothLeService.ACTION_DATA_WRITE_SUCCESS -> {
                    updateWriteState(true)
                }
                BluetoothLeService.ACTION_DATA_WRITE_FAILED -> {
                    updateWriteState(false)
                }
            }
        }
    }

    private fun updateWriteState(success: Boolean) {
        device_write.text = (device_write.text.toString().toInt() + 1).toString()
    }

    private fun clearUI() {
        finish()
    }

    private fun updateConnectionState(stringResource: Int) {
        device_connected.text = getString(stringResource)
    }
}
