import { NativeModules, PermissionsAndroid, Platform, Rationale } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-sim-cards-manager' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const SimCardsManagerModule = NativeModules.SimCardsManager
  ? NativeModules.SimCardsManager
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export async function requestCellularNetworkPermission(then: () => any, rationale?: Rationale) {
  if (Platform.OS == 'android') {
    let granted = PermissionsAndroid.RESULTS.DENIED;
    if(Platform.Version >= 33){ 
        granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.READ_PHONE_NUMBERS, 
          rationale ?? { 
            title: 'App Permission', 
            message: 'App needs access to get informations of your cellular network', 
            buttonNeutral: 'Ask Me Later', 
            buttonNegative: 'Cancel', 
            buttonPositive: 'OK', 
          }); 
      }
      granted = await PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE, 
        rationale ?? { 
          title: 'App Permission', 
          message: 'App needs access to get informations of your cellular network', 
          buttonNeutral: 'Ask Me Later', 
          buttonNegative: 'Cancel', 
          buttonPositive: 'OK', 
        }); 

    console.log(granted);
    console.log(PermissionsAndroid.RESULTS);
    if (
      granted === PermissionsAndroid.RESULTS.GRANTED ||
      granted === PermissionsAndroid.RESULTS.NEVER_ASK_AGAIN
    ) {
      return then();
    } else {
      return Promise.reject({ code: '3', message: 'Permission not granted' });
    }
  } else {
    return then();
  }
}

export type EsimConfig = {
  address: string;
  confirmationCode?: string;
  eid?: string;
  iccid?: string;
  matchingId?: string;
  oid?: string;
};

type SimManager = {
  getSimCards(rationale?: Rationale): Promise<Array<any>>; // Permission request handled by react-native-sim-cards-manager
  getSimCardsNative(): Promise<Array<any>>; // Permission request left to be handled by the user
  setupEsim(config: EsimConfig): Promise<boolean | never>;
  isEsimSupported(): Promise<boolean | never>;
};

SimCardsManagerModule.getSimCards = async () => {
  return await requestCellularNetworkPermission(NativeModules.SimCardsManager.getSimCardsNative);
};

export default SimCardsManagerModule as SimManager;
