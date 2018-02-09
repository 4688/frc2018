// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018;

import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot
{
	private final int JOYSTICK_USB = 0;
	
	private Dashboard dashboard;
	private MattDupuis matt;
	
	public void robotInit()
	{
		this.dashboard = new Dashboard("SaintsBotDS");
		this.matt = new MattDupuis(JOYSTICK_USB);
	}
	
	public void robotPeriodic()
	{
		this.dashboard.updateContinuous();
	}
	
	public void disabledInit()
	{
		this.dashboard.updateMode("Disabled");
	}
	
	public void autonomousInit()
	{
		this.dashboard.updateMode("Auto");
	}
	
	public void teleopInit()
	{
		this.dashboard.updateMode("Teleop");
	}
	
	public void testInit()
	{
		this.dashboard.updateMode("Test");
	}
	
	private static class Dashboard
	{
		private NetworkTable table;
		private NetworkTableEntry eventEntry, matchTypeEntry, matchNumEntry;
		private NetworkTableEntry modeEntry, timeEntry;
		private NetworkTableEntry batteryEntry;
		private NetworkTableEntry allianceEntry, stationEntry;
		private NetworkTableEntry platesEntry;
		private int timer;
		
		public Dashboard(String tableKey)
		{
			this.table = NetworkTableInstance.getDefault().getTable(tableKey);
			
			this.eventEntry = this.table.getEntry("event");
			this.matchTypeEntry = this.table.getEntry("matchType");
			this.matchNumEntry = this.table.getEntry("matchNum");
			this.modeEntry = this.table.getEntry("matchMode");
			this.timeEntry = this.table.getEntry("time");
			this.batteryEntry = this.table.getEntry("battery");
			this.allianceEntry = this.table.getEntry("alliance");
			this.stationEntry = this.table.getEntry("station");
			this.platesEntry = this.table.getEntry("plates");
			
			this.timer = 0;
		}
		
		public void updateContinuous()
		{
			DriverStation ds = DriverStation.getInstance();
			
			if (this.timer % 5 == 0)
			{
				String event = ds.getEventName();
				this.eventEntry.setString(event);
				
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
				
				int matchNum = ds.getMatchNumber();
				this.matchNumEntry.setNumber(matchNum);
				
				double time = ds.getMatchTime();
				this.timeEntry.setDouble(time);
				
				double battery = RobotController.getBatteryVoltage();
				this.batteryEntry.setDouble(battery);
				
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
				
				int station = ds.getLocation();
				this.stationEntry.setNumber(station);
				
				String plates = ds.getGameSpecificMessage();
				this.platesEntry.setString(plates);
			}
			
			this.timer += 1;
		}
		
		public void updateMode(String mode)
		{
			this.modeEntry.setString(mode);
		}
	}

	private static class MattDupuis
	{
		private Joystick joystick;
		
		public MattDupuis(int joystickUsb)
		{
			this.joystick = new Joystick(joystickUsb);
		}
		
		public double getX()
		{
			return this.joystick.getRawAxis(4);
		}
		
		public double getY()
		{
			return -this.joystick.getRawAxis(1);
		}
	}
}