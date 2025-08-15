import SwiftUI
import GoogleSignIn
import Firebase
import shared
import FirebaseCore
import FirebaseMessaging
import ComposeApp
import UserNotifications

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            ContentView().ignoresSafeArea() .onOpenURL { url in
                print("Received URL in onOpenURL: \(url)")
                
                if GIDSignIn.sharedInstance.handle(url) { return }
                
                guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true),
                      let queryItems = components.queryItems else { return }
                
                let success = queryItems.first(where: { $0.name == "success" })?.value == "true"
                let cancel = queryItems.first(where: { $0.name == "cancel" })?.value == "true"
                let token = queryItems.first(where: { $0.name == "token" })?.value
                
                print(
                    """
                        ✅ Success: \(success)
                        ✅ Cancel: \(cancel)
                        ✅ Token: \(token ?? "null")
                    """
                )
                
                
                PreferencesRepository().savePayPalData(
                    isSuccess: success ? KotlinBoolean(value: true) : nil,
                    error: cancel ? "Payment canceled." : nil,
                    token: token
                )
            }
        }
    }
}

final class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        // Firebase
        FirebaseApp.configure()

        // KMPNotifier does the simple path: ask permission + register for APNs for you.
        NotifierManager.shared.initialize(configuration: NotificationPlatformConfigurationIos(
            showPushNotification: true,          // show a banner when app is foreground (via local)
            askNotificationPermissionOnStart: true, // let library prompt + register
            notificationSoundName: nil
        ))

        // Foreground banners from iOS (nice to have even if showPushNotification=true)
        UNUserNotificationCenter.current().delegate = self

        // Optional: log/forward FCM token
        Messaging.messaging().delegate = self
        
#if DEBUG
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            if let t = Messaging.messaging().apnsToken {
                print("✅ APNs token present (\(t.count) bytes) \(t as NSData)")
            } else {
                print("❌ APNs token is nil")
            }
        }
#endif
        
        return true
    }
    // Optional with swizzling ON (harmless to keep; helps when swizzling is off)
    func application(_ application: UIApplication,
                     didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }

    // Foreground presentation (if you want iOS to show the system banner)
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification) async -> UNNotificationPresentationOptions {
        [.banner, .sound, .badge]
    }

    // Token logging (handy while testing)
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("🔥 FCM token: \(fcmToken ?? "nil")")
    }
}
