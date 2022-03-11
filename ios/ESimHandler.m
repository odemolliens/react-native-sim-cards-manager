#import "ESimHandler.h"

@implementation ESimHandler

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(isEsimSupported:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {

    if(@available(iOS 12.0, *)){
        CTCellularPlanProvisioning *plan = [[CTCellularPlanProvisioning alloc] init];
        resolve(@(plan.supportsCellularPlan));
    } else {
        NSError *error = [NSError errorWithDomain:@"react.native.esim.handler" code:1 userInfo:nil];
        reject(@"iOS 12 api availability", @"This functionality is not supported before iOS 12.0", error);
    }
}

RCT_EXPORT_METHOD(setupEsim:(NSDictionary *)config
                  promiseWithResolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
    
    if (@available(iOS 12.0, *)) {
        CTCellularPlanProvisioning *plan = [[CTCellularPlanProvisioning alloc] init];
        
        if (plan.supportsCellularPlan != YES) {
            NSError *error = [NSError errorWithDomain:@"react.native.esim.handler" code:2 userInfo:nil];
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
        NSError *error = [NSError errorWithDomain:@"react.native.esim.handler" code:1 userInfo:nil];
        reject(@"iOS 12 api availability", @"This functionality is not supported before iOS 12.0", error);
    }
}
@end
