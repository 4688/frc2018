// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018.components;

import java.util.List;
import edu.wpi.first.wpilibj.*;

/**
 * Manages pretty much everything related to autonomous and PID.
 * 
 * @author Jacob
 */
public class Autonomous
{
	// Settling time for PID; error must be consistently near 0 for this many
	// 1/50ths of a second in order to continue
	private final int SETTLING_TIME = 20;
	
	// Settling thresholds
	private final double DRIVE_SETTLED = 1d;
	private final double GYRO_SETTLED = 1d;
	private final double LIFT_SETTLED = 1d;
	private final double TILT_SETTLED = 1d;
	
	// DIO indices for autonomous select inputs
	private final int AUTO1_DIO = 19;
	private final int AUTO2_DIO = 20;
	private final int AUTO4_DIO = 21;
	private final int AUTO8_DIO = 22;
	private final int AUTO16_DIO = 23;
	
	// Autonomous select inputs
	private DigitalInput a1, a2, a4, a8, a16;
	
	// PID loops
	public PIDLoop driveLoop, gyroLoop, liftLoop, tiltLoop;
	
	// Autonomous segment tracker and general-use timer
	private int p, t;
	
	// RNG
	String rng;
	
	/**
	 * Constructor.
	 */
	public Autonomous()
	{
		// Autonomous select inputs
		this.a1 = new DigitalInput(AUTO1_DIO);
		this.a2 = new DigitalInput(AUTO2_DIO);
		this.a4 = new DigitalInput(AUTO4_DIO);
		this.a8 = new DigitalInput(AUTO8_DIO);
		this.a16 = new DigitalInput(AUTO16_DIO);
		
		// PID loops
		this.driveLoop = new PIDLoop(0d, 0d, 0d, 0d);
		this.gyroLoop = new PIDLoop(0d, 0d, 0d, 0d);
		this.liftLoop = new PIDLoop(0d, 0d, 0d, 0d);
		this.tiltLoop = new PIDLoop(0d, 0d, 0d, 0d);
		
		// Auto routine tracking
		this.p = 0;
		this.t = 0;
		
		// RNG
		this.rng = "";
	}
	
	/**
	 * Returns the routine number from 0 to 31 based on the current state of the
	 * auto select inputs. Basically, each input is a binary digit.
	 * 
	 * @return Autonomous behavior number, from 0 to 31
	 */
	public int getBehaviorNum()
	{
		// Read inputs and convert to numbers
		int n1 = (this.a1.get() ? 0 : 1) << 0;
		int n2 = (this.a2.get() ? 0 : 1) << 1;
		int n4 = (this.a4.get() ? 0 : 1) << 2;
		int n8 = (this.a8.get() ? 0 : 1) << 3;
		int n16 = (this.a16.get() ? 0 : 1) << 4;
		
		// Add to get routine number
		return n1 + n2 + n4 + n8 + n16;
	}
	
	/**
	 * Enumeration of possible autonomous behaviors.
	 */
	private enum Behavior
	{
		Nothing,
		RBaseline, RSwitch, RSwitch_Scale, RScale, RScale_Switch,
		LBaseline, LSwitch, LSwitch_Scale, LScale, LScale_Switch,
		MBaseline, MSwitch
	}
	
	private Behavior getBehavior()
	{
		switch(this.getBehaviorNum())
		{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				return Behavior.Nothing;
				
			case 8:
			case 9:
			case 10:
			case 11:
				return Behavior.RBaseline;
			case 12:
				return Behavior.RSwitch;
			case 13:
				return Behavior.RSwitch_Scale;
			case 14:
				return Behavior.RScale;
			case 15:
				return Behavior.RScale_Switch;
			
			case 16:
			case 17:
			case 18:
			case 19:
				return Behavior.LBaseline;
			case 20:
				return Behavior.LSwitch;
			case 21:
				return Behavior.LSwitch_Scale;
			case 22:
				return Behavior.LScale;
			case 23:
				return Behavior.LScale_Switch;
		
			case 24:
			case 25:
			case 26:
			case 27:
				return Behavior.MBaseline;
			case 28:
			case 29:
			case 30:
			case 31:
				return Behavior.MSwitch;
			
			default:
				return Behavior.Nothing;
		}
	}
	
	/**
	 * Enumeration of possible autonomous routines.
	 */
	private enum Routine
	{
		Nothing,
		RBaseline, RSwitch, RScale,
		LBaseline, LSwitch, LScale,
		MBaseline, MSwitchL, MSwitchR
	}
	
	public void retrieveRNG()
	{
		this.rng = DriverStation.getInstance().getGameSpecificMessage() + "   ";
	}
	
	/**
	 * Re-calculates PID loops and retrieves new data from the dashboard.
	 */
	public void update(DriveTrain drive, Hugger hugger, Lift lift)
	{		
		boolean done = false;
		
		// Map behavior to routine
		Routine r;
		switch (this.getBehavior())
		{
			case Nothing:
				r = Routine.Nothing;
				break;
			
			case RBaseline:
				r = Routine.RBaseline;
				break;
			case RSwitch:
				r = this.rng.charAt(0) == 'R'
						?
							Routine.RSwitch
						:
							Routine.RBaseline;
				break;
			case RSwitch_Scale:
				r = this.rng.charAt(0) == 'R'
						?
							Routine.RSwitch
						:
							this.rng.charAt(1) == 'R'
									?
										Routine.RScale
									:
										Routine.RBaseline;
				break;
			case RScale:
				r = this.rng.charAt(1) == 'R'
						?
							Routine.RScale
						:
							Routine.RBaseline;
				break;
			case RScale_Switch:
				r = this.rng.charAt(1) == 'R'
						?
							Routine.RScale
						:
							this.rng.charAt(0) == 'R'
									?
										Routine.RSwitch
									:
										Routine.RBaseline;
				break;
			
			case LBaseline:
				r = Routine.LBaseline;
				break;
			case LSwitch:
				r = this.rng.charAt(0) == 'L'
						?
							Routine.LSwitch
						:
							Routine.LBaseline;
				break;
			case LSwitch_Scale:
				r = this.rng.charAt(0) == 'L'
						?
							Routine.LSwitch
						:
							this.rng.charAt(1) == 'L'
									?
										Routine.LScale
									:
										Routine.LBaseline;
				break;
			case LScale:
				r = this.rng.charAt(1) == 'L'
						?
							Routine.LScale
						:
							Routine.LBaseline;
				break;
			case LScale_Switch:
				r = this.rng.charAt(1) == 'L'
						?
							Routine.LScale
						:
							this.rng.charAt(0) == 'L'
									?
										Routine.LSwitch
									:
										Routine.LBaseline;
				break;
			
			case MBaseline:
				r = Routine.MBaseline;
				break;
			case MSwitch:
				r = this.rng.charAt(0) == 'L'
						?
							Routine.MSwitchL
						:
							this.rng.charAt(0) == 'R'
									?
										Routine.MSwitchR
									:
										Routine.MBaseline;
				break;
			
			default:
				r = Routine.Nothing;
		}
		
		// Perform auto route
		switch (r)
		{
			case Nothing:
				break;
			
			case RBaseline:
			case LBaseline:
				switch (this.p)
				{
					case 0: // Drive forward
						this.driveLoop.resetSetpoint(148d);
						this.gyroLoop.resetSetpoint(0d);
						if (driveLoop.isSettled(DRIVE_SETTLED)) this.p += 1;
						break;
					default: // Done
						done = true;
						break;
				}
				break;
		}
		
		// Update loops
		this.driveLoop.calculate(drive.getDistance());
		this.gyroLoop.calculate(drive.getHeading());
		this.liftLoop.calculate(lift.getHeight());
		this.tiltLoop.calculate(hugger.getAngle());
				
		// Gains
		double gDrive = this.driveLoop.get();
		double gGyro = this.gyroLoop.get();
		double gLift = this.liftLoop.get();
		double gTilt = this.tiltLoop.get();
				
		// Set motor speeds
		if (!done)
		{
			drive.setLSpd(gDrive + gGyro);
			drive.setRSpd(-gDrive);
			lift.setLiftSpd(gLift);
			hugger.setTiltSpd(gTilt);
		}
		else
		{
			drive.setLSpd(0d);
			drive.setRSpd(0d);
			lift.setLiftSpd(0d);
			hugger.setTiltSpd(0d);
			hugger.setIntakeSpd(0d);
		}
	}
	
	/**
	 * Manages a single PID loop.
	 * 
	 * @author Jacob
	 */
	public class PIDLoop
	{
		// Setpoint and coefficients
		private double s, kP, kI, kD;
		
		// Proprtion, Integral, and Derivative values
		private double P, I, D;
		
		// Store previous errors
		private Object e;
		private List<Double> e50;
		
		public PIDLoop(double s, double kP, double kI, double kD)
		{
			this.s = s;
			this.kP = kP;
			this.kI = kI;
			this.kD = kD;
			
			this.P = 0d;
			this.I = 0d;
			this.D = 0d;
			this.e = null;
		}
		
		public void resetSetpoint(double newS)
		{
			if (newS != this.s)
			{
				this.s = newS;
				
				this.I = 0d;
				this.e = null;
				this.e50.clear();
			}
		}
		
		public void addSetpoint(double addS)
		{
			this.s += addS;
			
			this.I = 0d;
			this.e = null;
			this.e50.clear();
		}
		
		public void reset()
		{
			this.I = 0d;
			this.e = null;
			this.e50.clear();
		}
		
		public void reset(Dashboard.PIDInfo theNew)
		{
			this.s = theNew.s;
			this.kP = theNew.kP;
			this.kI = theNew.kI;
			this.kD = theNew.kD;
			
			this.reset();
		}
		
		public void calculate(double input)
		{
			this.P = this.s - input;
			this.I += (this.s - input)  / 50d;
			this.D = 0d;
			if (this.e instanceof Double)
			{
				this.D = (this.s - input - ((double) this.e)) * 50d;
			}
			this.e = this.s - input;
			this.e50.add((double) this.e);
			while (this.e50.size() > 25)
			{
				this.e50.remove(0);
			}
		}
		
		public boolean isSettled(double threshold)
		{
			for (double e : this.e50)
			{
				if (Math.abs(e) > threshold) return false;
			}
			return this.e50.size() > 0;
		}
		
		public double get()
		{
			return this.kP*this.P + this.kI*this.I + this.kD*this.D;
		}
		
		public double getError()
		{
			return this.P;
		}
	}
}