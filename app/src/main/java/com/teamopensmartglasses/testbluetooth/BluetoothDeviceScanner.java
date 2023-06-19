package com.teamopensmartglasses.testbluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.Set;

public class BluetoothDeviceScanner {

    private static final String TAG = "BluetoothDeviceScanner";
    private BluetoothAdapter bluetoothAdapter;

    public BluetoothDeviceScanner(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void listPairedDevices() {
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.e(TAG, "Device doesn't support bluetooth");
            return;
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth not enabled");
            return;
        }

        // Get a set of currently paired devices
        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, list them
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "Paired Device: Name: " + deviceName + ", Address: " + deviceHardwareAddress);
            }
        } else {
            Log.d(TAG, "No Paired Devices Found");
        }
    }
}