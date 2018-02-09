// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018;

import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot
{
	private Dashboard dashboard;
	
	public void robotInit()
	{
		this.dashboard = new Dashboard();
	}
	
	private class Dashboard
	{
		private NetworkTable table;
		private NetworkTableEntry eventEntry, matchTypeEntry, matchNumEntry;
		private NetworkTableEntry modeEntry, timeEntry;
		private NetworkTableEntry batteryEntry;
		private NetworkTableEntry allianceEntry, stationEntry;
		private NetworkTableEntry platesEntry;
		private int timer;
		
		public Dashboard()
		{
			this.table = NetworkTableInstance.getDefault().getTable("SaintsBotDS");
			
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
	}
}