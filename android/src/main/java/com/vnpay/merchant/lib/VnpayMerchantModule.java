package com.vnpay.merchant.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.vnpay.authentication.VNP_AuthenticationActivity;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.vnpay.authentication.VNP_SdkCompletedCallback;

public class VnpayMerchantModule extends ReactContextBaseJavaModule {

    public VnpayMerchantModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "VnpayMerchant";
    }

    @ReactMethod
    public void show(String scheme, boolean isSandbox, String paymentUrl, String tmn_code, String backAlert, String title, String titleColor, String beginColor, String endColor, String iconBackName) {
        Intent intent = new Intent(getReactApplicationContext(), VNP_AuthenticationActivity.class);
        intent.putExtra("url", paymentUrl);
        intent.putExtra("scheme", scheme);
        intent.putExtra("tmn_code", tmn_code);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        VNP_AuthenticationActivity.setSdkCompletedCallback(new VNP_SdkCompletedCallback() {
            @Override
            public void sdkAction(String s) {
                //action == AppBackAction
                //Người dùng nhấn back từ sdk để quay lại

                //action == CallMobileBankingApp
                //Người dùng nhấn chọn thanh toán qua app thanh toán (Mobile Banking, Ví...)
                //lúc này app tích hợp sẽ cần lưu lại cái PNR, khi nào người dùng mở lại app tích hợp thì sẽ gọi kiểm tra trạng thái thanh toán của PNR Đó xem đã thanh toán hay chưa.

                //action == WebBackAction
                //Người dùng nhấn back từ trang thanh toán thành công khi thanh toán qua thẻ khi gọi đến http://sdk.merchantbackapp

                //action == FaildBackAction
                //giao dịch thanh toán bị failed

                //action == SuccessBackAction
                //thanh toán thành công trên webview

                WritableMap params = Arguments.createMap();

                if ("AppBackAction".equals(s)) {
                    params.putInt("resultCode", -1);
                } else if ("CallMobileBankingApp".equals(s)) {
                    params.putInt("resultCode", 10);
                } else if ("WebBackAction".equals(s)) {
                    params.putInt("resultCode", 99);
                } else if ("FaildBackAction".equals(s)) {
                    params.putInt("resultCode", 98);
                } else if ("SuccessBackAction".equals(s)) {
                    params.putInt("resultCode", 97);
                }

                getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("PaymentBack", params);
            }
        });
        getReactApplicationContext().startActivity(intent);
    }
}
