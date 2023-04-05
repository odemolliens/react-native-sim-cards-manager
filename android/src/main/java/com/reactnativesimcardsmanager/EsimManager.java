package com.reactnativesimcardsmanager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.euicc.DownloadableSubscription;
import android.telephony.euicc.EuiccManager;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;

public class EsimManager {
  private EuiccManager mgr;
  private final ReactContext mContext;
  private final String ACTION_DOWNLOAD_SUBSCRIPTION = "download_subscription";

  EsimManager(ReactContext context) {
    mContext = context;
  }

  @RequiresApi(api = Build.VERSION_CODES.P)
  private void initESimMgr() {
    if (mgr == null) {
      mgr = (EuiccManager) mContext.getSystemService(Context.EUICC_SERVICE);
    }
  }


  @RequiresApi(api = Build.VERSION_CODES.P)
  public boolean isEnabled() {
    initESimMgr();
    if (mgr != null) {
      return mgr.isEnabled();
    }
    return false;
  }

  @RequiresApi(api = Build.VERSION_CODES.P)
  private void handleResolvableError(Promise promise, Intent intent) {
    try {
      // Resolvable error, attempt to resolve it by a user action
      // FIXME: review logic of resolve functions
      int resolutionRequestCode = 3;
      PendingIntent callbackIntent = PendingIntent.getBroadcast(mContext, resolutionRequestCode,
        new Intent(ACTION_DOWNLOAD_SUBSCRIPTION), PendingIntent.FLAG_UPDATE_CURRENT |
          PendingIntent.FLAG_MUTABLE);

      mgr.startResolutionActivity(mContext.getCurrentActivity(), resolutionRequestCode, intent, callbackIntent);
    } catch (Exception e) {
      promise.reject("3", "EMBEDDED_SUBSCRIPTION_RESULT_RESOLVABLE_ERROR - Can't setup eSim du to Activity error "
        + e.getLocalizedMessage());
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.P)
  public void setupEsim(ReadableMap config, Promise promise) {
    if (!isEnabled()) {
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
            "Can't setup eSim du to wrong Intent:" + intent.getAction() + " instead of "
              + ACTION_DOWNLOAD_SUBSCRIPTION);
          return;
        }
        int resultCode = getResultCode();
        if (resultCode == EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_RESOLVABLE_ERROR && mgr != null) {
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
            "Can't add an Esim subscription due to unknown error, resultCode is:" + resultCode);
        }
      }
    };

    mContext.registerReceiver(
      receiver,
      new IntentFilter(ACTION_DOWNLOAD_SUBSCRIPTION),
      null,
      null);

    DownloadableSubscription sub = DownloadableSubscription.forActivationCode(
      /* Passed from react side */
      config.getString("confirmationCode"));

    PendingIntent callbackIntent = PendingIntent.getBroadcast(
      mContext,
      0,
      new Intent(ACTION_DOWNLOAD_SUBSCRIPTION),
      PendingIntent.FLAG_UPDATE_CURRENT |
        PendingIntent.FLAG_MUTABLE);

    mgr.downloadSubscription(sub, true, callbackIntent);
  }
}
