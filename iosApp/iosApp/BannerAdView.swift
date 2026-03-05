import SwiftUI
import GoogleMobileAds

struct BannerAdView: UIViewRepresentable {
    private let adUnitID = "ca-app-pub-5480610103122843/4321272662"

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
