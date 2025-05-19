import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        KMMInject().doInitKMM(factory: Factory())
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
