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

**Minimum API Level is 12 for eSIM methods**

Note: At the moment *(iOS 14.2)* there might a bug in iOS SDK which returns uknown result before eSIM setup completion. More about it [here](https://developer.apple.com/forums/thread/662001)

## Usage

### Get sim cards

The list can be empty (no simcards detected) or one/many element(s)

```ts
import { SimManager } from "react-native-sim-cards-manager";

SimCardsManagerModule.getSimCards().then((array: Array<any>)=>{
    //
}).catch((error)=>{
    //
})
```

Available set of data per platform:

| Platform          	| iOS 	| Android 	| 
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
| simSerialNumber   	|  ❌  	|    ✅    	|
| subscriptionId    	|  ❌  	|    ✅    	|
| allowsVOIP        	|  ✅  	|    ❌    	|

### Esim is supported

Return true/false is the device support eSim feature

```ts
import { SimManager } from "react-native-sim-cards-manager";

SimCardsManagerModule.isEsimSupported().then((isSupported: boolean)=>{
    //
}).catch((error)=>{
    //
})
```

### Setup eSim with an activation code

Entry parameters for the bridge:

| Entry parameters 	| Mandatory 	| Description                                                                                                     	|
|------------------	|-----------	|-----------------------------------------------------------------------------------------------------------------	|
| address          	|     N/A   	| The address of the carrier network’s eSIM server                                                                	|
| confirmationCode 	|     N/A   	| The provisioning request’s confirmation code, provided by the network operator when initiating an eSIM download 	|
| iccid            	|     N/A   	| The provisioning request’s eUICC identifier                                                                     	|
| address          	|     N/A   	| The provisioning request’s Integrated Circuit Card Identifier                                                   	|
| matchingId       	|     N/A   	| The provisioning request’s matching identifier                                                                  	|
| oid              	|     N/A   	| The provisioning request’s Object Identifier                                                                    	|

Error code that can be returned by the bridge:

| Error code   	| Description                                                                       	|
|--------------	|-----------------------------------------------------------------------------------	|
| 0            	| Feature not available for that OS / device                                        	|
| 1            	| The device doesn't support a cellular plan                                        	|
| 2            	| The OS failed to add the new plan                                                 	|
| 3 **(iOS)**    	| The OS has returned an unknow error                                               	|
| 3 **(Android)** 	| The OS has returned an error **or** something goes wrong with the Intent/Activity 	|
```ts
import { SimManager } from "react-native-sim-cards-manager";

SimCardsManagerModule.setupEsim({
    confirmationCode, address: '',
}).then((isPlanAdded: boolean)=>{
  //
}).catch((error)=>{
  //
})
```


## Changelog

### 1.0.1
- Remove 'deviceID' on Android because it require high privilege
- Adding permission management on Android (READ_PHONE_STATE)

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
