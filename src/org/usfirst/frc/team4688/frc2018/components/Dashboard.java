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
	private final int UPDATE_RATE = 5;
	
	// Camera server network port; should be in range 1180-1190
	private final int CAMERA_PORT = 1181;
	//*/
	
	// Table containing entries
	private NetworkTable table;
	
	// Constant match info entries
	private NetworkTableEntry eventEntry, matchTypeEntry, matchNumEntry;
	private NetworkTableEntry allianceEntry, stationEntry;
	
	// Ongoing match/robot info entries
	private NetworkTableEntry modeEntry, timeEntry;
	private NetworkTableEntry batteryEntry;
	private NetworkTableEntry platesEntry;
	
	// Driver input entries
	private NetworkTableEntry forwardEntry, turnEntry;
	private NetworkTableEntry turboEntry;
	
	// Drive train entries
	private NetworkTableEntry leftSpdEntry, rightSpdEntry;
	private NetworkTableEntry distanceEntry, headingEntry;
	
	// Hugger tilt entries
	private NetworkTableEntry tiltEntry;
	private NetworkTableEntry tiltSpdEntry;
	private NetworkTableEntry tiltLowLimEntry, tiltHighLimEntry;
	
	// Lift entries
	private NetworkTableEntry liftEntry;
	private NetworkTableEntry liftLowLimEntry, liftHighLimEntry;
	
	// Climber entry
	private NetworkTableEntry deployedEntry;
	
	// Autonomous routine entry
	private NetworkTableEntry routineEntry;
	
	// PID entries
	private NetworkTableEntry driveSEntry, driveKPEntry, driveKIEntry, driveKDEntry;
	private NetworkTableEntry gyroSEntry, gyroKPEntry, gyroKIEntry, gyroKDEntry;
	private NetworkTableEntry liftSEntry, liftKPEntry, liftKIEntry, liftKDEntry;
	private NetworkTableEntry tiltSEntry, tiltKPEntry, tiltKIEntry, tiltKDEntry;
	private NetworkTableEntry driveGainEntry, driveErrorEntry;
	private NetworkTableEntry gyroGainEntry, gyroErrorEntry;
	private NetworkTableEntry liftGainEntry, liftErrorEntry;
	private NetworkTableEntry tiltGainEntry, tiltErrorEntry;
	
	// Camera and server
	private UsbCamera camera;
	private MjpegServer server;
	//*/
	
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
		this.platesEntry = this.table.getEntry("plates");
		
		// Driver input entries
		this.forwardEntry = this.table.getEntry("forward");
		this.turnEntry = this.table.getEntry("turn");
		this.turboEntry = this.table.getEntry("turbo");
		
		// Drive train entries
		this.leftSpdEntry = this.table.getEntry("leftSpd");
		this.rightSpdEntry = this.table.getEntry("rightSpd");
		this.distanceEntry = this.table.getEntry("distance");
		this.headingEntry = this.table.getEntry("heading");
		
		// Hugger entries
		this.tiltEntry = this.table.getEntry("tilt");
		this.tiltSpdEntry = this.table.getEntry("tiltSpd");
		this.tiltLowLimEntry = this.table.getEntry("tiltLowLim");
		this.tiltHighLimEntry = this.table.getEntry("tiltHighLim");
		
		// Lift entries
		this.liftEntry = this.table.getEntry("height");
		this.liftLowLimEntry = this.table.getEntry("liftLowLim");
		this.liftHighLimEntry = this.table.getEntry("liftHighLim");
		
		// Climber entry
		this.deployedEntry = this.table.getEntry("deployed");
		
		// Autonomous routine entry
		this.routineEntry = this.table.getEntry("routine");
		
		// PID entries
		this.driveSEntry = this.table.getEntry("PID/drive/s");
		this.driveKPEntry = this.table.getEntry("PID/drive/kP");
		this.driveKIEntry = this.table.getEntry("PID/drive/kI");
		this.driveKDEntry = this.table.getEntry("PID/drive/kD");
		this.gyroSEntry = this.table.getEntry("PID/gyro/s");
		this.gyroKPEntry = this.table.getEntry("PID/gyro/kP");
		this.gyroKIEntry = this.table.getEntry("PID/gyro/kI");
		this.gyroKDEntry = this.table.getEntry("PID/gyro/kD");
		this.liftSEntry = this.table.getEntry("PID/lift/s");
		this.liftKPEntry = this.table.getEntry("PID/lift/kP");
		this.liftKIEntry = this.table.getEntry("PID/lift/kI");
		this.liftKDEntry = this.table.getEntry("PID/lift/kD");
		this.tiltSEntry = this.table.getEntry("PID/tilt/s");
		this.tiltKPEntry = this.table.getEntry("PID/tilt/kP");
		this.tiltKIEntry = this.table.getEntry("PID/tilt/kI");
		this.tiltKDEntry = this.table.getEntry("PID/tilt/kD");
		this.driveGainEntry = this.table.getEntry("PID/drive/gain");
		this.driveErrorEntry = this.table.getEntry("PID/drive/error");
		this.gyroGainEntry = this.table.getEntry("PID/gyro/gain");
		this.gyroErrorEntry = this.table.getEntry("PID/gyro/error");
		this.liftGainEntry = this.table.getEntry("PID/lift/gain");
		this.liftErrorEntry = this.table.getEntry("PID/lift/error");
		this.tiltGainEntry = this.table.getEntry("PID/tilt/gain");
		this.tiltErrorEntry = this.table.getEntry("PID/tilt/error");
		
		// Set up and start streaming camera feed to server
		this.camera = new UsbCamera("cam0", 0);
		this.server = new MjpegServer("server0", CAMERA_PORT);
		this.server.setSource(this.camera);
		//*/
		
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
	
	/**
	 * Sends driver input (forward, turn, turbo) to the dashboard.
	 * 
	 * @param matt The Matt Dupuis to send to the dashboard
	 */
	public void updateMatt(MattDupuis matt)
	{
		if (this.timer % (50 / UPDATE_RATE) == 0)
		{
			// Update forward
			double fwd = matt.getForward();
			this.forwardEntry.setDouble(fwd);
			
			// Update turn
			double turn = matt.getTurn();
			this.turnEntry.setDouble(turn);
			
			// Update turbo
			double turbo = matt.getTurbo();
			this.turboEntry.setDouble(turbo);
		}
	}
	
	/**
	 * Sends drive speeds and sensors to the dashboard.
	 * 
	 * @param drive The Drive Train object to monitor
	 */
	public void updateDrive(DriveTrain drive)
	{
		if (this.timer % (50 / UPDATE_RATE) == 0)
		{
			// Update drive speeds
			double l = drive.getLSpd();
			double r = drive.getRSpd();
			this.leftSpdEntry.setDouble(l);
			this.rightSpdEntry.setDouble(r);
			
			// Update distance
			double distance = drive.getDistance();
			this.distanceEntry.setDouble(distance);
			
			// Update heading
			double heading = drive.getHeading();
			this.headingEntry.setDouble(heading);
		}
	}
	
	/**
	 * Sends hugger tilt info to the dashboard.
	 * 
	 * @param hugger The Hugger object to monitor
	 */
	public void updateHugger(Hugger hugger)
	{
		if (this.timer % (50 / UPDATE_RATE) == 0)
		{
			// Update tilt angle
			double tilt = hugger.getAngle();
			this.tiltEntry.setDouble(tilt);
			
			// Update tilt speed
			double tiltSpd = hugger.getTiltSpeed();
			this.tiltSpdEntry.setDouble(tiltSpd);
			
			// Update limit switches
			boolean lowLim = hugger.getLowLim();
			this.tiltLowLimEntry.setBoolean(lowLim);
			boolean highLim = hugger.getHighLim();
			this.tiltHighLimEntry.setBoolean(highLim);
		}
	}
	
	/**
	 * Sends the lift height and limit switches to the dashboard.
	 * 
	 * @param lift The Lift object to monitor
	 */
	public void updateLift(Lift lift)
	{
		if (this.timer % (50 / UPDATE_RATE) == 0)
		{
			// Update lift height
			double height = lift.getHeight();
			this.liftEntry.setDouble(height);
			
			// Update limit switches
			boolean lowLim = lift.getLowLim();
			this.liftLowLimEntry.setBoolean(lowLim);
			boolean highLim = lift.getHighLim();
			this.liftHighLimEntry.setBoolean(highLim);
		}
	}
	
	/**
	 * Sends the climber deploy status to the dashboard.
	 * 
	 * @param climber The Climber object to monitor
	 */
	public void updateClimber(Climber climber)
	{
		if (this.timer % (50 / UPDATE_RATE) == 0)
		{
			// Update deploy status
			boolean deployed = climber.isDeployed();
			this.deployedEntry.setBoolean(deployed);
		}
	}
	
	/**
	 * Sends the autonomous routine number and PID gains/error to the dashboard.
	 * 
	 * @param auto The Autonomous object to monitor
	*/
	public void updateAutonomous(Autonomous auto)
	{
		if (this.timer % (50 / UPDATE_RATE) == 0)
		{
			// Update routine number
			int routine = auto.getBehaviorNum();
			this.routineEntry.setNumber(routine);
			
			// Update gains and error
			this.driveGainEntry.setDouble(auto.driveLoop.get());
			this.driveErrorEntry.setDouble(auto.driveLoop.getError());
			this.gyroGainEntry.setDouble(auto.gyroLoop.get());
			this.gyroErrorEntry.setDouble(auto.gyroLoop.getError());
			this.liftGainEntry.setDouble(auto.liftLoop.get());
			this.liftErrorEntry.setDouble(auto.liftLoop.getError());
			this.tiltGainEntry.setDouble(auto.tiltLoop.get());
			this.tiltErrorEntry.setDouble(auto.tiltLoop.getError());
			
			// Retrieve new values and update loop constants
			auto.driveLoop.reset(this.getConstants(PIDType.Drive));
			auto.gyroLoop.reset(this.getConstants(PIDType.Gyro));
			auto.liftLoop.reset(this.getConstants(PIDType.Lift));
			auto.tiltLoop.reset(this.getConstants(PIDType.Tilt));
		}
	}
	
	/**
	 * Represents the four constants (setpoint + three coefficients) in a single
	 * PID loop.
	 * 
	 * @author Jacob
	 */
	public static class PIDInfo
	{
		// Constants received from dashboard
		public double s, kP, kI, kD;
		
		public PIDInfo(double s, double kP, double kI, double kD)
		{
			// Set constants
			this.s = s;
			this.kP = kP;
			this.kI = kI;
			this.kD = kD;
		}
	}
	
	/**
	 * Possible PID loops for which information can be retrieved from the
	 * dashboard.
	 */
	public enum PIDType
	{
		Drive, Gyro, Lift, Tilt
	}
	
	/**
	 * Returns the PID constants for a given loop.
	 * 
	 * @return PIDInfo object containing setpoint and coefficients
	 */
	private PIDInfo getConstants(PIDType pid)
	{
		// Constants
		double s, kP, kI, kD;
		
		// Get constants for the appropriate loop type
		switch (pid)
		{
			case Drive:
				s = this.driveSEntry.getDouble(0d);
				kP = this.driveKPEntry.getDouble(0d);
				kI = this.driveKIEntry.getDouble(0d);
				kD = this.driveKDEntry.getDouble(0d);
				break;
			case Gyro:
				s = this.gyroSEntry.getDouble(0d);
				kP = this.gyroKPEntry.getDouble(0d);
				kI = this.gyroKIEntry.getDouble(0d);
				kD = this.gyroKDEntry.getDouble(0d);
				break;
			case Lift:
				s = this.liftSEntry.getDouble(0d);
				kP = this.liftKPEntry.getDouble(0d);
				kI = this.liftKIEntry.getDouble(0d);
				kD = this.liftKDEntry.getDouble(0d);
				break;
			case Tilt:
				s = this.tiltSEntry.getDouble(0d);
				kP = this.tiltKPEntry.getDouble(0d);
				kI = this.tiltKIEntry.getDouble(0d);
				kD = this.tiltKDEntry.getDouble(0d);
				break;
			default:
				return new PIDInfo(0d, 0d, 0d, 0d);
		}
		
		// Return constants
		return new PIDInfo(s, kP, kI, kD);
	}
}