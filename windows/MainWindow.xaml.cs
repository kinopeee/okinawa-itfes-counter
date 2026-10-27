using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Animation;

namespace OkinawaCounter;

public partial class MainWindow : Window
{
    private readonly CounterState state = new();

    public MainWindow()
    {
        InitializeComponent();
        DataContext = state;
    }

    private void Increment_Click(object sender, RoutedEventArgs e)
    {
        state.Increment();
        AnimateCount();
    }

    private void Reset_Click(object sender, RoutedEventArgs e)
    {
        state.Reset();
        AnimateCount();
    }

    private void AnimateCount()
    {
        if (!SystemParameters.ClientAreaAnimation)
            return;

        var animation = new DoubleAnimation(0.94, 1, TimeSpan.FromMilliseconds(180))
        {
            EasingFunction = new QuadraticEase { EasingMode = EasingMode.EaseOut }
        };
        CountScale.BeginAnimation(ScaleTransform.ScaleXProperty, animation);
        CountScale.BeginAnimation(ScaleTransform.ScaleYProperty, animation);
    }
}
