import SwiftUI

struct CounterView: View {
  @State private var counter = CounterState()
  @Environment(\.accessibilityReduceMotion) private var reduceMotion

  private let navy = Color(red: 0.03, green: 0.16, blue: 0.32)
  private let teal = Color(red: 0.0, green: 0.43, blue: 0.48)

  private var statusColor: Color {
    switch counter.level {
    case .normal: .green
    case .warning: .orange
    case .overCapacity: .red
    }
  }

  var body: some View {
    GeometryReader { geometry in
      ScrollView {
        VStack(spacing: 28) {
          header
          card
          Text("沖縄のつながりを、ここから。")
            .font(.footnote)
            .foregroundStyle(.white.opacity(0.85))
        }
        .frame(maxWidth: 440)
        .padding(.horizontal, 24)
        .padding(.vertical, 32)
        .frame(maxWidth: .infinity)
        .frame(minHeight: geometry.size.height)
      }
      .scrollIndicators(.hidden)
    }
    .background {
      LinearGradient(
        colors: [
          navy, Color(red: 0.02, green: 0.36, blue: 0.49),
          Color(red: 0.02, green: 0.65, blue: 0.52),
        ],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
      )
      .ignoresSafeArea()
    }
  }

  private var header: some View {
    VStack(spacing: 12) {
      Image(systemName: "water.waves")
        .font(.system(size: 30, weight: .medium))
        .accessibilityHidden(true)
      Text("沖縄ビジネスITフェス")
        .font(.title3.weight(.bold))
      Text("受付カウンター")
        .font(.subheadline)
        .tracking(3)
        .foregroundStyle(.white.opacity(0.85))
    }
    .foregroundStyle(.white)
    .multilineTextAlignment(.center)
    .accessibilityElement(children: .combine)
  }

  private var card: some View {
    VStack(spacing: 24) {
      VStack(spacing: 8) {
        Text("現在の来場者数")
          .font(.subheadline.weight(.medium))
          .foregroundStyle(.secondary)
        Text("\(counter.visitors)人")
          .font(.system(size: 76, weight: .bold, design: .rounded))
          .monospacedDigit()
          .minimumScaleFactor(0.35)
          .lineLimit(1)
          .foregroundStyle(navy)
          .contentTransition(.numericText())
          .accessibilityIdentifier("visitorCount")
      }

      VStack(spacing: 12) {
        HStack(alignment: .firstTextBaseline, spacing: 8) {
          Text("定員50席")
            .font(.subheadline)
            .foregroundStyle(.secondary)
          Spacer(minLength: 4)
          Text("\(counter.percentage)%")
            .font(.title3.bold())
            .monospacedDigit()
            .contentTransition(.numericText())
            .accessibilityIdentifier("capacityPercentage")
          if counter.level == .overCapacity {
            Text("定員オーバー")
              .font(.caption.bold())
              .foregroundStyle(.red)
              .accessibilityIdentifier("overCapacity")
          }
        }
        .foregroundStyle(navy)
        .lineLimit(1)
        .minimumScaleFactor(0.65)

        GeometryReader { geometry in
          Capsule()
            .fill(Color.black.opacity(0.06))
            .overlay(alignment: .leading) {
              Capsule()
                .fill(statusColor)
                .frame(width: geometry.size.width * counter.progress)
            }
        }
        .frame(height: 12)
        .accessibilityElement()
        .accessibilityLabel("定員に対する割合")
        .accessibilityValue("\(counter.percentage)%")
      }

      VStack(spacing: 12) {
        Button {
          update { counter.increment() }
        } label: {
          Text("来場者+1")
            .font(.title3.bold())
            .frame(maxWidth: .infinity, minHeight: 58)
            .foregroundStyle(.white)
            .background(teal.gradient, in: RoundedRectangle(cornerRadius: 16))
        }
        .buttonStyle(.plain)
        .accessibilityIdentifier("incrementButton")

        Button {
          update { counter.reset() }
        } label: {
          Text("リセット")
            .font(.subheadline.weight(.semibold))
            .frame(maxWidth: .infinity, minHeight: 46)
            .foregroundStyle(teal)
            .overlay {
              RoundedRectangle(cornerRadius: 14)
                .strokeBorder(teal.opacity(0.3), lineWidth: 1)
            }
            .contentShape(RoundedRectangle(cornerRadius: 14))
        }
        .buttonStyle(.plain)
        .accessibilityIdentifier("resetButton")
      }
    }
    .padding(28)
    .background(.white, in: RoundedRectangle(cornerRadius: 28))
    .shadow(color: navy.opacity(0.22), radius: 24, x: 0, y: 14)
  }

  private func update(_ action: () -> Void) {
    withAnimation(reduceMotion ? nil : .easeInOut(duration: 0.22), action)
  }
}
