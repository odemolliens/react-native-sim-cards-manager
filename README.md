# 📱 react-native-sim-cards-manager

A unified library merging features from multiple SIM card management libraries:

- [react-native-esim](https://github.com/markneh/react-native-esim)
- [react-native-sim-data](https://github.com/pocesar/react-native-sim-data)

---

## 🚀 Why use this library?

✅ Unified APIs for eSIM and SIM data management.  
✅ Bug fixes and enhancements for improved stability.  
✅ Simplified transition from previous libraries (requires some code adaptation).  

---

## 📦 Installation

Install the package using npm:

```sh
npm install react-native-sim-cards-manager
```

---

## 🛠 Debugging

### Android

To debug device logs during eSIM setup, use the following command:

```sh
adb logcat | grep "Euicc"
```

---

## 🔐 Permissions

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

⚠️ **Note:** As of iOS 14.2, there is a known bug where an unknown result may be returned before completing eSIM setup. Details [here](https://developer.apple.com/forums/thread/662001).

---

## 📖 Usage

### 📲 Retrieve SIM Cards

Fetch a list of SIM cards. The result may contain no elements (if no SIM cards are detected) or multiple entries.

#### 📌 Method: `getSimCards`

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

#### 📌 Method: `getSimCardsNative`

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

#### 📊 Available Data

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

### 🔍 eSIM Support Check

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

### ⚙️ eSIM Setup

Configure an eSIM using an activation code.

#### 📌 Parameters

| Field            | Mandatory | Description |
|------------------|-----------|-------------|
| address          | N/A       | Carrier network's eSIM server address. |
| confirmationCode | N/A       | Confirmation code for eSIM provisioning. |
| eid              | N/A       | eUICC identifier. |
| iccid            | N/A       | Integrated Circuit Card Identifier. |
| matchingId       | N/A       | Matching identifier for the eSIM profile. |
| oid              | N/A       | Object Identifier for the provisioning request. |

#### ⚠️ Error Codes

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

## 📝 Changelog

[Changelog content remains unchanged]  

---

## 👨‍💻 Contributing

Read the [Contributing Guide](CONTRIBUTING.md) for details on how to contribute to the project.

---

## 📜 License

MIT License.

---
