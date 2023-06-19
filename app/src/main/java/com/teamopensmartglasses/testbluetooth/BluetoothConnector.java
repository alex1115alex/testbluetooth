package com.teamopensmartglasses.testbluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnector {

    private static final String TAG = "BluetoothConnector";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Bluetooth Serial Board UUID
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;

    public BluetoothConnector(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    @SuppressLint("MissingPermission")
    public void connectToDevice(String deviceHardwareAddress) {
        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceHardwareAddress);

        // Attempt to connect to the device
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
            return;
        }

        // Cancel discovery because it otherwise slows down the connection
        bluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception
            bluetoothSocket.connect();
            Log.d(TAG, "Connected to the device");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return
            try {
                bluetoothSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // Code to manage your connection in a separate thread goes here,
        // you can either initiate a new thread here or pass the bluetoothSocket object back to the MainActivity
    }

    // Closes the client socket and causes the thread to finish
    public void cancelConnection() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}