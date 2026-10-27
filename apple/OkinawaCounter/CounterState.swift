struct CounterState {
  enum Level {
    case normal, warning, overCapacity
  }

  static let capacity = 50
  private(set) var visitors = 0

  var percentage: Int { visitors * 100 / Self.capacity }
  var progress: Double { min(Double(visitors) / Double(Self.capacity), 1) }
  var level: Level {
    if visitors > Self.capacity { return .overCapacity }
    return visitors >= 40 ? .warning : .normal
  }

  mutating func increment() { visitors += 1 }
  mutating func reset() { visitors = 0 }
}
