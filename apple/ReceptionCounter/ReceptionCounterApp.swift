import SwiftUI

@main
struct ReceptionCounterApp: App {
    var body: some Scene {
        WindowGroup("沖縄ビジネスITフェス受付カウンター") {
            CounterView()
                .preferredColorScheme(.light)
                #if os(macOS)
                .frame(minWidth: 440, minHeight: 620)
                #endif
        }
        #if os(macOS)
        .defaultSize(width: 560, height: 740)
        #endif
    }
}
