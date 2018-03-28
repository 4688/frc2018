// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018.components;

import edu.wpi.first.wpilibj.*;

/**
 * Manages pretty much everything related to autonomous and PID.
 * 
 * @author Jacob
 */
public class Autonomous
{
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
	}
	
	/**
	 * Returns the routine number from 0 to 31 based on the current state of the
	 * auto select inputs. Basically, each input is a binary digit.
	 * 
	 * @return Autonomous routine number, from 0 to 31
	 */
	public int getRoutine()
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
	 * Re-calculates PID loops and retrieves new data from the dashboard.
	 */
	public void update(DriveTrain drive, Hugger hugger, Lift lift)
	{		
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
		drive.setLSpd(gDrive + gGyro);
		drive.setRSpd(-gDrive);
		lift.setLiftSpd(gLift);
		hugger.setTiltSpd(gTilt);
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
		
		// Store previous error
		private Object e;
		
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
			this.s = newS;
			
			this.I = 0d;
			this.e = null;
		}
		
		public void addSetpoint(double addS)
		{
			this.s += addS;
			
			this.I = 0d;
			this.e = null;
		}
		
		public void reset()
		{
			this.I = 0d;
			this.e = null;
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