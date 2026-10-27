@main
struct CounterStateTests {
  static func main() {
    var counter = CounterState()
    precondition(counter.visitors == 0 && counter.percentage == 0 && counter.progress == 0)
    precondition(counter.level == .normal)
    for _ in 0..<5 { counter.increment() }
    precondition(counter.visitors == 5 && counter.percentage == 10 && counter.progress == 0.1)
    for _ in 5..<39 { counter.increment() }
    precondition(counter.level == .normal)
    counter.increment()
    precondition(counter.visitors == 40 && counter.percentage == 80 && counter.level == .warning)
    for _ in 40..<50 { counter.increment() }
    precondition(counter.percentage == 100 && counter.level == .warning)
    counter.increment()
    precondition(counter.visitors == 51 && counter.percentage == 102)
    precondition(counter.level == .overCapacity && counter.progress == 1)
    counter.reset()
    precondition(counter.visitors == 0 && counter.percentage == 0 && counter.progress == 0)
    precondition(counter.level == .normal)
    print("PASS: 0, 5, 39, 40, 50, 51 visitors and reset")
  }
}
