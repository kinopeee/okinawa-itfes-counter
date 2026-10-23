namespace OkinawaCounter;

public enum CapacityLevel { Normal, Warning, OverCapacity }

public sealed class CounterState
{
    public const int Capacity = 50;
    public long Visitors { get; private set; }
    public decimal Percentage => (decimal)Visitors * 100 / Capacity;
    public double Progress => (double)Math.Min(Percentage, 100);
    public CapacityLevel Level => Visitors > Capacity ? CapacityLevel.OverCapacity
        : Visitors >= 40 ? CapacityLevel.Warning : CapacityLevel.Normal;
    public string VisitorsText => $"{Visitors}人";
    public string PercentageText => $"{Percentage:0}%";

    public void Increment()
    {
        if (Visitors < long.MaxValue)
            Visitors++;
    }

    public void Reset() => Visitors = 0;
}
