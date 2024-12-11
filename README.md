
# react-native-sim-cards-manager

A unified library merging features from multiple SIM card management libraries:

- [react-native-esim](https://github.com/markneh/react-native-esim)
- [react-native-sim-data](https://github.com/pocesar/react-native-sim-data)

## Why use this library?

- Unified APIs for eSIM and SIM data management.
- Bug fixes and enhancements for improved stability.
- Simplified transition from previous libraries (requires some code adaptation).

---

## Installation

Install the package using npm:

```sh
npm install react-native-sim-cards-manager
```

---

## Debugging

### Android

To debug device logs during eSIM setup, use the following command:

```sh
adb logcat | grep "Euicc"
```

---

## Permissions

### Android

- **Minimum API Level:** 22
- Add the following permission in `AndroidManifest.xml` for `TelephonyManager`:

```xml
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

For **eSIM features**:

- For regular apps, follow the instructions in the [carrier privileges guide](https://source.android.com/docs/core/connect/esim-overview#carrier-privileges).
- For system apps, include the following permission:

```xml
<uses-permission android:name="android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS" />
```

More details [here](https://source.android.com/devices/tech/connect/esim-euicc-api#download-sub).

### iOS

- **Minimum API Level:** 12 (eSIM features)
- Requires eSIM entitlement. Learn more [here](https://stackoverflow.com/a/60162323).

**Note:** As of iOS 14.2, there is a known bug where an unknown result may be returned before completing eSIM setup. Details [here](https://developer.apple.com/forums/thread/662001).

---

## Usage

### Retrieve SIM Cards

Fetch a list of SIM cards. The result may contain no elements (if no SIM cards are detected) or multiple entries.

#### Method: `getSimCards`

Handles permission requests and accepts an optional `_rationale_` parameter.

```ts
import SimCardsManagerModule from 'react-native-sim-cards-manager';

SimCardsManagerModule.getSimCards({
  title: 'App Permission',
  message: 'Custom message',
  buttonNeutral: 'Not now',
  buttonNegative: 'Not OK',
  buttonPositive: 'OK',
})
  .then((array: Array<any>) => {
    // Handle results
  })
  .catch((error) => {
    // Handle errors
  });
```

#### Method: `getSimCardsNative`

Used internally by `getSimCards`. Does not handle permissions.

```ts
import SimCardsManagerModule from 'react-native-sim-cards-manager';

SimCardsManagerModule.getSimCardsNative()
  .then((array: Array<any>) => {
    // Handle results
  })
  .catch((error) => {
    // Handle errors
  });
```

#### Available Data

| Field             | iOS | Android |
| ------------------|-----|---------|
| carrierName       | ✅  | ✅      |
| displayName       | ❌  | ✅      |
| isoCountryCode    | ✅  | ✅      |
| mobileCountryCode | ✅  | ✅      |
| mobileNetworkCode | ✅  | ✅      |
| isNetworkRoaming  | ❌  | ✅      |
| isDataRoaming     | ❌  | ✅      |
| simSlotIndex      | ❌  | ✅      |
| phoneNumber       | ❌  | ✅      |
| simSerialNumber   | ❌  | ✅      |
| subscriptionId    | ❌  | ✅      |
| allowsVOIP        | ✅  | ❌      |

---

### eSIM Support Check

Verify if the device supports eSIM functionality.

```ts
import SimCardsManagerModule from 'react-native-sim-cards-manager';

SimCardsManagerModule.isEsimSupported()
  .then((isSupported: boolean) => {
    // Handle result
  })
  .catch((error) => {
    // Handle errors
  });
```

---

### eSIM Setup

Configure an eSIM using an activation code.

#### Parameters

| Field            | Mandatory | Description |
|------------------|-----------|-------------|
| address          | N/A       | Carrier network's eSIM server address. |
| confirmationCode | N/A       | Confirmation code for eSIM provisioning. |
| eid              | N/A       | eUICC identifier. |
| iccid            | N/A       | Integrated Circuit Card Identifier. |
| matchingId       | N/A       | Matching identifier for the eSIM profile. |
| oid              | N/A       | Object Identifier for the provisioning request. |

#### Error Codes

| Code             | Description |
|------------------|-------------|
| 0                | Feature not available. |
| 1                | Device does not support cellular plans. |
| 2                | OS failed to add the plan. |
| 3 (iOS)          | Unknown OS error. |
| 3 (Android)      | OS or Intent/Activity error. |

```ts
import SimCardsManagerModule from 'react-native-sim-cards-manager';

SimCardsManagerModule.setupEsim({
  confirmationCode,
  address: '',
})
  .then((isPlanAdded: boolean) => {
    // Handle success
  })
  .catch((error) => {
    // Handle errors
  });
```

---


## Changelog

### 1.0.26
- **Fix** Fix iOS LPA returning unknown before finishing eSIM setup by @Neeal20 in #91

### 1.0.24
- **Fix** Create Pending Intent with mutable flag to receive EUICC manager callback on Android 14 by @raffiot in #77
- **Fix** crash on android < sdk 28 by @MargusSalk in #83

### 1.0.22
- **Fix** Android flag MUTABLE to IMMUTABLE to solve an Android 14 issue

### 1.0.21
- **Fix** an issue introduced in 1.0.20

### 1.0.20
- **Fix** for Android 13 READ_PHONE_NUMBERS
- **Fix** for Android 14 registerReceiver()
- **Fix** iOS result success

### 1.0.18
- **Fix** setupEsim. Check for OS version P or above before continuing; return if below. 
- Moved initMgr() to after the version check.

### 1.0.12 & .13 & .14 & .15 & .16 & .17
- **Fix** npm package; missing index.ts file

### 1.0.11
- **Fix** Android - get phone number on API 33
- Android - ```sendPhoneCall``` with phone number and SIM slot index

### 1.0.10
- **Fix** Issue when the OS requires user action

### 1.0.9
- Improve package generation and add types declaration (one more time!)

### 1.0.8
- Improve package generation and add types declaration

### 1.0.7
- Upgrade packages (detected CVE)

### 1.0.6
- Added an extra check on eSIM method for Android (hasCarrierPrivileges())

### 1.0.5
- Fix bug with missing MUTABLE flag on intent for Android
- Remove some useless logs

### 1.0.4
- Improve eSIM resolvable error

### 1.0.3
- Fix bug with EMBEDDED_SUBSCRIPTION_RESULT_ERROR #12

### 1.0.2
- Remove 'deviceID' on Android because it requires high privilege
- Adding permission management on Android (READ_PHONE_STATE)

### 1.0.1
- Android bug fixes

### 1.0.0
- First stable release

### 0.9.9
- iOS implementation
- Android implementation
- RN implementation
- Documentation & sample project


## Contributing

Read the [Contributing Guide](CONTRIBUTING.md) for details on how to contribute to the project.

---

## License

MIT License.

---
