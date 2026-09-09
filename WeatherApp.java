package com.weatherApplication; //creates package for weather app GUI

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONObject;

public class WeatherApp extends JFrame { //creates class for weather app and extends JFrame to creates window for user to interact
	private JSONObject weatherData; //creates private variable for retrieving weather data using JSONObject class

	public WeatherApp() { //constructor for weather app
		super("Weather App"); //title for app
		setDefaultCloseOperation(EXIT_ON_CLOSE); //closes weather app when x is clicked
		setSize(500, 700); //sets dimensions of window
		setLocationRelativeTo(null); //sets window to open to center of screen
		setLayout(null); // sets layout of window
		setResizable(true); //allows window to be resizable
		Color color = new Color(200, 150, 125); //creates new color for background of app
		getContentPane().setBackground(color); //sets the color variable to background of window
		addGuiComponents(); //method for adding GUI components
		}
	
	private void addGuiComponents() { //method for adding components to GUI
		JTextField searchTF = new JTextField("Enter a city"); //search field for user to enter a city they want to check the weather
		searchTF.setBounds(15, 15, 350, 45); //dimensions of textfield box
		searchTF.setFont(new Font("SansSerif", Font.PLAIN, 25)); //font style of characters typed in search textfield
		searchTF.setBackground(Color.yellow);
		add(searchTF); //adds search textfield to GUI
		
		JLabel weatherIcon = new JLabel(loadImage("default_weather_homepage.png")); //label for displaying weather icon matched to weather condition, initially set to default weather icon until user searches for a city
		weatherIcon.setBounds(0, 125, 450, 217); //dimensions of icon on app window
		add(weatherIcon); //adds weather Icon label to GUI
		
		JLabel temperatureT = new JLabel("Temperature C/F"); //label for displaying temperature at celsius and fahrenheit
		temperatureT.setBounds(0, 350, 450, 54); //dimensions for label
		temperatureT.setFont(new Font("SansSerif", Font.BOLD, 48)); //font style for label
		temperatureT.setHorizontalAlignment(SwingConstants.CENTER); //aligns label to the center
		add(temperatureT); //adds label for temperature display
		
		JLabel weatherC = new JLabel("Weather Condition"); //label for giving weather condition of searched city
		weatherC.setBounds(0, 405, 450, 36); //dimensions for weather condition label
		weatherC.setFont(new Font("SansSerif", Font.PLAIN, 32)); //font style for weather condition label
		weatherC.setHorizontalAlignment(SwingConstants.CENTER); //aligns label to center
		add(weatherC); //adds weather condition label to GUI
		
		JLabel humidityIcon = new JLabel(loadImage("humidity_icon.png")); //label for humidity of searched city
		humidityIcon.setBounds(15, 500, 74, 66); //dimensions for displaying humidity icon
		add(humidityIcon); //adds humidity icon label to GUI
		
		JLabel humidityT = new JLabel("<html><b>Humidity</b> ---%</html>"); //label for percentage of humidity
		humidityT.setBounds(90, 500, 85, 55); //dimensions for displaying humidity label
		humidityT.setFont(new Font("SansSerif", Font.PLAIN, 16)); //font style of humidity percentage label
		add(humidityT); //adds humidity percentage label to GUI
		
		JLabel windSpeedIcon = new JLabel(loadImage("wind_icon.png")); //label for windspeed icon 
		windSpeedIcon.setBounds(220, 500, 74, 66); //dimensions for windspeed icon
		add(windSpeedIcon); //adds label for windspeed icon to GUI
		
		JLabel windSpeedT = new JLabel("<html><b>Windspeed</b> ---km/h (mph)</html>"); //label for displaying windspeed in km/h and mph
		windSpeedT.setBounds(310, 475, 100, 100); //dimensions for displaying windspeed information
		windSpeedT.setFont(new Font("SansSerif", Font.PLAIN, 16)); //font style for windspeed information
		add(windSpeedT); //adds windspeed label to GUI
		
		JButton searchB = new JButton(loadImage("search_button_icon.png")); //creates button to click for searching for weather once user inputs a city in search textfield
		
		searchB.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); //changes cursor to hand cursor to indicate to user to click once cursor is hovering over button
		searchB.setBounds(375, 13, 47, 45); //dimensions for button
		searchB.addActionListener(new ActionListener() { //creates an actionlistener to listen for when button is clicked
			public void actionPerformed(ActionEvent e) { //method for what to do when button is clicked
				String userInput = searchTF.getText(); //gets text that was inputed by user in the search textfield and assigns it to new variable
				if(userInput.replaceAll("\\s", "").length() <= 0) { //if variable from user input is empty than it will return and wait for valid user entry
					return;
				}
				weatherData = ObtainWeatherInfo.getWeatherData(userInput); //uses getWeatherData method from ObtainWeatherInfo class to grab weather data for city user searched
				String weatherCondition = (String) weatherData.get("weather_condition"); //casts weather condition to string and stores data in new variable
				
				switch(weatherCondition) { //checks the content of variable weatherCondition to indicate what steps to take
					case "Clear": //if clear weather than sun icon will be used
						weatherIcon.setIcon(loadImage("sun_icon.png"));
						break;
					case "Rain": //if rainy weather than rain icon will be used
						weatherIcon.setIcon(loadImage("rain_icon.png"));
						break;
					case "Cloudy": //if cloudy weather than cloud icon will be used
						weatherIcon.setIcon(loadImage("cloud_icon.png"));
						break;
					case "Snow": //if snowy weather than snow icon will be used.
						weatherIcon.setIcon(loadImage("snow_icon.png"));
						break;
				}
				double temperature = (double) weatherData.get("temperature"); //obtains temperature data (in celsius) and sets it to double data type
				double tempFar = ((temperature * 1.8) + 32); //creates new variable for temperature in fahrenheit using temperature given in celsius and uses formula to convert
				temperatureT.setText(temperature + " C / " + tempFar + " F"); //sets text for temperature label to the celsius temperature obtained from weatherData and fahrenheit temperature calculated in tempFar variable
				weatherC.setText(weatherCondition); //sets weather condition to description assigned from weatherData
				long humidity = (long) weatherData.get("humidity"); //obtains humidity data from weatherDaata and sets to long 
				humidityT.setText("<html><b>Humidity</b> " + humidity + "%</html>"); //sets humidity text to the percentage obtained from weatherData
				double windSpeed = (double) weatherData.get("windspeed"); //obtains windspeed information from weatherData in km/h
				float windSpeedMPH = (float) (windSpeed * 0.62); //multiplies windspeed data by 0.62 to convert to miles for mph information to be displayed
				windSpeedT.setText("<html><b>Windspeed</b> " + windSpeed + "km/h<br>" + windSpeedMPH + "mph</html>"); //displays windspeed information in km/h and mph
			}
		});
		add(searchB); //adds search button to GUI
		
	}
	
	private ImageIcon loadImage(String resourcePath) { //method for loading image icon to be displayed when weather condition changes depending on search results
		try { //try statement to read file for image trying to be loaded
			Image image = ImageIO.read(new File(resourcePath)); //uses Image class to read file containing image and directs path to find file
			return new ImageIcon(image); //if file found method will return image of icon
		} catch(IOException e) { //catches IOException for any errors that occur when searching for file
			e.printStackTrace();
		}
		System.out.println("Could not find file"); //prints error message if file could not be found
		return null; //returns nothing
	}

}
