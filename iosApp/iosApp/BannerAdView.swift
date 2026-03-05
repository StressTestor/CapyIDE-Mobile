import SwiftUI
import GoogleMobileAds

struct BannerAdView: UIViewRepresentable {
    // Test ad unit ID - replace with real ID before App Store submission
    private let adUnitID = "ca-app-pub-3940256099942544/2934735716"

    func makeUIView(context: Context) -> BannerView {
        let banner = BannerView(adSize: AdSizeBanner)
        banner.adUnitID = adUnitID
        banner.translatesAutoresizingMaskIntoConstraints = false

        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let rootVC = windowScene.windows.first?.rootViewController {
            banner.rootViewController = rootVC
        }

        banner.load(Request())
        return banner
    }

    func updateUIView(_ uiView: BannerView, context: Context) {}
}

// fallback if AdMob SDK isn't linked yet - shows a placeholder
struct PlaceholderAdView: View {
    var body: some View {
        Rectangle()
            .fill(Color(.systemGray6))
            .frame(height: 50)
            .overlay(
                Text("ad space")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            )
    }
}
