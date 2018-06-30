package br.com.embs.bleheater

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import br.com.embs.bleheater.deviceList.BTDeviceListAdapter
import br.com.embs.bleheater.deviceList.DeviceListActivity

class BLEHelper(activity: Activity) {
    private val btAdapter: BluetoothAdapter
    private val handler = Handler(Looper.getMainLooper())
    var scanning = false
        private set

    companion object {
        private const val REQUEST_ENABLE_BT = 1

        // Stops scanning after 10 seconds.
        private const val SCAN_PERIOD: Long = 10000
    }

    init {
        // Initializes Bluetooth adapter.
        val btManager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager.adapter

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (btAdapter == null || !btAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    /**
     * Toggles scanning and warns listener
     */
    private fun toggleScanning(enable: Boolean, scanStatusListener: DeviceListActivity.ScanStatusListener? = null) {
        scanning = enable
        scanStatusListener?.onScanStatusChange(enable)
    }

    /**
     * Enable/disable scan for available BLE devices, adding them to a RecyclerView adapter
     */
    fun scanLeDevice(enable: Boolean, deviceListAdapter: BTDeviceListAdapter,
                     scanStatusListener: DeviceListActivity.ScanStatusListener? = null) {

        val scanCallback = BLEScannerCallback(deviceListAdapter)
        deviceListAdapter.clearDevices()

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                toggleScanning(false, scanStatusListener)
                btAdapter.bluetoothLeScanner.stopScan(scanCallback)
            }, SCAN_PERIOD)

            toggleScanning(true, scanStatusListener)
            btAdapter.bluetoothLeScanner.startScan(scanCallback) //TODO use UUID array

        } else {
            toggleScanning(false, scanStatusListener)
            btAdapter.bluetoothLeScanner.stopScan(scanCallback)
        }
    }

    /**
     * Callback for BLE device scan that add devices to a RecyclerView adapter
     */
    private class BLEScannerCallback(val deviceListAdapter: BTDeviceListAdapter) : ScanCallback() {


        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d("BLE_SCAN", "Scan result : $result")
            deviceListAdapter.addDevice(result?.device)
            deviceListAdapter.notifyDataSetChanged()
        }

        override fun onScanFailed(errorCode: Int) {
            Log.d("BLE_SCAN", "Scan failed with error $errorCode")
        }
    }
}