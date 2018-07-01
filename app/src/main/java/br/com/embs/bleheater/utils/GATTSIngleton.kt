package br.com.embs.bleheater.utils

import android.bluetooth.BluetoothGatt

class GATTSIngleton {
    companion object {
        lateinit var BluetoothGatt: BluetoothGatt
    }
}