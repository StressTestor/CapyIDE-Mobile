import SwiftUI
import composeApp
import GoogleMobileAds

@main
struct CapyIDEApp: SwiftUI.App {
    @StateObject private var storeManager = StoreManager.shared

    init() {
        KoinHelperKt.doInitKoin()
        MobileAds.shared.start(completionHandler: nil)
    }

    var body: some Scene {
        WindowGroup {
            VStack(spacing: 0) {
                ComposeContainerView(storeManager: storeManager)
                    .ignoresSafeArea(.all)

                if !storeManager.isSubscribed {
                    BannerAdView()
                        .frame(height: 50)
                }
            }
            .sheet(isPresented: $storeManager.showPaywall) {
                PaywallView()
                    .environmentObject(storeManager)
            }
        }
    }
}

struct ComposeContainerView: UIViewControllerRepresentable {
    let storeManager: StoreManager

    func makeUIViewController(context: Context) -> UIViewController {
        MainKt.MainViewController(
            onSubscribeClick: {
                Task { @MainActor in
                    storeManager.showPaywall = true
                }
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
