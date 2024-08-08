package com.reactnativesimcardsmanager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.euicc.EuiccManager;
import com.facebook.react.bridge.*;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.euicc.DownloadableSubscription;
import android.content.IntentFilter;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import java.util.List;

@ReactModule(name = SimCardsManagerModule.NAME)
public class SimCardsManagerModule extends ReactContextBaseJavaModule {
  public static final String NAME = "SimCardsManager";
  private String ACTION_DOWNLOAD_SUBSCRIPTION = "download_subscription";
  private ReactContext mReactContext;
  private EsimModule mEsimModule;

  public SimCardsManagerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    mReactContext = reactContext;
    mEsimModule = new EsimModule(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
  @ReactMethod
  public void getSimCardsNative(Promise promise) {
    WritableArray simCardsList = new WritableNativeArray();

    TelephonyManager telManager = (TelephonyManager) mReactContext.getSystemService(Context.TELEPHONY_SERVICE);
    try {
      SubscriptionManager manager = (SubscriptionManager) mReactContext
          .getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
        int activeSubscriptionInfoCount = manager.getActiveSubscriptionInfoCount();
        int activeSubscriptionInfoCountMax = manager.getActiveSubscriptionInfoCountMax();

        List<SubscriptionInfo> subscriptionInfos = manager.getActiveSubscriptionInfoList();

        for (SubscriptionInfo subInfo : subscriptionInfos) {
          WritableMap simCard = Arguments.createMap();

          String number = "";
          if(android.os.Build.VERSION.SDK_INT >= 33) {
            number = manager.getPhoneNumber(subInfo.getSubscriptionId());
          } else {
            number = subInfo.getNumber();
          }

          CharSequence carrierName = subInfo.getCarrierName();
          String countryIso = subInfo.getCountryIso();
          int dataRoaming = subInfo.getDataRoaming(); // 1 is enabled ; 0 is disabled
          CharSequence displayName = subInfo.getDisplayName();
          String iccId = subInfo.getIccId();
          int mcc = subInfo.getMcc();
          int mnc = subInfo.getMnc();
          int simSlotIndex = subInfo.getSimSlotIndex();
          int subscriptionId = subInfo.getSubscriptionId();
          int networkRoaming = telManager.isNetworkRoaming() ? 1 : 0;

          simCard.putString("carrierName", carrierName.toString());
          simCard.putString("displayName", displayName.toString());
          simCard.putString("isoCountryCode", countryIso);
          simCard.putInt("mobileCountryCode", mcc);
          simCard.putInt("mobileNetworkCode", mnc);
          simCard.putInt("isNetworkRoaming", networkRoaming);
          simCard.putInt("isDataRoaming", dataRoaming);
          simCard.putInt("simSlotIndex", simSlotIndex);
          simCard.putString("phoneNumber", number);
          simCard.putString("simSerialNumber", iccId);
          simCard.putInt("subscriptionId", subscriptionId);

          simCardsList.pushMap(simCard);
        }
      } else {
        promise.reject("0", "This functionality is not supported before Android 5.1 (22)");
      }
    } catch (Exception e) {
      promise.reject("1", "Something goes wrong to fetch simcards: " + e.getLocalizedMessage());
    }
    promise.resolve(simCardsList);
  }

  @RequiresApi(api = Build.VERSION_CODES.P)
  @ReactMethod
  public void sendPhoneCall(String phoneNumberString, int simSlotIndex) {
    Uri uri = Uri.parse("tel:" + phoneNumberString.trim());
    TelecomManager telecomManager =(TelecomManager) mReactContext.getSystemService(Context.TELECOM_SERVICE);
    List<PhoneAccountHandle> list = telecomManager.getCallCapablePhoneAccounts();

    PhoneAccountHandle accountHandle = null;
    if (list != null) {
      accountHandle = list.get(Math.min(simSlotIndex, list.size()));
    }

    if (accountHandle != null) {
      Bundle extras = new Bundle();
      extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE,accountHandle);
      telecomManager.placeCall(uri, extras);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.P)
  @ReactMethod
  public void isEsimSupported(Promise promise) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && mEsimModule.getMgr() != null) {
      promise.resolve(mEsimModule.getMgr().isEnabled());
    } else {
      promise.resolve(false);
    }
    return;
  }

  @RequiresApi(api = Build.VERSION_CODES.P)
  private void handleResolvableError(Promise promise, Intent intent) {
    try {
      // Resolvable error, attempt to resolve it by a user action
      // FIXME: review logic of resolve functions
      int resolutionRequestCode = 3;
      PendingIntent callbackIntent = PendingIntent.getBroadcast(mReactContext, resolutionRequestCode,
          intent, PendingIntent.FLAG_UPDATE_CURRENT |
              PendingIntent.FLAG_MUTABLE);

      mEsimModule.getMgr().startResolutionActivity(mReactContext.getCurrentActivity(), resolutionRequestCode, intent, callbackIntent);
    } catch (Exception e) {
      promise.reject("3", "EMBEDDED_SUBSCRIPTION_RESULT_RESOLVABLE_ERROR - Can't setup eSim due to Activity error "
          + e.getLocalizedMessage());
    }
  }

  private boolean checkCarrierPrivileges() {
    TelephonyManager telManager = (TelephonyManager) mReactContext.getSystemService(Context.TELEPHONY_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
      return telManager.hasCarrierPrivileges();
    } else {
      return false;
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.P)
  @ReactMethod
  public void setupEsim(ReadableMap config, Promise promise) {

    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P) {
      promise.reject("0", "EuiccManager is not available or before Android 9 (API 28)");
      return;
    }

    if (mEsimModule.getMgr() != null && !mEsimModule.getMgr().isEnabled()) {
      promise.reject("1", "The device doesn't support a cellular plan (EuiccManager is not available)");
      return;
    }

//    if (!checkCarrierPrivileges()) {
//      promise.reject("1", "No carrier privileges detected");
//      return;
//    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

      @Override
      public void onReceive(Context context, Intent intent) {
        if (!ACTION_DOWNLOAD_SUBSCRIPTION.equals(intent.getAction())) {
          promise.reject("3",
              "Can't setup eSim due to wrong Intent:" + intent.getAction() + " instead of "
                  + ACTION_DOWNLOAD_SUBSCRIPTION);
          return;
        }
        int resultCode = getResultCode();
        if (resultCode == EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_RESOLVABLE_ERROR && mEsimModule.getMgr() != null) {
          handleResolvableError(promise, intent);
        } else if (resultCode == EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_OK) {
          promise.resolve(true);
        } else if (resultCode == EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_ERROR) {
          // Embedded Subscription Error
          promise.reject("2",
              "EMBEDDED_SUBSCRIPTION_RESULT_ERROR - Can't add an Esim subscription");
        } else {
          // Unknown Error
          promise.reject("3",
              "Can't add an Esim subscription due to unknown error, resultCode is:" + String.valueOf(resultCode));
        }
      }
    };

   // Changes for registering reciever for Android 14
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      mReactContext.registerReceiver(receiver, new IntentFilter(ACTION_DOWNLOAD_SUBSCRIPTION), Context.RECEIVER_NOT_EXPORTED);
    }else {
      mReactContext.registerReceiver(
              receiver,
              new IntentFilter(ACTION_DOWNLOAD_SUBSCRIPTION),
              null,
              null);
    }

    DownloadableSubscription sub = DownloadableSubscription.forActivationCode(
        /* Passed from react side */
        config.getString("confirmationCode"));

    Intent intent = new Intent(ACTION_DOWNLOAD_SUBSCRIPTION).setPackage(mReactContext.getPackageName());
    PendingIntent callbackIntent = PendingIntent.getBroadcast(
        mReactContext,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT |
            PendingIntent.FLAG_MUTABLE);

    mEsimModule.getMgr().downloadSubscription(sub, true, callbackIntent);
  }
}
