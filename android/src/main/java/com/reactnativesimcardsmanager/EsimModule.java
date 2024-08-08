package com.reactnativesimcardsmanager;

import static android.content.Context.EUICC_SERVICE;

import android.os.Build;
import android.telephony.euicc.EuiccManager;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReactContext;

public class EsimModule {
  @RequiresApi(api = Build.VERSION_CODES.P)
  private EuiccManager mgr;
  private final ReactContext mReactContext;

  EsimModule(ReactContext reactContext) {
    mReactContext = reactContext;
  }

  @RequiresApi(api = Build.VERSION_CODES.P)
  public EuiccManager getMgr() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P || mgr == null) {
      return null;
    }

    mgr = (EuiccManager) mReactContext.getSystemService(EUICC_SERVICE);
    return mgr;
  }
}
