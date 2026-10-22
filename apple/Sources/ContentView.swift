import SwiftUI

struct ContentView: View {
    @State private var counter = VisitorCounter()

    var body: some View {
        VStack(spacing: 28) {
            VStack(spacing: 8) {
                Image(systemName: "person.3.fill")
                    .font(.largeTitle)
                    .foregroundStyle(.tint)
                    .accessibilityHidden(true)
                Text("沖縄ビジネスITフェス受付カウンター")
                    .font(.title2.bold())
                    .multilineTextAlignment(.center)
                Text("定員 \(VisitorCounter.capacity)席")
                    .foregroundStyle(.secondary)
            }

            VStack(spacing: 12) {
                Text("来場者数")
                    .font(.headline)
                Text("\(counter.count)人")
                    .font(.system(size: 64, weight: .bold, design: .rounded))
                    .monospacedDigit()
                    .minimumScaleFactor(0.5)
                    .lineLimit(1)
                    .accessibilityIdentifier("visitorCount")

                HStack(spacing: 12) {
                    Text("\(counter.percentage)%")
                        .font(.title.bold())
                        .monospacedDigit()
                        .accessibilityIdentifier("capacityPercentage")
                    if counter.isOverCapacity {
                        Text("定員オーバー")
                            .font(.headline)
                            .foregroundStyle(.red)
                            .accessibilityIdentifier("overCapacityWarning")
                    }
                }
                .frame(minHeight: 36)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 24)
            .background(.quaternary.opacity(0.5), in: RoundedRectangle(cornerRadius: 20))

            VStack(spacing: 16) {
                Button {
                    counter.increment()
                } label: {
                    Text("来場者+1")
                        .font(.title3.bold())
                        .frame(maxWidth: .infinity, minHeight: 40)
                }
                .buttonStyle(.borderedProminent)
                .accessibilityIdentifier("incrementButton")

                Button("リセット") {
                    counter.reset()
                }
                .buttonStyle(.bordered)
                .controlSize(.large)
                .accessibilityIdentifier("resetButton")
            }
        }
        .padding(28)
        .frame(maxWidth: 560)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        #if os(macOS)
        .frame(minWidth: 440, minHeight: 540)
        #endif
    }
}
