using OkinawaCounter;

var counter = new CounterState();
Check(0, "0人", "0%", 0, CapacityLevel.Normal);
for (var i = 0; i < 5; i++) counter.Increment();
Check(5, "5人", "10%", 10, CapacityLevel.Normal);
for (var i = 5; i < 39; i++) counter.Increment();
Check(39, "39人", "78%", 78, CapacityLevel.Normal);
counter.Increment();
Check(40, "40人", "80%", 80, CapacityLevel.Warning);
for (var i = 40; i < 50; i++) counter.Increment();
Check(50, "50人", "100%", 100, CapacityLevel.Warning);
counter.Increment();
Check(51, "51人", "102%", 100, CapacityLevel.OverCapacity);
counter.Reset();
Check(0, "0人", "0%", 0, CapacityLevel.Normal);
counter.Increment();
Check(1, "1人", "2%", 2, CapacityLevel.Normal);
Console.WriteLine("PASS: initial, five clicks, 39/40/50/51 boundaries, reset and increment after reset");

void Check(long visitors, string visitorsText, string percentage, double progress, CapacityLevel level)
{
    if (counter.Visitors != visitors || counter.VisitorsText != visitorsText ||
        counter.PercentageText != percentage || counter.Progress != progress || counter.Level != level)
        throw new InvalidOperationException($"Unexpected counter state at {visitors} visitors.");
}
