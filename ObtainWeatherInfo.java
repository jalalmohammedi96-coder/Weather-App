package com.weatherApplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ObtainWeatherInfo { //creats class to obtain weather information from API open-meteo
	public static JSONObject getWeatherData(String locationName) { //uses JSONObject class to create object for getting weather data for location searched by user
		JSONArray locationData = getLocationData(locationName); //stores location data for city searched
		JSONObject location = (JSONObject) locationData.get(0); //gets location data from first index and maps it to location variable
		double latitude = (double) location.get("latitude"); //sets latitude data to value type double and assigns it to latitude variable
		double longitude = (double) location.get("longitude"); //sets longitude data to value type double and assigns it to longitude variable
		//stores url into urlString and maps the value of latitude and longitude in the url to the variables created above for latitude and longitude
		String urlString = "https://api.open-meteo.com/v1/forecast?" + 
		"latitude=" + latitude + "&longitude=" + longitude +
		"&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FLos_Angeles";
		
		try { //try statement for connecting with HTTP server for urlString by sending a request to socket
			HttpURLConnection conn = fetchApiResponse(urlString); //assigns response to conn
			if(conn.getResponseCode() != 200) { //if response code is not equal to 200 than it could not connect to API
				System.out.println("Error: Could not connect to API"); //displays error message
				return null; //Returns null
			}
			
			StringBuilder resultJson = new StringBuilder(); //if connected to API, creates new variable to store information in string
			Scanner sc = new Scanner(conn.getInputStream()); //gets data from url stored in conn and assigns it to variable sc
			while(sc.hasNext()) { //while there is another token in the sc variable, loop will remain open
				resultJson.append(sc.nextLine()); //connects characters from sc to resultJson StringBuilder object
			}
			sc.close(); //closes Scanner object
			conn.disconnect(); //disconnects from server connection
			
			JSONParser parser = new JSONParser(); //creates new object for parsing data using JSONParser class
			JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson)); //parses data from resultJson data to string form and assigns to variable created using JSONObject class
			JSONObject hourly = (JSONObject) resultJsonObj.get("hourly"); //gets hourly data and assigns to variable created using JSONObject
			JSONArray time = (JSONArray) hourly.get("time"); //gets time of when weather forecast information was obtained
			int index = findIndexOfCurrentTime(time); //assigns current time to index variable
			JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m"); //gets temperature data from urlString section
			double temperature = (double) temperatureData.get(index); //assigns temperature data to variable of double type
			JSONArray weatherCode = (JSONArray) hourly.get("weather_code"); //obtains weather code information from urlString section
			String weatherCondition = convertWeatherCode((long) weatherCode.get(index)); //assigns weather code to weatherCondition variable of string type
			JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m"); //obtains humidity information form urlString section
			long humidity = (long) relativeHumidity.get(index); //assigns humidity data to variable of long type
			JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m"); //obtains windspeed information from urlString section
			double windSpeed = (double) windSpeedData.get(index); //assigns windspeed data to variable of double type
			
			JSONObject weatherData = new JSONObject(); //creates object for weather data to map keys to values using type HashMap
			weatherData.put("temperature", temperature);
			weatherData.put("weather_condition", weatherCondition);
			weatherData.put("humidity", humidity);
			weatherData.put("windspeed", windSpeed);
			
			return weatherData; //returns weather data of location searched
			
		} catch(Exception e) { //catches exception for errors encountered
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray getLocationData(String locationName) { //method for getting location data to assign latitude and longitude for grabbing weather data from API
		locationName =locationName.replaceAll(" ", "+"); //replaces spaces with + for locationName
		String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
		locationName + "&count=10&language=en&format=json";
		
		try { //try statement for connecting to HTTP server by requesting access from socket
			HttpURLConnection conn = fetchApiResponse(urlString);
			if(conn.getResponseCode() != 200) { //if not successful connection then error message will display
				System.out.println("Error: Could not connect to API");
				return null;
			} else { //if connection successful than results will be parsed into readable JavaScript data using StringBuilder class
				StringBuilder resultJson = new StringBuilder();
				Scanner sc = new Scanner(conn.getInputStream());
				
				while(sc.hasNext()) { //while loop for when there is a character in the next check
					resultJson.append(sc.nextLine());
				}
				sc.close(); //closes scanner
				conn.disconnect(); //disconnects from server
				
				JSONParser parser = new JSONParser(); //parser for converting data into readable JavaScript code
				JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson)); //assigns data to string value type
				JSONArray locationData = (JSONArray) resultJsonObj.get("results"); //assigns data to locationData variable
				return locationData; //returns value stored in locationData
			}
		} catch(Exception e) { //catches exception for errors encountered
			e.printStackTrace();
		}
		return null;

	}
	
	private static HttpURLConnection fetchApiResponse(String urlString) { //method for connecting with API
		try{
			URL url = new URL(urlString); //assigns urlString to URL object url
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //builds connection between server and URL object url
			
			conn.setRequestMethod("GET"); //sets method to GET for the URL request
			conn.connect(); //opens a communication link
			return conn; //return connection link
		} catch(IOException e) { //catches IOException for any errors encountered
			e.printStackTrace();
		}
		return null;
	}

	private static int findIndexOfCurrentTime(JSONArray timeList) { //method for finding index of token in current time
		String currentTime = getCurrentTime(); //assigns current time to currentTime variable and sets to string type
		for(int i = 0; i < timeList.size(); i++) { //for loop for iterating through timeList
			String time = (String) timeList.get(i); //sets each character in tiemList to string type in time variable 
			if(time.equalsIgnoreCase(currentTime)) { //if time equals current time then returns character at that index
				return i;
			}
		}
		return 0;
	}
	
	public static String getCurrentTime() { //method for getting current time and formatting it to the same format as API format for time
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
		String formattedDateTime = currentDateTime.format(formatter);
		return formattedDateTime;
	}
	
	private static String convertWeatherCode(long weatherCode) { //method for converting weather code to weather condition that will be displayed in weather app
		String weatherCondition = ""; //sets weatherCoondition to an empty string
		if(weatherCode == 0L) { //if weather code is equal to zero then it is a clear day and the description "Clear" will be displayed
			weatherCondition = "Clear";
		} else if (weatherCode > 0L && weatherCode <= 3L) { //if weather code is in between zero and 4 then it is a cloudy day and the description "Cloudy" will be displayed
			weatherCondition = "Cloudy";
		} else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)){ //if weather code is in between 50 and 68 then it is a rainy day and the description "Rain" will be displayed
			weatherCondition = "Rain";
		} else if (weatherCode >= 71L && weatherCode <= 77L) { //if weather code is in between 70 and 78 then it is a snowy day and the description "Snow" will be displayed
			weatherCondition = "Snow";
		}
		return weatherCondition; //returns value stored in weatherCondition
	}
}
