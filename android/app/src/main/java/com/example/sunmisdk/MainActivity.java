package com.example.sunmisdk;

import java.io.Console;

import android.os.Bundle;
import android.os.RemoteException;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import com.example.sunmisdk.MyApplication;
import com.example.sunmisdk.ByteUtil;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.AidlErrorCodeV2;
import com.sunmi.pay.hardware.aidl.AidlConstants.CardType;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "com.example.sunmisdk/smart_card";

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler(
            (call, result) -> {
                if ("smartCardExchange".equals(call.method)) {
                    String command = call.argument("command");
                    String lc = call.argument("lc");
                    String indata = call.argument("indata");
                    String le = call.argument("le");
                    String response = sendApduToCard(command != null ? command : "", lc != null ? lc : "", indata != null ? indata : "", le != null ? le : "");
                    result.success(response);
                } else {
                    result.notImplemented();
                }
            }
        );
    }

    private String sendApduToCard(String command, String lc, String indata, String le) {
        System.err.println("HIIIII");
        // Check if PaySDK is connected and readCardOptV2 is initialized
        if (!MyApplication.app.isPaySDKConnected() || MyApplication.app.readCardOptV2 == null) {
            return "Error: PaySDK is not connected";
        }

        byte[] sendBytes = ByteUtil.concatByteArrays(
            ByteUtil.hexStr2Bytes(command),
            ByteUtil.hexStr2Bytes(lc),
            ByteUtil.hexStr2Bytes(indata),
            ByteUtil.hexStr2Bytes(le)
        );
        byte[] recvBytes = new byte[260];

        try {
            int code = MyApplication.app.readCardOptV2.smartCardExchange(0x04, sendBytes, recvBytes);

            if (code < 0) {
                return "Error: smartCardExchange failed with code " + code + ": " + AidlErrorCodeV2.valueOf(code).msg;
            } else {
                // Convert the entire recvBytes array to a hex string and return it
                return ByteUtil.bytesArray2HexStr(recvBytes);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return "Error: RemoteException: " + e.getMessage();
        }
    }
}
