package com.teamopensmartglasses.testbluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;
import java.util.UUID;

public class BLEManager {
    private static final String TAG = "BLEManager";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice bleDevice;

    // UUID of the BLE service and characteristic you want to connect to
    private static final UUID SERVICE_UUID = UUID.fromString("eda84e34-b894-4abf-8949-5db14239357a");
    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("906d7a37-ecba-48a7-a0ea-9f87d83cb91c");

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothAdapter.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" + bluetoothGatt.discoverServices());
            } else if (newState == BluetoothAdapter.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "SERVICES DISCOVERED!");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "SERVICES DISCOVERED!: SUCC :D");

                List<BluetoothGattService> services = gatt.getServices();
                String serviceUUID;
                for (int i = 0; i < services.size(); i++) {
                    serviceUUID = String.valueOf(services.get(i).getUuid());
                    List<BluetoothGattCharacteristic> characteristics = services.get(i).getCharacteristics();

                    //   Log.d(TAG, "SERV #" + i + ": UUID: " + serviceUUID + ", numChars: " + String.valueOf(characteristics.size()));

                    for (int j = 0; j < characteristics.size(); j++) {
                        //All chars for all services


                        //BluetoothGattService service = gatt.getService(SERVICE_UUID);
                        BluetoothGattService service = services.get(i);
                        if (service != null) {
                            //Log.d(TAG, "SERVICES DISCOVERED!: SERVICE NOT NULL");
                            //BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
                            BluetoothGattCharacteristic characteristic = characteristics.get(j);
                            if (characteristic != null) {
                                //Log.d(TAG, "SERVICES DISCOVERED!: DOING CHAR SHIT");
                                try {
                                    if(!gatt.setCharacteristicNotification(characteristic, true))
                                    {
                                        Log.e(TAG, "Failed to set notification for: " + characteristic.toString());
                                    }

                                    List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
                                    for (int k = 0; k < descriptors.size(); k++) {
                                        BluetoothGattDescriptor descriptor = descriptors.get(k);
                                        if (descriptor != null) {
                                            Log.d(TAG, "WRITE DESC");
                                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                            gatt.writeDescriptor(descriptor);
                                        }
                                    }

                                    gatt.readCharacteristic(characteristic);
                                    //gatt.setCharacteristicNotification(characteristic, true);


                                } catch (Exception e) {
                                    //   Log.d(TAG, "EXCEPTION: " + e.toString());
                                }


                                //BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                                //       characteristics.get(j).getUuid());
                                //descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                //gatt.writeDescriptor(descriptor);
                            }
                        }


                    }
                }

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onRead, STATUS: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Read characteristic " + characteristic.getUuid() + " Value: " + new String(characteristic.getValue()));
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onChange");
            if (CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();
                // Process your data here
                Log.d(TAG, "Data received: " + new String(data));
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onWrite");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Wrote to characteristic " + characteristic.getUuid() + " New Value: " + new String(characteristic.getValue()));
            }
        }
    };

    private ScanCallback leScanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceName = device.getName();
            Log.d(TAG, "GOT NAME: " + deviceName);

            if (deviceName == null) return;
            if (deviceName.equals("JX-03")) {
                bleDevice = device;
                bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
                bluetoothGatt = bleDevice.connectGatt(null, false, gattCallback);
            }
        }
    };

    public BLEManager(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new RuntimeException("BLE not supported");
        }

        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            throw new RuntimeException("Bluetooth not enabled");
        }
    }

    @SuppressLint("MissingPermission")
    public void startScanning() {
        bluetoothAdapter.getBluetoothLeScanner().startScan(leScanCallback);
    }

    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            List<BluetoothGattService> services = gatt.getServices();
            for (int i = 0; i < services.size(); i++) {
                List<BluetoothGattCharacteristic> characteristics = services.get(i).getCharacteristics();
                for (int j = 0; j < characteristics.size(); j++) {
                    //All chars for all services
                    BluetoothGattService service = services.get(i);
                    if (service != null) {
                        BluetoothGattCharacteristic characteristic = characteristics.get(j);
                        if (characteristic != null) {
                            try {
                                gatt.setCharacteristicNotification(characteristic, true);
                            } catch (Exception e) {
                                Log.d(TAG, "EXCEPTION: " + e);
                            }
                        }
                    }
                }
            }
        } else {
            Log.w(TAG, "onServicesDiscovered received: " + status);
        }
    }
}
