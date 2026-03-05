import Foundation
import StoreKit

@MainActor
class StoreManager: ObservableObject {
    static let shared = StoreManager()

    @Published var isSubscribed = false
    @Published var products: [Product] = []
    @Published var purchaseError: String?
    @Published var isLoading = false
    @Published var showPaywall = false

    private let productIds = [
        "dev.capyide.mobile.pro.monthly",
        "dev.capyide.mobile.pro.yearly"
    ]

    var monthlyProduct: Product? {
        products.first { $0.id == "dev.capyide.mobile.pro.monthly" }
    }

    var yearlyProduct: Product? {
        products.first { $0.id == "dev.capyide.mobile.pro.yearly" }
    }

    private var updateTask: Task<Void, Never>?

    init() {
        updateTask = Task {
            await loadProducts()
            await updateSubscriptionStatus()
            await listenForTransactions()
        }
    }

    deinit {
        updateTask?.cancel()
    }

    func loadProducts() async {
        do {
            let storeProducts = try await Product.products(for: productIds)
            products = storeProducts.sorted { $0.price < $1.price }
        } catch {
            purchaseError = "Failed to load products: \(error.localizedDescription)"
        }
    }

    func purchase(_ product: Product) async {
        isLoading = true
        purchaseError = nil

        do {
            let result = try await product.purchase()

            switch result {
            case .success(let verification):
                let transaction = try checkVerification(verification)
                await transaction.finish()
                await updateSubscriptionStatus()
                showPaywall = false

            case .pending:
                purchaseError = "Purchase is pending approval"

            case .userCancelled:
                break

            @unknown default:
                break
            }
        } catch {
            purchaseError = "Purchase failed: \(error.localizedDescription)"
        }

        isLoading = false
    }

    func restorePurchases() async {
        isLoading = true
        try? await AppStore.sync()
        await updateSubscriptionStatus()
        isLoading = false
    }

    private func updateSubscriptionStatus() async {
        var hasActive = false

        for productId in productIds {
            guard let result = await Transaction.currentEntitlement(for: productId) else {
                continue
            }

            do {
                let transaction = try checkVerification(result)
                if transaction.revocationDate == nil {
                    hasActive = true
                }
            } catch {
                continue
            }
        }

        isSubscribed = hasActive
    }

    private func listenForTransactions() async {
        for await result in Transaction.updates {
            do {
                let transaction = try checkVerification(result)
                await transaction.finish()
                await updateSubscriptionStatus()
            } catch {
                // verification failed
            }
        }
    }

    private func checkVerification<T>(_ result: VerificationResult<T>) throws -> T {
        switch result {
        case .unverified(_, let error):
            throw error
        case .verified(let item):
            return item
        }
    }
}
