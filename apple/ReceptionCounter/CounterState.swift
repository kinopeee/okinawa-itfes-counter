struct CounterState {
    static let capacity = 50

    enum Occupancy {
        case available
        case nearlyFull
        case overCapacity
    }

    private(set) var visitors = 0

    var percentage: Int { visitors * 100 / Self.capacity }
    var progress: Double { min(Double(visitors) / Double(Self.capacity), 1) }
    var occupancy: Occupancy {
        if visitors > Self.capacity { return .overCapacity }
        return visitors >= 40 ? .nearlyFull : .available
    }

    mutating func increment() {
        visitors += 1
    }

    mutating func reset() {
        visitors = 0
    }
}
