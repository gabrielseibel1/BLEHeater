package br.com.embs.bleheater.deviceList

import android.bluetooth.BluetoothDevice
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import br.com.embs.bleheater.*
import br.com.embs.bleheater.utils.BLEHelper
import br.com.embs.bleheater.utils.launchActivity
import kotlinx.android.synthetic.main.activity_device_list.*

class DeviceListActivity : AppCompatActivity(),
        BTDeviceListFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        val fragment = supportFragmentManager.findFragmentById(R.id.device_list_fragment)
                as BTDeviceListFragment

        val statusListener = object : ScanStatusListener {
            override fun onScanStatusChange(scanning: Boolean) {
                when (scanning) {
                    true -> {
                        scan_status.text = resources.getString(R.string.scanning)
                        scan_button.isEnabled = false
                    }
                    false -> {
                        scan_status.text = resources.getString(R.string.not_scanning)
                        scan_button.isEnabled = true
                    }
                }
            }
        }

        val bleHelper = BLEHelper(this)
        bleHelper.scanLeDevice(true, fragment.deviceListAdapter, statusListener)

        scan_button.setOnClickListener {
            bleHelper.scanLeDevice(true, fragment.deviceListAdapter, statusListener)
        }
    }

    override fun onDeviceSelected(device: BluetoothDevice) {
        Log.d("onDeviceSelected", "onDeviceSelected ${device.name}")
        launchActivity<DeviceControlActivity> {
            putExtra("device", device)
        }
    }

    interface ScanStatusListener {
        fun onScanStatusChange(scanning: Boolean)
    }
}
