import XCTest
@testable import ReceptionCounter

final class VisitorCounterTests: XCTestCase {
    func testFiveVisitorsRepresentTenPercent() {
        var counter = VisitorCounter()
        XCTAssertEqual(counter.count, 0)
        XCTAssertEqual(counter.percentage, 0)
        for _ in 0..<5 { counter.increment() }
        XCTAssertEqual(counter.count, 5)
        XCTAssertEqual(counter.percentage, 10)
        XCTAssertFalse(counter.isOverCapacity)
    }

    func testCapacityBoundaryAndReset() {
        var counter = VisitorCounter()
        for _ in 0..<50 { counter.increment() }
        XCTAssertEqual(counter.percentage, 100)
        XCTAssertFalse(counter.isOverCapacity)
        counter.increment()
        XCTAssertEqual(counter.count, 51)
        XCTAssertEqual(counter.percentage, 102)
        XCTAssertTrue(counter.isOverCapacity)
        counter.reset()
        XCTAssertEqual(counter.count, 0)
        XCTAssertEqual(counter.percentage, 0)
        XCTAssertFalse(counter.isOverCapacity)
    }
}
