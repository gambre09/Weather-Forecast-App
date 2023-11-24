import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AppGui extends JFrame {
    private JSONObject weatherData;
    public AppGui(){
        //setup gui and add title
        super("Weather Forecast App");

        //configure gui to end the program's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //set size of gui (in pixels)
        setSize(450,650);

        //backgroud colour
        getContentPane().setBackground(new Color(125, 115, 255));

        //load gui at center of the screen
        setLocationRelativeTo(null);

        //make layout manager null to manually postion our component within the gui
        setLayout(null);

        //prevent any resize of our gui
        setResizable(false);

        addGuiComponents();
    }
    private void addGuiComponents(){
        //search field
        JTextField searchTextField = new JTextField();

        //set Location and size of component
        searchTextField.setBounds(15,15,351,45);

        //change font size and style
        searchTextField.setFont(new Font("San-serif",Font.ITALIC,24));
        add(searchTextField);

        //weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        //temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0,350,450,44);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //center the text
        temperatureText.setHorizontalAlignment((SwingConstants.CENTER));
        add(temperatureText);

        //weather condition description
        JLabel weatherConditionDis = new JLabel("Cloudy");
        weatherConditionDis.setBounds(0,405,450,36);
        weatherConditionDis.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDis.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDis);

        //humidity image
        JLabel humidityimage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityimage.setBounds(15,500,74,66);
        add(humidityimage);

        //Humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //windspeed image
        JLabel  windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220,500,74,66);
        add(windspeedImage);

        //windspeed text
        JLabel windspeedText = new JLabel("<html><b>Wind Speed</b> 15km</html>");
        windspeedText.setBounds(310,500,85,55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN,16));
        add(windspeedText);

        //search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        //change the cursor to a hand cursor when hovered over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get location fromm user
                String userInput = searchTextField.getText();

                //Validate input - remove white space to ensure non-empty text
                if (userInput.replaceAll("\\s","").length() <= 0){
                    return;
                }

                //retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                //update gui

                //update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                //depending on the condition, we will update the weather image that corresponds with th condition
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;

                }

                //update Temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                //update weather condition text
                weatherConditionDis.setText(weatherCondition);

                //update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                //update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");

            }
        });
        add(searchButton);
    }

    //used to create images in gui components
    private ImageIcon loadImage(String resourcePath) {
        try{
            //read image file the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            //read the image icon so that component can render it
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }
}
