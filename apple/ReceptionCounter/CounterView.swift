import SwiftUI

struct CounterView: View {
    @State private var counter = CounterState()
    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    private let navy = Color(red: 0.025, green: 0.15, blue: 0.30)
    private let teal = Color(red: 0.0, green: 0.43, blue: 0.44)

    private var occupancyColor: Color {
        switch counter.occupancy {
        case .available: .green
        case .nearlyFull: .orange
        case .overCapacity: .red
        }
    }

    var body: some View {
        GeometryReader { geometry in
            ScrollView {
                VStack(spacing: 30) {
                    VStack(spacing: 12) {
                        Image(systemName: "water.waves")
                            .font(.system(size: 34, weight: .medium))
                            .accessibilityHidden(true)
                        Text("沖縄ビジネスITフェス\n受付カウンター")
                            .font(.title2.bold())
                            .multilineTextAlignment(.center)
                            .fixedSize(horizontal: false, vertical: true)
                    }
                    .foregroundStyle(.white)

                    counterCard

                    Text("ようこそ、沖縄のビジネスとITの出会いへ。")
                        .font(.footnote)
                        .multilineTextAlignment(.center)
                        .foregroundStyle(.white.opacity(0.9))
                }
                .frame(maxWidth: 480)
                .padding(.horizontal, 24)
                .padding(.vertical, 36)
                .frame(maxWidth: .infinity)
                .frame(minHeight: geometry.size.height)
            }
            .scrollIndicators(.hidden)
        }
        .background {
            LinearGradient(
                colors: [navy, Color(red: 0.02, green: 0.37, blue: 0.53),
                         Color(red: 0.02, green: 0.68, blue: 0.53)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()
        }
    }

    private var counterCard: some View {
        VStack(spacing: 24) {
            VStack(spacing: 6) {
                Text("現在の来場者数")
                    .font(.headline)
                    .foregroundStyle(.secondary)
                Text("\(counter.visitors)人")
                    .font(.system(size: 76, weight: .bold, design: .rounded))
                    .monospacedDigit()
                    .minimumScaleFactor(0.4)
                    .lineLimit(1)
                    .foregroundStyle(navy)
                    .contentTransition(.numericText(value: Double(counter.visitors)))
                    .accessibilityIdentifier("visitorCount")
            }

            VStack(spacing: 12) {
                Text("定員50席")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                HStack(spacing: 10) {
                    Text("\(counter.percentage)%")
                        .font(.title2.bold())
                        .monospacedDigit()
                        .foregroundStyle(navy)
                        .contentTransition(.numericText(value: Double(counter.percentage)))
                        .accessibilityIdentifier("occupancyPercentage")
                    if counter.occupancy == .overCapacity {
                        Text("定員オーバー")
                            .font(.subheadline.bold())
                            .foregroundStyle(.red)
                            .accessibilityIdentifier("overCapacityWarning")
                    }
                }
                .minimumScaleFactor(0.7)
                .lineLimit(1)

                GeometryReader { geometry in
                    ZStack(alignment: .leading) {
                        Capsule().fill(Color.black.opacity(0.07))
                        Capsule()
                            .fill(occupancyColor)
                            .frame(width: geometry.size.width * counter.progress)
                    }
                }
                .frame(height: 12)
                .accessibilityElement()
                .accessibilityLabel("定員50席に対する割合")
                .accessibilityValue("\(counter.percentage)%")
            }

            VStack(spacing: 12) {
                Button(action: { update { counter.increment() } }) {
                    Label("来場者+1", systemImage: "plus")
                        .font(.title3.bold())
                        .frame(maxWidth: .infinity, minHeight: 58)
                        .foregroundStyle(.white)
                        .background(teal, in: RoundedRectangle(cornerRadius: 16))
                        .contentShape(RoundedRectangle(cornerRadius: 16))
                }
                .buttonStyle(.plain)
                .accessibilityIdentifier("incrementButton")

                Button(action: { update { counter.reset() } }) {
                    Text("リセット")
                        .font(.body.weight(.medium))
                        .frame(maxWidth: .infinity, minHeight: 46)
                        .foregroundStyle(teal)
                        .overlay {
                            RoundedRectangle(cornerRadius: 14)
                                .strokeBorder(teal.opacity(0.4), lineWidth: 1)
                        }
                        .contentShape(RoundedRectangle(cornerRadius: 14))
                }
                .buttonStyle(.plain)
                .accessibilityIdentifier("resetButton")
            }
        }
        .padding(28)
        .background(.white, in: RoundedRectangle(cornerRadius: 28))
        .shadow(color: navy.opacity(0.25), radius: 24, x: 0, y: 12)
    }

    private func update(_ action: () -> Void) {
        withAnimation(reduceMotion ? nil : .easeInOut(duration: 0.22), action)
    }
}
