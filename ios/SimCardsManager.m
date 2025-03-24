#import "SimCardsManager.h"

#import <CoreTelephony/CTCallCenter.h>
#import <CoreTelephony/CTCall.h>
#import <CoreTelephony/CTCarrier.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>
#import <CoreTelephony/CTCellularPlanProvisioning.h>

API_AVAILABLE(ios(12.0))
CTCellularPlanProvisioning *plan;

API_AVAILABLE(ios(12.0))
CTCellularPlanProvisioningRequest *request;

@interface SimCardsManager ()

@property (strong, nonatomic) CTTelephonyNetworkInfo *telephonyNetworkInfo;

@end

@implementation SimCardsManager

RCT_EXPORT_MODULE()

- (instancetype)init {
    self = [super init];
    if (self) {
        _telephonyNetworkInfo = [[CTTelephonyNetworkInfo alloc] init];
    }
    return self;
}

RCT_EXPORT_METHOD(getSimCardsNative:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    NSMutableArray *simCardsList = [[NSMutableArray alloc] init];

    if (@available(iOS 12.0, *)) {
        NSDictionary *providers = [self.telephonyNetworkInfo serviceSubscriberCellularProviders];
        NSMutableDictionary *simCard = [[NSMutableDictionary alloc] init];

        for (NSString *aProvider in providers) {
            CTCarrier *aCarrier = providers[aProvider];
            [simCard setValue:[NSString stringWithFormat:@"%i", [aCarrier allowsVOIP]] forKey:@"allowsVOIP"];
            [simCard setValue:[aCarrier carrierName] forKey:@"carrierName"];
            [simCard setValue:[aCarrier isoCountryCode] forKey:@"isoCountryCode"];
            [simCard setValue:[aCarrier mobileNetworkCode] forKey:@"mobileNetworkCode"];
            [simCard setValue:[aCarrier mobileCountryCode] forKey:@"mobileCountryCode"];
        }

        [simCardsList addObject:simCard];

    } else {
        // Support for older version, can return only 1 simcard in the list (subscriberCellularProvider)
        NSMutableDictionary *simCard = [[NSMutableDictionary alloc] init];

        CTCarrier *aCarrier = [self.telephonyNetworkInfo subscriberCellularProvider];

        // Catch no-sim case
        if ([aCarrier isoCountryCode] != nil) {
            [simCard setValue:[NSString stringWithFormat:@"%i", [aCarrier allowsVOIP]] forKey:@"allowsVOIP"];
            [simCard setValue:[aCarrier carrierName] forKey:@"carrierName"];
            [simCard setValue:[aCarrier isoCountryCode] forKey:@"isoCountryCode"];
            [simCard setValue:[aCarrier mobileNetworkCode] forKey:@"mobileNetworkCode"];
            [simCard setValue:[aCarrier mobileCountryCode] forKey:@"mobileCountryCode"];
            [simCardsList addObject:simCard];
        }
    }
    resolve(simCardsList);
}

RCT_EXPORT_METHOD(isEsimSupported:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    if (@available(iOS 12.0, *)) {
        plan = [[CTCellularPlanProvisioning alloc] init];
        resolve(@(plan.supportsCellularPlan));
    } else {
        NSError *error = [NSError errorWithDomain:@"react.native.simcardsmanager.handler" code:1 userInfo:nil];
        reject(@"0", @"This functionality is not supported before iOS 12.0", error);
    }
}

RCT_EXPORT_METHOD(setupEsim:(NSDictionary *)config
                  promiseWithResolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
    if (@available(iOS 12.0, *)) {
        plan = [[CTCellularPlanProvisioning alloc] init];

        if (plan.supportsCellularPlan == NO) {
            NSError *error = [NSError errorWithDomain:@"react.native.simcardsmanager.handler" code:2 userInfo:nil];
            reject(@"1", @"The device doesn't support a cellular plan", error);
        } else {
            request = [[CTCellularPlanProvisioningRequest alloc] init];
            request.OID = config[@"oid"];
            request.EID = config[@"eid"];
            request.ICCID = config[@"iccid"];
            request.address = config[@"address"];
            request.matchingID = config[@"matchingId"];
            request.confirmationCode = config[@"confirmationCode"];

            UIBackgroundTaskIdentifier backgroundTaskIdentifier = [[UIApplication sharedApplication] beginBackgroundTaskWithExpirationHandler:^{}];

            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                [plan addPlanWith:request completionHandler:^(CTCellularPlanProvisioningAddPlanResult result) {
                    if (result == CTCellularPlanProvisioningAddPlanResultFail) {
                        NSError *error = [NSError errorWithDomain:@"react.native.simcardsmanager.handler" code:1 userInfo:nil];
                        reject(@"2", @"CTCellularPlanProvisioningAddPlanResultFail - Can't add an Esim subscription", error);
                    } else if (result == CTCellularPlanProvisioningAddPlanResultUnknown) {
                        NSError *error = [NSError errorWithDomain:@"react.native.simcardsmanager.handler" code:1 userInfo:nil];
                        reject(@"3", @"CTCellularPlanProvisioningAddPlanResultUnknown - Can't setup eSim due to unknown error", error);
                    } else {
                        // CTCellularPlanProvisioningAddPlanResultSuccess or CTCellularPlanProvisioningAddPlanResultCancel
                        resolve(@(result == CTCellularPlanProvisioningAddPlanResultSuccess));
                    }
                    [[UIApplication sharedApplication] endBackgroundTask:backgroundTaskIdentifier];
                }];
            });
        }

    } else {
        NSError *error = [NSError errorWithDomain:@"react.native.simcardsmanager.handler" code:1 userInfo:nil];
        reject(@"0", @"This functionality is not supported before iOS 12.0", error);
    }
}

@end
