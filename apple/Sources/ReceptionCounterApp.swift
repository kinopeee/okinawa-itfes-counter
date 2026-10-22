import SwiftUI

@main
struct ReceptionCounterApp: App {
    var body: some Scene {
        WindowGroup("沖縄ビジネスITフェス受付カウンター") {
            ContentView()
        }
        #if os(macOS)
        .defaultSize(width: 560, height: 480)
        .windowResizability(.contentMinSize)
        #endif
    }
}
