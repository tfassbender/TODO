package net.jfabricationgames.todo.frame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.jfabricationgames.todo.frame.util.DialogUtils;

public class TodoFramePropertiesStore {
	
	private static final String PROPERTIES_FILE = "TODOs/.properties/.todo.properties";
	private static final String PROPERTY_FILES_KEY = "FILES";
	private static final String FILES_DELEMITER_REGEX = "\\|";
	private static final String FILES_DELEMITER = "|";
	private static final String PROPERTY_SELECTED_TAB_INDEX = "SELECTED_TAB_INDEX";
	private static final String PROPERTY_WORD_WRAP = "WORD_WRAP_ENABLED";
	
	private static final String PROPERTY_WINDOW_X = "WINDOW_POS_X";
	private static final String PROPERTY_WINDOW_Y = "WINDOW_POS_Y";
	private static final String PROPERTY_WINDOW_WIDTH = "WINDOW_POS_WIDTH";
	private static final String PROPERTY_WINDOW_HEIGHT = "WINDOW_POS_HEIGHT";
	private static final String PROPERTY_WINDOW_MAXIMIZED = "WINDOW_POS_MAXIMIZED";
	
	private Properties properties;
	
	public TodoFramePropertiesStore() {
		loadProperties();
	}
	
	//***********************************************************************************
	//*** public
	//***********************************************************************************
	
	public List<File> getTodoFiles() {
		List<File> todoFiles = new ArrayList<File>();
		
		String[] files = properties.getProperty(PROPERTY_FILES_KEY, "").toString().split(FILES_DELEMITER_REGEX);
		for (String file : files) {
			if (!file.trim().isEmpty()) {
				todoFiles.add(new File(file));
			}
		}
		
		return todoFiles;
	}
	
	public void setFiles(List<File> files) {
		String fileList = files.stream().map(file -> file.getAbsolutePath()).collect(Collectors.joining(FILES_DELEMITER));
		properties.put(PROPERTY_FILES_KEY, fileList);
	}
	
	/**
	 * Adjust the windows position to the values in the properties.
	 */
	public void adjustWindowPosition(Window window) {
		double x = Double.parseDouble(properties.getProperty(PROPERTY_WINDOW_X, "200"));
		double y = Double.parseDouble(properties.getProperty(PROPERTY_WINDOW_Y, "200"));
		double width = Double.parseDouble(properties.getProperty(PROPERTY_WINDOW_WIDTH, "1200"));
		double height = Double.parseDouble(properties.getProperty(PROPERTY_WINDOW_HEIGHT, "800"));
		boolean maximized = Boolean.parseBoolean(properties.getProperty(PROPERTY_WINDOW_MAXIMIZED, "false"));
		
		window.setX(x);
		window.setY(y);
		if (!maximized) {
			window.setWidth(width);
			window.setHeight(height);
		}
		Platform.runLater(() -> ((Stage) window).setMaximized(maximized));
	}
	
	/**
	 * Set the properties value to the current window position
	 */
	public void setWindowPosition(Window window) {
		double x = window.getX();
		double y = window.getY();
		double width = window.getWidth();
		double height = window.getHeight();
		boolean maximized = ((Stage) window).isMaximized();
		
		properties.setProperty(PROPERTY_WINDOW_X, Double.toString(x));
		properties.setProperty(PROPERTY_WINDOW_Y, Double.toString(y));
		if (!maximized) {
			properties.setProperty(PROPERTY_WINDOW_WIDTH, Double.toString(width));
			properties.setProperty(PROPERTY_WINDOW_HEIGHT, Double.toString(height));
		}
		properties.setProperty(PROPERTY_WINDOW_MAXIMIZED, Boolean.toString(maximized));
	}
	
	public int getSelectedTab() {
		return Integer.parseInt(properties.getProperty(PROPERTY_SELECTED_TAB_INDEX, "0"));
	}
	
	public void setSelectedTab(int selectedTabIndex) {
		properties.setProperty(PROPERTY_SELECTED_TAB_INDEX, Integer.toString(selectedTabIndex));
	}
	
	public boolean getWordWrap() {
		return Boolean.parseBoolean(properties.getProperty(PROPERTY_WORD_WRAP, "false"));
	}
	
	public void setWordWrap(boolean selected) {
		properties.setProperty(PROPERTY_WORD_WRAP, Boolean.toString(selected));
	}
	
	public void store() {
		try (OutputStream out = new FileOutputStream(new File(PROPERTIES_FILE))) {
			properties.store(out, "");
		}
		catch (IOException ioe) {
			DialogUtils.showErrorDialog("Saving failed", "Saving of properties failed", ioe.getMessage(), true);
		}
	}
	
	//***********************************************************************************
	//*** private
	//***********************************************************************************
	
	private void loadProperties() {
		properties = new Properties();
		
		File propertiesFile = new File(PROPERTIES_FILE);
		if (propertiesFile.exists()) {
			//load properties
			try (InputStream inStream = new FileInputStream(propertiesFile)) {
				properties.load(inStream);
			}
			catch (IOException ioe) {
				DialogUtils.showErrorDialog("Loading failed", "Loading of properties failed", ioe.getMessage(), false);
			}
		}
		else {
			//create a properties file
			propertiesFile.getParentFile().mkdirs();
			try {
				propertiesFile.createNewFile();
			}
			catch (IOException ioe) {
				DialogUtils.showErrorDialog("Properties error", "Couldn't create properties file", ioe.getMessage(), false);
			}
		}
	}
}
