package com.weatherApplication;

import javax.swing.SwingUtilities;

public class Launcher { //creates class to launch weather app from 
	public static void main(String[] args) { //main method
		SwingUtilities.invokeLater(new Runnable() { //safely runs methods from outside swing classes on same AWT thread
			public void run() { //method to run functions inside this method
				new WeatherApp().setVisible(true); //displays window for WeatherApp
			}
		});
	}

}