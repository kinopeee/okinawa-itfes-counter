using OkinawaCounter;

var state = new CounterState();
var notifications = 0;
state.PropertyChanged += (_, _) => notifications++;
Check(0, "0人", "0%", "#059669", false, 0);
for (var i = 0; i < 5; i++) state.Increment();
Check(5, "5人", "10%", "#059669", false, 5);
for (var i = 5; i < 39; i++) state.Increment();
Check(39, "39人", "78%", "#059669", false, 39);
state.Increment();
Check(40, "40人", "80%", "#D97706", false, 40);
for (var i = 40; i < 50; i++) state.Increment();
Check(50, "50人", "100%", "#D97706", false, 50);
state.Increment();
Check(51, "51人", "102%", "#DC2626", true, 50);
state.Reset();
Check(0, "0人", "0%", "#059669", false, 0);
if (notifications != 52) throw new Exception("Missing change notifications.");
Console.WriteLine("PASS: initial, five increments, 39/40/50/51 boundaries, progress cap, reset, notifications.");

void Check(int count, string countText, string percentage, string color, bool overCapacity, int progress)
{
    if (state.Count != count || state.CountText != countText || state.PercentageText != percentage ||
        state.StatusColor != color || state.IsOverCapacity != overCapacity || state.Progress != progress)
        throw new Exception($"Unexpected state at {count} visitors.");
}
