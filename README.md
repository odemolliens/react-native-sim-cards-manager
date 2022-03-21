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

### Permissions

#### Android

**Minimum API Level is 22**

*TelephonyManager* require Android permission **READ_PHONE_STATE**:

Add in your Android Manifest:
```xml
<uses-permission
          android:name="android.permission.READ_PHONE_STATE" />
```

#### iOS

**This API won't work without eSIM entitlement. You can read more about it [here](https://stackoverflow.com/a/60162323)**

Note: At the moment *(iOS 14.2)* there might a bug in iOS SDK which returns uknown result before eSIM setup completion. More about it [here](https://developer.apple.com/forums/thread/662001)

## Usage


```ts
import { multiply } from "react-native-sim-cards-manager";

// ...

const result = await multiply(3, 7);
```

### Methods

#### Get sim cards

```ts
import { multiply } from "react-native-sim-cards-manager";

// ...

const result = await multiply(3, 7);
```

Available set of data per platform:

| Platform          	| iOS 	| Android 	| Description 	|
|-------------------	|-----	|---------	|
| carrierName       	|  ✅  	|    ✅    	|
| displayName       	|  ❌  	|    ✅    	|
| isoCountryCode    	|  ✅  	|    ✅    	|
| mobileCountryCode 	|  ✅  	|    ✅    	|
| mobileNetworkCode 	|  ✅  	|    ✅    	|
| isNetworkRoaming  	|  ❌  	|    ✅    	|
| isDataRoaming     	|  ❌  	|    ✅    	|
| simSlotIndex      	|  ❌  	|    ✅    	|
| phoneNumber       	|  ❌  	|    ✅    	|
| deviceId          	|  ❌  	|    ✅    	|
| simSerialNumber   	|  ❌  	|    ✅    	|
| subscriptionId    	|  ❌  	|    ✅    	|
| allowsVOIP        	|  ✅  	|    ❌    	|

#### Esim supported

```ts
import { multiply } from "react-native-sim-cards-manager";

// ...

const result = await multiply(3, 7);
```

#### Setup eSim with an activation code

```ts
import { multiply } from "react-native-sim-cards-manager";

// ...

const result = await multiply(3, 7);
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
