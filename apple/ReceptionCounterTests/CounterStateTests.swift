import XCTest

final class CounterStateTests: XCTestCase {
    func testFiveVisitorsAndReset() {
        var counter = CounterState()
        XCTAssertEqual(counter.visitors, 0)
        XCTAssertEqual(counter.percentage, 0)
        for _ in 0..<5 { counter.increment() }
        XCTAssertEqual(counter.visitors, 5)
        XCTAssertEqual(counter.percentage, 10)
        XCTAssertEqual(counter.progress, 0.1, accuracy: 0.0001)
        XCTAssertEqual(counter.occupancy, .available)
        counter.reset()
        XCTAssertEqual(counter.visitors, 0)
        XCTAssertEqual(counter.percentage, 0)
        XCTAssertEqual(counter.progress, 0)
    }

    func testCapacityBoundaries() {
        var counter = CounterState()
        for _ in 0..<39 { counter.increment() }
        XCTAssertEqual(counter.occupancy, .available)
        counter.increment()
        XCTAssertEqual(counter.percentage, 80)
        XCTAssertEqual(counter.occupancy, .nearlyFull)
        for _ in 0..<10 { counter.increment() }
        XCTAssertEqual(counter.percentage, 100)
        XCTAssertEqual(counter.occupancy, .nearlyFull)
        counter.increment()
        XCTAssertEqual(counter.visitors, 51)
        XCTAssertEqual(counter.percentage, 102)
        XCTAssertEqual(counter.progress, 1)
        XCTAssertEqual(counter.occupancy, .overCapacity)
        counter.reset()
        XCTAssertEqual(counter.occupancy, .available)
        XCTAssertEqual(counter.progress, 0)
    }
}
