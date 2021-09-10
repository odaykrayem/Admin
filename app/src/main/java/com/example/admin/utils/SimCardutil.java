package com.example.admin.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.admin.Models.OperationModel;

import java.util.List;

public class SimCardutil {
    final int REQUEST_PHONE_CALL = 1;


    /**
     * @TelecomManager Object
     * @int value represent  0 for main sim card , any number greater than 0 for second sim card
     * if value is negative it will use main sim
     *
     */
    private PhoneAccountHandle getSimHandler(TelecomManager telecomManager, int simNumber, Context context, Activity activity) {
        int mDefaultSimNum = 0;
        if(simNumber <= 0){
            simNumber = mDefaultSimNum;
        }

        //To find SIM ID
        String primarySimId = "", secondarySimId = "";
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, 2);

        }
        List<SubscriptionInfo> subList = subscriptionManager.getActiveSubscriptionInfoList();
        int index = -1;
        for (SubscriptionInfo subscriptionInfo : subList) {
            index++;
            if (index == 0) {
                primarySimId = subscriptionInfo.getIccId();
            } else {
                secondarySimId = subscriptionInfo.getIccId();
            }
        }
        // TO CREATE PhoneAccountHandle FROM SIM ID
        List<PhoneAccountHandle> list = telecomManager.getCallCapablePhoneAccounts();
        PhoneAccountHandle primaryPhoneAccountHandle = null, secondaryPhoneAccountHandle = null;
        for (PhoneAccountHandle phoneAccountHandle : list) {
            if (phoneAccountHandle.getId().contains(primarySimId)) {
                primaryPhoneAccountHandle = phoneAccountHandle;
            }
            if (phoneAccountHandle.getId().contains(secondarySimId)) {
                secondaryPhoneAccountHandle = phoneAccountHandle;
            }
        }
        if(simNumber == 0) {
            return primaryPhoneAccountHandle;
        }else {
            return secondaryPhoneAccountHandle;
        }
    }
    private PhoneAccountHandle getSimHandler1(TelecomManager telecomManager, int simNumber, Context context) {
        int mDefaultSimNum = 0;
        if(simNumber <= 0){
            simNumber = mDefaultSimNum;
        }

        //To find SIM ID
        String primarySimId = "", secondarySimId = "";
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(context, "no permission", Toast.LENGTH_SHORT).show();
        }
        List<SubscriptionInfo> subList = subscriptionManager.getActiveSubscriptionInfoList();
        int index = -1;
        for (SubscriptionInfo subscriptionInfo : subList) {
            index++;
            if (index == 0) {
                primarySimId = subscriptionInfo.getIccId();
            } else {
                secondarySimId = subscriptionInfo.getIccId();
            }
        }
        // TO CREATE PhoneAccountHandle FROM SIM ID
        List<PhoneAccountHandle> list = telecomManager.getCallCapablePhoneAccounts();
        PhoneAccountHandle primaryPhoneAccountHandle = null, secondaryPhoneAccountHandle = null;
        for (PhoneAccountHandle phoneAccountHandle : list) {
            if (phoneAccountHandle.getId().contains(primarySimId)) {
                primaryPhoneAccountHandle = phoneAccountHandle;
            }
            if (phoneAccountHandle.getId().contains(secondarySimId)) {
                secondaryPhoneAccountHandle = phoneAccountHandle;
            }
        }
        if(simNumber == 0) {
            return primaryPhoneAccountHandle;
        }else {
            return secondaryPhoneAccountHandle;
        }
    }

//    public void tt(OperationModel operationModel, Activity activity, Context context){
//
//            String number = operationModel.getMobile_number();
//            String category = operationModel.getCategory().trim();
////                if (!checkPhoneNumber(number)){
////                    Toast.makeText(getContext(), "Invalid Phone Number", Toast.LENGTH_LONG).show();
////                    return;
////                }
//            TelecomManager telecomManager = (TelecomManager) activity.getSystemService(Context.TELECOM_SERVICE);
//
////                //To call from SIM 1
////                Uri uri = Uri.fromParts("tel",number, "");
////                Bundle extras = new Bundle();
////                extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE,primaryPhoneAccountHandle);
////                telecomManager.placeCall(uri, extras);
//
//
//        String code = "";
//        if(operationModel.getSim_type().equals(OperationModel.MTN_PROVIDER)){
//             code = "*150*75169*"+ number + "*"+ category + "#";
//
//        }else{
//             code = "*150*1*74862*1*"+ category + "*" + number + "*" + number + "#";
//
//        }
////            String mtnCode = "*150*75169*"+ number + "*"+ category + "#";
////            String syriatelCode = "*150*1*74862*1*"+ category + "*" + number + "*" + number + "#";
//            Toast.makeText(activity, code, Toast.LENGTH_LONG).show();
//            Log.e("syriatelCode", code);
//
//            //To call from SIM 2
//            Uri uri1 = Uri.parse("tel:" + Uri.encode( code ));
//            final int REQUEST_PHONE_CALL = 1;
//            Bundle extras = new Bundle();
//            /**
//             * ApI > = 26
//             *
//             *        TelephonyManager telMgr = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
//             *        int simState = telMgr.getSimState(1);
//             */
//            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, getSimHandler(telecomManager,  1, context, activity) );
//
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
//            } else {
//                telecomManager.placeCall(uri1, extras);
//            }
//
//
//    }
    public void tt1(OperationModel operationModel,  Context context){

        String number = operationModel.getMobile_number().trim();
        String category = operationModel.getCategory().trim();
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

//                //To call from SIM 1
//                Uri uri = Uri.fromParts("tel",number, "");
//                Bundle extras = new Bundle();
//                extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE,primaryPhoneAccountHandle);
//                telecomManager.placeCall(uri, extras);

        Bundle extras = new Bundle();
        String code = "";
        if(operationModel.getSim_type().equals(OperationModel.MTN_PROVIDER)){
            code = "*150*75169*"+ number + "*"+ category + "#";
            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, getSimHandler1(telecomManager,  0, context) );
            Toast.makeText(context, code, Toast.LENGTH_LONG).show();

        }else if(operationModel.getSim_type().equals(OperationModel.SYRIATEL_PROVIDER)){
            code = "*150*1*74862*1*"+ category + "*" + number + "*" + number + "#";
            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, getSimHandler1(telecomManager,  1, context) );
            Toast.makeText(context, code, Toast.LENGTH_LONG).show();

        }
        Toast.makeText(context, "after", Toast.LENGTH_LONG).show();

        Log.e("syriatelCode", code);

        //To call from SIM 2
        Uri uri1 = Uri.parse("tel:" + Uri.encode( code ));


        /**
         * ApI > = 26
         *
         *        TelephonyManager telMgr = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
         *        int simState = telMgr.getSimState(1);
         */

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(context, "no permission", Toast.LENGTH_SHORT).show();
        } else {
            telecomManager.placeCall(uri1, extras);
        }


    }

}
