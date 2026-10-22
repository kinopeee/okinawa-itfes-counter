using System.Windows;

namespace ReceptionCounter;

public partial class MainWindow : Window
{
    private const int Capacity = 50;
    private long visitorCount;

    public MainWindow()
    {
        InitializeComponent();
        UpdateDisplay();
    }

    private void AddVisitor_Click(object sender, RoutedEventArgs e)
    {
        visitorCount++;
        UpdateDisplay();
    }

    private void Reset_Click(object sender, RoutedEventArgs e)
    {
        visitorCount = 0;
        UpdateDisplay();
    }

    private void UpdateDisplay()
    {
        CountDisplay.Text = $"{visitorCount}人・{visitorCount * 100m / Capacity:0}%";
        CapacityWarning.Visibility = visitorCount > Capacity ? Visibility.Visible : Visibility.Collapsed;
    }
}
