// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018.components;

import edu.wpi.cscore.*;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.*;

/**
 * Sends information from the robot to the dashboard over NetworkTables.
 * 
 * @author Jacob
 */
public class Dashboard
{
	// Dashboard info update period, in Hz; should be a factor of 50 because the
	// update checks for a remainder of 0
	private final int UPDATE_RATE = 10;
	
	// Camera server network port; should be in range 1180-1190
	private final int CAMERA_PORT = 1181;
	
	// Table containing entries
	private NetworkTable table;
	
	// Constant match info entries
	private NetworkTableEntry eventEntry, matchTypeEntry, matchNumEntry;
	private NetworkTableEntry allianceEntry, stationEntry;
	
	// Ongoing match/robot info entries
	private NetworkTableEntry modeEntry, timeEntry;
	private NetworkTableEntry batteryEntry;
	private NetworkTableEntry platesEntry;
	
	// Camera and server
	private UsbCamera camera;
	private MjpegServer server;
	
	// Iteration timer
	private int timer;
	
	/**
	 * Constructor.
	 * 
	 * @param tableKey The key of the table containing the entries.
	 */
	public Dashboard(String tableKey)
	{
		// Create table
		this.table = NetworkTableInstance.getDefault().getTable("SaintsBotDS");
		
		// Constant match info entries
		this.eventEntry = this.table.getEntry("event");
		this.matchTypeEntry = this.table.getEntry("matchType");
		this.matchNumEntry = this.table.getEntry("matchNum");
		this.allianceEntry = this.table.getEntry("alliance");
		this.stationEntry = this.table.getEntry("station");
		
		// Ongoing match/robot info entries
		this.modeEntry = this.table.getEntry("mode");
		this.timeEntry = this.table.getEntry("time");
		this.batteryEntry = this.table.getEntry("battery");
		this.platesEntry = this.table.getEntry("platesEntry");
		
		// Set up and start streaming camera feed to server
		this.camera = new UsbCamera("cam0", 0);
		this.server = new MjpegServer("server0", CAMERA_PORT);
		this.server.setSource(this.camera);
		
		// Initialize timer
		this.timer = 0;
	}
	
	/**
	 * Increments the timer by 1. Should be called once per iteration.
	 */
	public void tick()
	{
		this.timer += 1;
	}
	
	/**
	 * Sends information to the dashboard about the current match and robot
	 * operation.
	 */
	public void updateMatch()
	{
		// Proceed only at each update period
		if (this.timer % (50 / UPDATE_RATE) == 0)
		{
			// Get DriverStation instance
			DriverStation ds = DriverStation.getInstance();
			
			// Update event name
			String event = ds.getEventName();
			this.eventEntry.setString(event);
			
			// Update match type entry
			String matchType;
			switch (ds.getMatchType())
			{
				case Qualification:
					matchType = "Quals";
					break;
				case Elimination:
					matchType = "Elims";
					break;
				case Practice:
					matchType = "Practice";
					break;
				default:
					matchType = "Match";
			}
			this.matchTypeEntry.setString(matchType);
			
			// Update match number entry
			int matchNum = ds.getMatchNumber();
			this.matchNumEntry.setNumber(matchNum);
			
			// Update alliance entry
			String alliance;
			switch (ds.getAlliance())
			{
				case Red:
					alliance = "Red";
					break;
				case Blue:
					alliance = "Blue";
					break;
				default:
					alliance = "Station";
			}
			this.allianceEntry.setString(alliance);
			
			// Update station entry
			int station = ds.getLocation();
			this.stationEntry.setNumber(station);
			
			// Update time entry
			double time = ds.getMatchTime();
			this.timeEntry.setDouble(time);
			
			// Update battery entry
			double battery = RobotController.getBatteryVoltage();
			this.batteryEntry.setDouble(battery);
			
			// Update plates entry
			String plates = ds.getGameSpecificMessage();
			this.platesEntry.setString(plates);
		}
	}
	
	/**
	 * Enumeration of possible match modes.
	 */
	public static enum Mode
	{
		Disabled, Teleop, Auto, Test
	}
	
	/**
	 * Updates the current operating mode on the dashboard. Should be called
	 * whenever the mode changes on the robot.
	 * 
	 * @param mode New operating mode
	 */
	public void updateMode(Mode mode)
	{
		switch (mode)
		{
			case Disabled:
				this.modeEntry.setString("Disabled");
				break;
			case Teleop:
				this.modeEntry.setString("Teleop");
				break;
			case Auto:
				this.modeEntry.setString("Auto");
				break;
			case Test:
				this.modeEntry.setString("Test");
				break;
		}
	}
}