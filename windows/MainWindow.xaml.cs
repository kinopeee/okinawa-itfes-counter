using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Animation;

namespace OkinawaCounter;

public partial class MainWindow : Window
{
    private readonly CounterState counter = new();
    private static readonly Brush Green = new SolidColorBrush(Color.FromRgb(22, 128, 74));
    private static readonly Brush Orange = new SolidColorBrush(Color.FromRgb(190, 92, 0));
    private static readonly Brush Red = new SolidColorBrush(Color.FromRgb(197, 45, 56));

    public MainWindow()
    {
        InitializeComponent();
        UpdateDisplay();
    }

    private void Increment_Click(object sender, RoutedEventArgs e)
    {
        counter.Increment();
        UpdateDisplay(animate: true);
    }

    private void Reset_Click(object sender, RoutedEventArgs e)
    {
        counter.Reset();
        UpdateDisplay(animate: true);
    }

    private void UpdateDisplay(bool animate = false)
    {
        VisitorCount.Text = counter.VisitorsText;
        Percentage.Text = counter.PercentageText;
        CapacityProgress.Value = counter.Progress;
        var color = counter.Level switch
        {
            CapacityLevel.Warning => Orange,
            CapacityLevel.OverCapacity => Red,
            _ => Green
        };
        Percentage.Foreground = CapacityProgress.Foreground = color;
        OverCapacity.Visibility = counter.Level == CapacityLevel.OverCapacity
            ? Visibility.Visible : Visibility.Collapsed;

        if (animate && SystemParameters.ClientAreaAnimation)
        {
            var pulse = new DoubleAnimation(0.94, 1, TimeSpan.FromMilliseconds(180))
            {
                EasingFunction = new CubicEase { EasingMode = EasingMode.EaseOut }
            };
            CountScale.BeginAnimation(ScaleTransform.ScaleXProperty, pulse);
            CountScale.BeginAnimation(ScaleTransform.ScaleYProperty, pulse);
        }
    }
}
