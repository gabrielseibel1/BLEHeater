package br.com.embs.bleheater.deviceList

import android.bluetooth.BluetoothDevice
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import br.com.embs.bleheater.BLEHelper
import br.com.embs.bleheater.R
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

    override fun onDeviceSelected(item: BluetoothDevice) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface ScanStatusListener {
        fun onScanStatusChange(scanning: Boolean)
    }
}
