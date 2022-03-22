#import "SimCardsManager.h"

#import<CoreTelephony/CTCallCenter.h>
#import<CoreTelephony/CTCall.h>
#import<CoreTelephony/CTCarrier.h>
#import<CoreTelephony/CTTelephonyNetworkInfo.h>
#import<CoreTelephony/CTCellularPlanProvisioning.h>

@implementation SimCardsManager

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(getSimCards:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {

    NSMutableArray *simCardsList = [[NSMutableArray alloc]init];
    
    CTTelephonyNetworkInfo *networkInfo = [[CTTelephonyNetworkInfo alloc]init];
    if (@available(iOS 12.0, *)) {
        NSDictionary *providers = [networkInfo serviceSubscriberCellularProviders];
        NSMutableDictionary *simCard = [[NSMutableDictionary alloc]init];
        
        for (NSString *aProvider in providers) {
            CTCarrier *aCarrier = providers[aProvider];
            [simCard setValue:[NSString stringWithFormat:@"%@",[aCarrier allowsVOIP]]forKey:@"allowsVOIP"];
            [simCard setValue:[aCarrier carrierName] forKey:@"carrierName"];
            [simCard setValue:[aCarrier isoCountryCode] forKey:@"isoCountryCode"];
            [simCard setValue:[aCarrier mobileNetworkCode] forKey:@"mobileNetworkCode"];
            [simCard setValue:[aCarrier mobileCountryCode] forKey:@"mobileCountryCode"];
        }
        
        [simCardsList addObject: simCard];
    } else {
        NSError *error = [NSError errorWithDomain:@"react.native.simcardsmanager.handler" code:1 userInfo:nil];
        reject(@"iOS 12 api availability", @"This functionality is not supported before iOS 12.0", error);
    }
}

RCT_EXPORT_METHOD(isEsimSupported:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {

    if(@available(iOS 12.0, *)){
        CTCellularPlanProvisioning *plan = [[CTCellularPlanProvisioning alloc] init];
        resolve(@(plan.supportsCellularPlan));
    } else {
        NSError *error = [NSError errorWithDomain:@"react.native.simcardsmanager.handler" code:1 userInfo:nil];
        reject(@"iOS 12 api availability", @"This functionality is not supported before iOS 12.0", error);
    }
}

RCT_EXPORT_METHOD(setupEsim:(NSDictionary *)config
                  promiseWithResolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
    
    if (@available(iOS 12.0, *)) {
        CTCellularPlanProvisioning *plan = [[CTCellularPlanProvisioning alloc] init];
        
        if (plan.supportsCellularPlan != YES) {
            NSError *error = [NSError errorWithDomain:@"react.native.simcardsmanager.handler" code:2 userInfo:nil];
            reject(@"Doesn't support cellular plan", @"This functionality is not supported on this device", error);
        } else {
            CTCellularPlanProvisioningRequest *request = [[CTCellularPlanProvisioningRequest alloc] init];
            request.OID = config[@"oid"];
            request.EID = config[@"eid"];
            request.ICCID = config[@"iccid"];
            request.address = config[@"address"];
            request.matchingID = config[@"matchingId"];
            request.confirmationCode = config[@"confirmationCode"];
            
            UIBackgroundTaskIdentifier backgroundTaskIdentifier = [[UIApplication sharedApplication] beginBackgroundTaskWithExpirationHandler:^{}];
            
            [plan addPlanWith:request completionHandler:^(CTCellularPlanProvisioningAddPlanResult result) {
                resolve(@(result));
                [[UIApplication sharedApplication] endBackgroundTask:backgroundTaskIdentifier];
            }];
        }
        
    } else {
        NSError *error = [NSError errorWithDomain:@"react.native.simcardsmanager.handler" code:1 userInfo:nil];
        reject(@"iOS 12 api availability", @"This functionality is not supported before iOS 12.0", error);
    }
}

@end
