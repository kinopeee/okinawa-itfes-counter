struct VisitorCounter {
    static let capacity = 50
    private(set) var count = 0

    var percentage: Int { count * 100 / Self.capacity }
    var isOverCapacity: Bool { count > Self.capacity }

    mutating func increment() {
        count += 1
    }

    mutating func reset() {
        count = 0
    }
}
