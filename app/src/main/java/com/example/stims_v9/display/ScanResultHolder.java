package com.example.stims_v9.display;

public class ScanResultHolder {
    private static String scanResult;

    public static String getScanResult() {
        return scanResult;
    }

    public static void setScanResult(String scanResult) {
        ScanResultHolder.scanResult = scanResult;
    }
}
