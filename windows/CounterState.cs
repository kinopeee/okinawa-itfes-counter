using System.ComponentModel;
using System.Globalization;

namespace OkinawaCounter;

public sealed class CounterState : INotifyPropertyChanged
{
    public const int Capacity = 50;
    public int Count { get; private set; }
    public string CountText => $"{Count.ToString(CultureInfo.InvariantCulture)}人";
    public long Percentage => (long)Count * 100 / Capacity;
    public string PercentageText => $"{Percentage.ToString(CultureInfo.InvariantCulture)}%";
    public int Progress => Math.Min(Count, Capacity);
    public bool IsOverCapacity => Count > Capacity;
    public string StatusColor => IsOverCapacity ? "#DC2626" : Count >= 40 ? "#D97706" : "#059669";
    public bool CanIncrement => Count < int.MaxValue;

    public event PropertyChangedEventHandler? PropertyChanged;

    public void Increment()
    {
        if (!CanIncrement)
            return;

        Count++;
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(null));
    }

    public void Reset()
    {
        Count = 0;
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(null));
    }
}
