package br.com.embs.bleheater.utils

import android.app.IntentService

import android.bluetooth.*
import android.content.Intent
import android.util.Log
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import java.nio.ByteBuffer
import java.util.*

class BluetoothLeService : IntentService("BluetoothLeService") {

    private var mConnectionState = STATE_DISCONNECTED

    override fun onHandleIntent(intent: Intent) {
        if (intent.hasExtra(DEVICE_KEY)) {
            val device = intent.extras.getParcelable<BluetoothDevice>("device")
            GATTSIngleton.BluetoothGatt = device.connectGatt(this, true, gattCallback)
        }
        if (intent.hasExtra(TEMPERATURE_KEY)) {
            val temperature = intent.extras.getString(TEMPERATURE_KEY)
            writeTemperature(temperature)
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private fun writeTemperature(temperature: String) {

        val service = GATTSIngleton.BluetoothGatt.getService(UUID.fromString(AT_09_SERVICE_UUID))
        val characteristic = service.getCharacteristic(UUID.fromString(AT_09_CHARACTERISTIC_UUID))

        if (characteristic == null) {
            Log.e(TAG, "Char not found!")
            broadcastUpdate(ACTION_DATA_WRITE_FAILED)
        }

        characteristic.setValue(temperature)
        GATTSIngleton.BluetoothGatt.writeCharacteristic(characteristic)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val intentAction: String
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED
                mConnectionState = STATE_CONNECTED
                broadcastUpdate(intentAction)
                Log.i(TAG, "Connected to GATT server.")
                Log.i(TAG, "Attempting to start service discovery:" +
                        GATTSIngleton.BluetoothGatt.discoverServices())

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED
                mConnectionState = STATE_DISCONNECTED
                Log.i(TAG, "Disconnected from GATT server.")
                broadcastUpdate(intentAction)
            }
        }

        // New services discovered
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_WRITE_SUCCESS)
                Log.i(TAG, "Characteristic write success.")
            } else {
                broadcastUpdate(ACTION_DATA_WRITE_FAILED)
                Log.i(TAG, "Characteristic write failed.")
            }
        }
    }

    companion object {
        const val TEMPERATURE_KEY = "temperature"
        const val DEVICE_KEY = "device"

        private val TAG = BluetoothLeService::class.java.simpleName
        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTING = 1

        private const val STATE_CONNECTED = 2
        const val ACTION_DATA_WRITE_SUCCESS = "com.example.bluetooth.le.ACTION_GATT_WRITE_SUCCESS"
        const val ACTION_DATA_WRITE_FAILED = "com.example.bluetooth.le.ACTION_GATT_WRITE_FAILED"
        const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"

        const val ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
        const val AT_09_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb"
        const val AT_09_CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb"
    }
}
