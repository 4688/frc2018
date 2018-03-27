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
	 * Manages a single PID loop.
	 * 
	 * @author Jacob
	 */
	public class PIDLoop
	{
		// Setpoint and coefficients
		private double s, kP, kI, kD;
		
		// Stored integral and previous error
		private double integral
		private Object error;
		
		public PIDLoop(double s, double kP, double kI, double kD)
		{
			this.s = s;
			this.kP = kP;
			this.kI = kI;
			this.kD = kD;
			
			this.integral = 0d;
			this.error = null;
		}
		
		public void update(Dashboard.PIDInfo theNew)
		{
			this.s = theNew.s;
			this.kP = theNew.kP;
			this.kI = theNew.kI;
			this.kD = theNew.kD;
			
			this.integral = 0d;
			this.error = null;
		}
		
		public double getGain(double input)
		{
			double E = this.s - input;
			double P = this.kP * E;
		}
	}
}