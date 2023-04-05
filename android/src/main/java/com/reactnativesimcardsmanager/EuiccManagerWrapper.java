package com.reactnativesimcardsmanager;

import android.content.Context;
import android.os.Build;
import android.telephony.euicc.EuiccManager;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReactContext;

public class EuiccManagerWrapper {
  private EuiccManager mMgr;
  private final ReactContext mContext;

  EuiccManagerWrapper(ReactContext context) {
    mContext = context;
  }

  @RequiresApi(Build.VERSION_CODES.P)
  public EuiccManager getMgr() {
    if (this.mMgr == null) {
      this.mMgr = (EuiccManager) mContext.getSystemService(Context.EUICC_SERVICE);
    }
    return this.mMgr;
  }
}
