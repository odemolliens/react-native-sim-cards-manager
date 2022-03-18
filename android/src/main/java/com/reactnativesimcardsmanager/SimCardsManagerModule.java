package com.reactnativesimcardsmanager;
import static android.content.Context.EUICC_SERVICE;

import android.os.Build;
import android.telephony.euicc.EuiccManager;
import com.facebook.react.bridge.*;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.euicc.DownloadableSubscription;
import android.content.IntentFilter;
import android.content.Context;
import android.content.BroadcastReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = SimCardsManagerModule.NAME)
public class SimCardsManagerModule extends ReactContextBaseJavaModule {
    public static final String NAME = "SimCardsManager";

    public SimCardsManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @ReactMethod
    public void getSimCards(Promise promise) {
        final Map<String, Object> constants = new HashMap<>();

        TelephonyManager telManager = (TelephonyManager) this.reactContext.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            SubscriptionManager manager = (SubscriptionManager) this.reactContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                int activeSubscriptionInfoCount = manager.getActiveSubscriptionInfoCount();
                int activeSubscriptionInfoCountMax = manager.getActiveSubscriptionInfoCountMax();

                List<SubscriptionInfo> subscriptionInfos = manager.getActiveSubscriptionInfoList();

                int sub = 0;
                for (SubscriptionInfo subInfo : subscriptionInfos) {
                    CharSequence carrierName = subInfo.getCarrierName();
                    String countryIso = subInfo.getCountryIso();
                    int dataRoaming = subInfo.getDataRoaming();  // 1 is enabled ; 0 is disabled
                    CharSequence displayName = subInfo.getDisplayName();
                    String iccId = subInfo.getIccId();
                    int mcc = subInfo.getMcc();
                    int mnc = subInfo.getMnc();
                    String number = subInfo.getNumber();
                    int simSlotIndex = subInfo.getSimSlotIndex();
                    int subscriptionId = subInfo.getSubscriptionId();
                    boolean networkRoaming = telManager.isNetworkRoaming();
                    String deviceId = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        deviceId = telManager.getDeviceId(simSlotIndex);
                    }

                    constants.put("carrierName" + sub, carrierName.toString());
                    constants.put("displayName" + sub, displayName.toString());
                    constants.put("countryCode" + sub, countryIso);
                    constants.put("mcc" + sub, mcc);
                    constants.put("mnc" + sub, mnc);
                    constants.put("isNetworkRoaming" + sub, networkRoaming);
                    constants.put("isDataRoaming" + sub, (dataRoaming == 1));
                    constants.put("simSlotIndex" + sub, simSlotIndex);
                    constants.put("phoneNumber" + sub, number);
                    constants.put("deviceId" + sub, deviceId);
                    constants.put("simSerialNumber" + sub, iccId);
                    constants.put("subscriptionId" + sub, subscriptionId);
                    sub++;
                }
            }
            else{
                constants.put("phoneNumber0", telManager.getLine1Number());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        promise.resolve(constants);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @ReactMethod
    public void isEsimSupported(Promise promise) {
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && mgr != null ) {
        promise.resolve(mgr.isEnabled());
      } else {
        promise.resolve(false);
      }
      return;
    }
  
    @RequiresApi(api = Build.VERSION_CODES.P)
    @ReactMethod
    public void setupEsim(ReadableMap config, Promise promise) {
      if (mgr == null){
        // Could not get react context";
        promise.resolve(0);
        return;
      }
  
      BroadcastReceiver receiver = new BroadcastReceiver() {
  
        @Override
        public void onReceive(Context context, Intent intent) {
          if (!ACTION_DOWNLOAD_SUBSCRIPTION.equals(intent.getAction())) {
            promise.resolve(0);
            return;
          }
          int resultCode = getResultCode();
          if(resultCode == EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_RESOLVABLE_ERROR && mgr != null) {
            // Resolvable error, attempt to resolve it by a user action
            promise.resolve(3);
            PendingIntent callbackIntent = PendingIntent.getBroadcast(mReactContext, 3, Intent(ACTION_DOWNLOAD_SUBSCRIPTION), PendingIntent.FLAG_ONE_SHOT);
            mgr.startResolutionActivity(mReactContext.currentActivity, 3, intent, callbackIntent);
          } else if (resultCode == EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_OK){
            promise.resolve(2);
          } else if (resultCode == EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_ERROR){
            // Embedded Subscription Error
            promise.resolve(1);
          } else {
            // Unknown Error
            promise.resolve(0);
          }
        }
      };
  
      mReactContext.registerReceiver(
        receiver,
        new IntentFilter(ACTION_DOWNLOAD_SUBSCRIPTION),
        null,
        null);
  
      DownloadableSubscription sub = DownloadableSubscription.forActivationCode(
        /* Passed from react side */
        config.getString("confirmationCode")
      );
  
      PendingIntent callbackIntent = PendingIntent.getBroadcast(
        mReactContext,
        0,
        new Intent(ACTION_DOWNLOAD_SUBSCRIPTION),
        PendingIntent.FLAG_UPDATE_CURRENT);
  
      mgr.downloadSubscription(sub, true, callbackIntent);
    }
}
