import SwiftUI

@main
struct OkinawaCounterApp: App {
  var body: some Scene {
    WindowGroup("沖縄ビジネスITフェス受付カウンター") {
      CounterView()
        .preferredColorScheme(.light)
        #if os(macOS)
          .frame(minWidth: 420, minHeight: 640)
        #endif
    }
    #if os(macOS)
      .defaultSize(width: 540, height: 760)
    #endif
  }
}
