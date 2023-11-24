import javax.swing.*;

public class WeatherAppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //display weather app gui
                new AppGui().setVisible(true);
//                System.out.println(WeatherAPP.getLocationData("India"));
//                System.out.println(WeatherAPP.getCurrentTime());
            }
        });
    }
}
