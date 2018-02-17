// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot
{
	private final int JOYSTICK_USB = 0;
	
	private Dashboard dashboard;
	private MattDupuis matt;
	private DriveTrain driveTrain;
	private Hugger hugger;
	private Lift lift;
	private Autonomous auto;
	
	public void robotInit()
	{
		this.dashboard = new Dashboard("SaintsBotDS");
		this.matt = new MattDupuis(JOYSTICK_USB);
		this.driveTrain = new DriveTrain();
		this.hugger = new Hugger();
		this.lift = new Lift();
		this.auto = new Autonomous();
	}
	
	public void robotPeriodic()
	{
		this.dashboard.updateMatchInfo();
		this.dashboard.updateRoutine(this.auto.getRoutine());
		this.dashboard.tick();
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
	
	public void teleopPeriodic()
	{
		this.driveTrain.control(this.matt);
		this.hugger.control(this.matt);
		this.lift.control(this.matt);
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
		private NetworkTableEntry routineEntry;
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
			this.routineEntry = this.table.getEntry("routine");
			
			this.timer = 0;
		}
		
		public void tick()
		{
			this.timer += 1;
		}
		
		public void updateMatchInfo()
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
		}
		
		public void updateMode(String mode)
		{
			this.modeEntry.setString(mode);
		}
		
		public void updateRoutine(int routine)
		{
			if (this.timer % 5 == 0)
			{
				this.routineEntry.setNumber(routine);
			}
		}
	}

	private static class MattDupuis
	{
		private Joystick joystick;
		
		public MattDupuis(int joystickUsb)
		{
			this.joystick = new Joystick(joystickUsb);
		}
		
		public double getForward()
		{
			double fwd = -this.joystick.getRawAxis(1);
			return fwd * Math.abs(fwd);
		}
		
		public double getTurn()
		{
			double turn = this.joystick.getRawAxis(4);
			return turn * Math.abs(turn);
		}
		
		public double getTurbo()
		{
			return 1d + this.joystick.getRawAxis(3) * 0.5;
		}
		
		public double getIntake()
		{
			boolean inBtn = this.joystick.getRawButton(6);
			boolean reverseBtn = this.joystick.getRawButton(2);
			double in = inBtn ? -1d : 0d;
			double reverse = reverseBtn ? -1d : 1d;
			if (!inBtn && reverseBtn) return -0.25d;
			return in * reverse;
		}
		
		public double getTilt()
		{
			int dpad = this.joystick.getPOV();
			if (dpad == 0)
			{
				return -1d;
			}
			else if (dpad == 180)
			{
				return 1d;
			}
			else
			{
				return 0d;
			}
		}
		
		public double getLift()
		{
			boolean up = this.joystick.getRawButton(4);
			boolean dn = this.joystick.getRawButton(3);
			if (up && !dn)
			{
				return 0.65d;
			}
			else if (!up && dn)
			{
				return -0.35d;
			}
			else
			{
				return 0d;
			}
		}
	}
	
	private static class DriveTrain
	{
		private static final double DRIVE_FACTOR = 0.5;
		
		private TalonSRX lfm, lrm, rfm, rrm;
		
		public DriveTrain()
		{
			this.lfm = new TalonSRX(1);
			this.lrm = new TalonSRX(3);
			this.rfm = new TalonSRX(2);
			this.rrm = new TalonSRX(4);
			
			this.lrm.follow(this.lfm);
			this.rrm.follow(this.rfm);
		}
		
		public void control(MattDupuis matt)
		{
			double y = matt.getForward();
			double x = matt.getTurn();
			double l = 0d, r = 0d;
			double d = DRIVE_FACTOR;
			double t = matt.getTurbo();
			if (Math.abs(x) < 0.04)
			{
				l = y;
				r = -y;
			}
			else if (Math.abs(y) < 0.04)
			{
				l = x;
				r = x;
			}
			else if (Math.abs(x) >= 0.04)
			{
				l = x + y;
				r = x - y;
			}
			this.setLSpd(l * d * t);
			this.setRSpd(r * d * t);
		}
		
		public void setLSpd(double spd)
		{
			if (Math.abs(spd) > 0.04)
			{
				this.lfm.set(ControlMode.PercentOutput, spd);
			}
			else
			{
				this.lfm.set(ControlMode.PercentOutput, 0d);
			}
		}
		
		public void setRSpd(double spd)
		{
			if (Math.abs(spd) > 0.04)
			{
				this.rfm.set(ControlMode.PercentOutput, spd);
			}
			else
			{
				this.rfm.set(ControlMode.PercentOutput, 0d);
			}
		}
	}

	private static class Hugger
	{
		TalonSRX intakeL, intakeR;
		Spark tilt;
		DigitalInput lowLim, highLim;
		
		public Hugger()
		{
			this.intakeL = new TalonSRX(5);
			this.intakeR = new TalonSRX(6);
			this.tilt = new Spark(0);
			this.lowLim = new DigitalInput(0);
			this.highLim = new DigitalInput(1);
		}
		
		public void control(MattDupuis matt)
		{
			double intake = matt.getIntake();
			this.intakeL.set(ControlMode.PercentOutput, intake);
			this.intakeR.set(ControlMode.PercentOutput, -intake);
			
			double tilt = matt.getTilt();
			if (!this.lowLim.get())
			{
				tilt = Math.min(tilt, 0);
			}
			else if (!this.highLim.get())
			{
				tilt = Math.max(tilt, 0);
			}
			this.tilt.set(tilt);
		}
	}
	
	private static class Lift
	{
		DigitalInput lowLim, highLim;
		Spark lifty;
		Servo lock;
		
		public Lift()
		{
			this.lowLim = new DigitalInput(2);
			this.highLim = new DigitalInput(3);
			this.lifty = new Spark(1);
			this.lock = new Servo(2);
		}
		
		public void control(MattDupuis matt)
		{
			double lift = matt.getLift();
			System.out.println(matt.getLift() + "\t" + (matt.getTurbo() * 2 - 2));
			if ((lift < -0.04 && this.lowLim.get()) || (lift > 0.04 && this.highLim.get()))
			{
				this.lifty.set(lift);
				this.lock.set(0d);
			}
			else
			{
				this.lifty.set(0d);
				this.lock.set(matt.getTurbo() * 2 - 2);
			}
		}
	}
	
	private static class Autonomous
	{
		DigitalInput a1, a2, a4, a8, a16;
		
		public Autonomous()
		{
			this.a1 = new DigitalInput(19);
			this.a2 = new DigitalInput(20);
			this.a4 = new DigitalInput(21);
			this.a8 = new DigitalInput(22);
			this.a16 = new DigitalInput(23);
		}
		
		public int getRoutine()
		{
			int n1 = (this.a1.get() ? 0 : 1) << 0;
			int n2 = (this.a2.get() ? 0 : 1) << 1;
			int n4 = (this.a4.get() ? 0 : 1) << 2;
			int n8 = (this.a8.get() ? 0 : 1) << 3;
			int n16 = (this.a16.get() ? 0 : 1) << 4;
			return n1 + n2 + n4 + n8 + n16;
		}
	}
}