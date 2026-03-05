import SwiftUI
import StoreKit

struct PaywallView: View {
    @EnvironmentObject var storeManager: StoreManager
    @Environment(\.dismiss) var dismiss

    var body: some View {
        NavigationStack {
            VStack(spacing: 24) {
                Spacer()

                // header
                VStack(spacing: 8) {
                    Text("CapyIDE Pro")
                        .font(.largeTitle)
                        .fontWeight(.bold)

                    Text("remove ads and support development")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }

                // features
                VStack(alignment: .leading, spacing: 12) {
                    featureRow(icon: "xmark.circle", text: "no banner ads")
                    featureRow(icon: "bolt.fill", text: "priority AI responses")
                    featureRow(icon: "heart.fill", text: "support indie development")
                }
                .padding(.horizontal, 32)

                Spacer()

                // subscription options
                VStack(spacing: 12) {
                    if let monthly = storeManager.monthlyProduct {
                        SubscriptionButton(
                            product: monthly,
                            label: "monthly",
                            sublabel: "\(monthly.displayPrice)/mo",
                            isLoading: storeManager.isLoading
                        ) {
                            Task { await storeManager.purchase(monthly) }
                        }
                    }

                    if let yearly = storeManager.yearlyProduct {
                        SubscriptionButton(
                            product: yearly,
                            label: "yearly",
                            sublabel: "\(yearly.displayPrice)/yr — save ~58%",
                            isLoading: storeManager.isLoading,
                            highlighted: true
                        ) {
                            Task { await storeManager.purchase(yearly) }
                        }
                    }

                    if storeManager.products.isEmpty {
                        Text("loading subscription options...")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }
                }
                .padding(.horizontal, 24)

                if let error = storeManager.purchaseError {
                    Text(error)
                        .font(.caption)
                        .foregroundStyle(.red)
                        .padding(.horizontal)
                }

                // restore + terms
                VStack(spacing: 8) {
                    Button("restore purchases") {
                        Task { await storeManager.restorePurchases() }
                    }
                    .font(.caption)
                    .foregroundStyle(.secondary)

                    HStack(spacing: 16) {
                        Link("privacy policy", destination: URL(string: "https://capyide.dev/privacy")!)
                            .font(.caption2)
                        Link("terms of use", destination: URL(string: "https://www.apple.com/legal/internet-services/itunes/dev/stdeula/")!)
                            .font(.caption2)
                    }
                    .foregroundStyle(.secondary)
                }
                .padding(.bottom, 16)
            }
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("close") { dismiss() }
                }
            }
        }
    }

    private func featureRow(icon: String, text: String) -> some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .foregroundStyle(Color(red: 0.357, green: 0.369, blue: 0.733)) // CapyIndigo
                .frame(width: 24)
            Text(text)
                .font(.body)
        }
    }
}

struct SubscriptionButton: View {
    let product: Product
    let label: String
    let sublabel: String
    let isLoading: Bool
    var highlighted: Bool = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            VStack(spacing: 4) {
                if isLoading {
                    ProgressView()
                        .tint(.white)
                } else {
                    Text(label)
                        .font(.headline)
                    Text(sublabel)
                        .font(.caption)
                        .opacity(0.8)
                }
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 14)
            .background(highlighted ?
                Color(red: 0.357, green: 0.369, blue: 0.733) :
                Color(red: 0.357, green: 0.369, blue: 0.733).opacity(0.7)
            )
            .foregroundStyle(.white)
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
        .disabled(isLoading)
    }
}
