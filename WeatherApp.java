import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;




public class WeatherApp {
    //fetch weather data for given location
    public static JSONObject getWeatherData(String locationName){
        //get location coordinates using the geolocation API
        JSONArray locationData = getLocationData(locationName);

        //extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //build API request URL with loaction coordinaters
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FLos_Angeles";

        try{
            //call Api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check for response status
            //200 - means that the connection was a success
            if (conn.getResponseCode() != 200){
                System.out.println("Error:Could not connect to API");
                return null;
            }

            //store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner sc = new Scanner(conn.getInputStream());
            while(sc.hasNext()){
                //read and store into the string builder
                resultJson.append(sc.nextLine());
            }

            //close scanner
            sc.close();

            //close url connection
            conn.disconnect();

            //parse through data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            //we want to get the current hour's data
            //so we need to get the index of our currnt hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexofCurrentyTime(time);

            //get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            //get humidity
            JSONArray relativHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativHumidity.get(index);

            //get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            //build the weather json data object that we are going to access in our frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getLocationData(String locationName){
        //replace any whitespace in loaction name to + to adhere to API's request format
        locationName = locationName.replaceAll(" ","+");

        //build API url with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";
        try{
            //call api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //catch response status
            //200 means succesful connection
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                StringBuilder resultJson = new StringBuilder();
                Scanner sc = new Scanner(conn.getInputStream());


                while(sc.hasNext()){
                    resultJson.append(sc.nextLine());
                }

                //close Scanner
                sc.close();

                //close url connection
                conn.disconnect();

                // parse the JSON Strong into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                //get the list of location data the API generate from the loaction name
                JSONArray locationData = (JSONArray) resultJsonObj.get("results");
                return locationData;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //couldn't find location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //set request method to get
            conn.setRequestMethod("GET");

            //connect to API
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }

        //could not make connection
        return null;
    }
    private static int findIndexofCurrentyTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        //iterate through the time list and see which one matches current time
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)){
                //return the Index
                return 1;
            }
        }
        return 0;

    }

    private static String getCurrentTime(){
        //get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format date to be 2023-09- 02T00:00 (this is how it is readed in API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //format and orint the current date antd time
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            weatherCondition = "Clear";
        } else if (weathercode > 0L && weathercode <= 3L) {
            weatherCondition = "Cloudy";
        } else if ((weathercode >= 51L && weathercode >= 67L)
                    || (weathercode >= 80L && weathercode <= 99L)) {
            weatherCondition = "Rainy";
        } else if (weathercode >= 71L && weathercode <= 77L) {
            weatherCondition = "Snowy";
        }

        return  weatherCondition;
    }
}
