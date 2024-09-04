# react-native-sim-cards-manager

A new library that merge multiple sim cards libraries into a single one:

- https://github.com/markneh/react-native-esim
- https://github.com/pocesar/react-native-sim-data

Bridges have been adapted, so you will have to adapt your code if you come from one of those libraries.

Bugs fixed and some improvements have been made.

## Installation

```sh
npm install react-native-sim-cards-manager
```

### Debug

### Android
In order to debug the device during the setup of the eSIM to obtain more detailed logs, please use the following command to filter the logs:
```shell
adb logcat | grep "Euicc"
```

### Permissions

#### Android

**Minimum API Level is 22**

_TelephonyManager_ require Android permission **READ_PHONE_STATE**:

Add in your Android Manifest:

```xml
<uses-permission
          android:name="android.permission.READ_PHONE_STATE" />
```



To use eSIM features, **for regular apps**, please [follow this](https://source.android.com/docs/core/connect/esim-overview#carrier-privileges)


To use eSIM features, **for system apps**, add in your Android Manifest:

```xml
<uses-permission
          android:name="android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS" />
```

More info [here](https://source.android.com/devices/tech/connect/esim-euicc-api#download-sub)




#### iOS

**This API won't work without eSIM entitlement. You can read more about it [here](https://stackoverflow.com/a/60162323)**

**Minimum API Level is 12 for eSIM methods**

Note: At the moment _(iOS 14.2)_ there might a bug in iOS SDK which returns uknown result before eSIM setup completion. More about it [here](https://developer.apple.com/forums/thread/662001)

## Usage

### Get sim cards

The list can be empty (no simcards detected) or one/many element(s)
Two methods are availabe :

#### Method getSimCards

This handle the permission request and takes an optionnal _rationale_ Parameter

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
    //
  })
  .catch((error) => {
    //
  });
```

#### Method getSimCardsNative

This is the method used internally by getSimCards. It does not handle the permission request and leaves the user of this lib.

```ts
import SimCardsManagerModule from 'react-native-sim-cards-manager';

SimCardsManagerModule.getSimCardsNative()
  .then((array: Array<any>) => {
    //
  })
  .catch((error) => {
    //
  });
```

Available set of data per platform:

| Platform          | iOS | Android |
| ----------------- | --- | ------- |
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

### Esim is supported

Return true/false is the device support eSim feature

```ts
import SimCardsManagerModule from 'react-native-sim-cards-manager';

SimCardsManagerModule.isEsimSupported()
  .then((isSupported: boolean) => {
    //
  })
  .catch((error) => {
    //
  });
```

### Setup eSim with an activation code

Entry parameters for the bridge:

| Entry parameters | Mandatory | Description                                                                                                     |
| ---------------- | --------- | --------------------------------------------------------------------------------------------------------------- |
| address          | N/A       | The address of the carrier network’s eSIM server                                                                |
| confirmationCode | N/A       | The provisioning request’s confirmation code, provided by the network operator when initiating an eSIM download |
| eid            | N/A       | The provisioning request’s eUICC identifier                                                                     |
| iccid          | N/A       | The provisioning request’s Integrated Circuit Card Identifier                                                   |
| matchingId       | N/A       | The provisioning request’s matching identifier                                                                  |
| oid              | N/A       | The provisioning request’s Object Identifier                                                                    |

Error code that can be returned by the bridge:

| Error code      | Description                                                                       |
| --------------- | --------------------------------------------------------------------------------- |
| 0               | Feature not available for that OS / device                                        |
| 1               | The device doesn't support a cellular plan                                        |
| 2               | The OS failed to add the new plan                                                 |
| 3 **(iOS)**     | The OS has returned an unknow error                                               |
| 3 **(Android)** | The OS has returned an error **or** something goes wrong with the Intent/Activity |

```ts
import SimCardsManagerModule from 'react-native-sim-cards-manager';

SimCardsManagerModule.setupEsim({
  confirmationCode,
  address: '',
})
  .then((isPlanAdded: boolean) => {
    //
  })
  .catch((error) => {
    //
  });
```

## Changelog

### 1.0.24

- **Fix** Create Pending Intent with mutable flag to receive EUICC manager callback on Android 14 by @raffiot in #77
- **Fix** crash on android < sdk 28 by @MargusSalk in #83

### 1.0.22

- **Fix** Android flag MUTABLE to IMMUTABLE to solve a Android 14 issue

### 1.0.21

- **Fix** an issue introduced in 1.0.20

### 1.0.20

- **Fix** for Android 13 READ_PHONE_NUMBERS
- **Fix** for Android 14 registerReceiver()
- **Fix** iOS result success

### 1.0.20

- **Fix** for Android 13 READ_PHONE_NUMBERS
- **Fix** for Android 14 registerReceiver()
- **Fix** iOS result success

### 1.0.18

- **Fix** fix setupEsim. check for OS version P or above, before continuing and return if below. 
- Moved initMgr() to after the version check.

### 1.0.12 & .13 & .14 & .15 & .16 & .17

- **Fix** npm package; missing index.ts file

### 1.0.11

- **Fix** Android -  get phone number on API 33
- Android - ```sendPhoneCall``` with phone number and sim slot index

### 1.0.10

- **Fix** Fix issue when the OS requires user action

### 1.0.9

- Improve package generation and add types declaration (one more time !)

### 1.0.8

- Improve package generation and add types declaration

### 1.0.7

- Upgrade packages (detected CVE)

### 1.0.6

- Added an extra check on eSim method for Android (hasCarrierPrivileges())

### 1.0.5

- Fix bug with missing MUTABLE flag on intent for Android
- Remove some useless logs

### 1.0.4

- Improve esim resolvable error

### 1.0.3

- Fix bug with EMBEDDED_SUBSCRIPTION_RESULT_ERROR #12

### 1.0.2

- Remove 'deviceID' on Android because it require high privilege
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

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
