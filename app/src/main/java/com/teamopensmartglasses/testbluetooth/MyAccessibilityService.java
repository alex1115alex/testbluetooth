package com.teamopensmartglasses.testbluetooth;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

public class MyAccessibilityService extends AccessibilityService {
    private String TAG = "retardation";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "EVT TYPE: " + event.getEventType() + ", + " + event.toString());

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        Log.d(TAG, "GOT A KEY EVENT");
        if (action == KeyEvent.ACTION_DOWN) {
            Log.d("MyAccessibilityService", "Key " + KeyEvent.keyCodeToString(keyCode) + " pressed");
        }

        return super.onKeyEvent(event);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "something");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        setServiceInfo(info);
    }
}