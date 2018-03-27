// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018.components;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import com.kauailabs.navx.frc.*;
import edu.wpi.first.wpilibj.*;

/**
 * Manages the drive motors and associated sensors.
 * 
 * @author Jacob
 */
public class DriveTrain
{
	// Base drive speed (calculated speeds are multiplied by this value)
	private final double BASE_SPD = 0.5d;
	
	// Drive motor % deadband (speeds lower than this do not draw enough voltage
	// to actually make the motors move)
	private final double DEADBAND = 0.04d;
	
	// Wheel diameter, in inches
	private final double WHEEL_DIAMETER = 6d;
	
	// Max turbo multiplier
	private final double TURBO_FACTOR = 1.5d;
	
	// Drive motor CAN indices
	private final int LFM_CAN = 1;
	private final int LRM_CAN = 3;
	private final int RFM_CAN = 2;
	private final int RRM_CAN = 4;
	
	// Drive motors
	private TalonSRX lfm, lrm, rfm, rrm;
	
	// NavX
	private AHRS navx;
	
	// Set initial left/right speeds (for sending to dashboard)
	private double leftSpd, rightSpd;
	
	/**
	 * Constructor.
	 */
	public DriveTrain()
	{
		// Initialize drive motors
		this.lfm = new TalonSRX(LFM_CAN);
		this.lrm = new TalonSRX(LRM_CAN);
		this.rfm = new TalonSRX(RFM_CAN);
		this.rrm = new TalonSRX(RRM_CAN);
		
		// Make rear motors follow front
		this.lrm.follow(this.lfm);
		this.rrm.follow(this.rfm);
		
		// Initialize NavX
		this.navx = new AHRS(SPI.Port.kMXP);
		
		// Set initial left/right speeds
		this.leftSpd = 0d;
		this.rightSpd = 0d;
	}
	
	/**
	 * Sets the speeds of the left drive motors; -1 means full speed backwards,
	 * 0 means no motion, and 1 means full speed forwards.
	 * 
	 * @param spd Left drive motor speed, between -1 and 1
	 */
	public void setLSpd(double spd)
	{
		// Clamp speeds to +/-100%
		spd = Math.min(Math.max(spd, -1d), 1d);
		
		// If speed is above deadband:
		if (Math.abs(spd) > DEADBAND)
		{
			this.lfm.set(ControlMode.PercentOutput, spd);
		}
		// Otherwise, speed is below deadband, so set motor speed to 0; this is
		// important or the robot will maintain its previous motor speed and
		// continue moving at a slow speed (learn from my mistakes)
		else
		{
			this.lfm.set(ControlMode.PercentOutput, 0d);
		}
		
		// Store value to send to dashboard
		this.leftSpd = spd;
	}
	
	/**
	 * Sets the speeds of the right drive motors; -1 means full speed towards
	 * the front, 0 means no motion, and 1 means full speed towards the back.
	 * 
	 * @param spd Right drive motor speed, between -1 and 1
	 */
	public void setRSpd(double spd)
	{
		// Clamp speeds to +/-100%
		spd = Math.min(Math.max(spd, -1d), 1d);
		
		// If speed is above deadband:
		if (Math.abs(spd) > DEADBAND)
		{
			this.rfm.set(ControlMode.PercentOutput, spd);
		}
		// Otherwise, speed is below deadband, so set motor speed to 0; this is
		// important or the robot will maintain its previous motor speed and
		// continue moving at a slow speed (learn from my mistakes)
		else
		{
			this.rfm.set(ControlMode.PercentOutput, 0d);
		}
		
		// Store value to send to dashboard
		this.rightSpd = spd;
	}
	
	/**
	 * Gets the previous set left drive speed.
	 * 
	 * @return Previous left drive speed
	 */
	public double getLSpd()
	{
		return this.leftSpd;
	}
	
	/**
	 * Gets the previous set right drive speed.
	 * 
	 * @return Previous right drive speed
	 */
	public double getRSpd()
	{
		return this.rightSpd;
	}
	
	/**
	 * Sets drive motor speeds based on driver input.
	 * 
	 * @param matt The Matt that is driving the robot
	 */
	public void control(MattDupuis matt)
	{
		// Axis values
		double fwd = matt.getForward();
		double turn = matt.getTurn();
		double turbo = 1d + matt.getTurbo() * (TURBO_FACTOR - 1d);
		
		// Final left/right speeds
		double l = 0d, r = 0d;
		
		// Straight driving
		if (Math.abs(turn) < DEADBAND)
		{
			l = fwd;
			r = -fwd;
		}
		// Stationary pivot
		else if (Math.abs(fwd) < 0.04)
		{
			l = turn;
			r = turn;
		}
		// Driving while turning
		else if (Math.abs(turn) >= 0.04)
		{
			l = turn + fwd;
			r = turn - fwd;
		}
		
		// Set motor speeds
		this.setLSpd(BASE_SPD * l * turbo);
		this.setRSpd(BASE_SPD * r * turbo);
	}
	
	/**
	 * Calculates the displacement traveled by the left drive motors based on
	 * the encoder value and wheel diameter.
	 * 
	 * @return The distance traveled, in inches
	 */
	public double getDistance()
	{
		double enc = this.lfm.getSensorCollection().getQuadraturePosition();
		return WHEEL_DIAMETER * Math.PI * (-enc / 4096d);
	}
	
	/**
	 * @return Current heading as reported by the NavX gyro
	 */
	public double getHeading()
	{
		return this.navx.getAngle();
	}
	
	/**
	 * Zeroes drive encoder and gyro.
	 */
	public void zeroSensors()
	{
		this.lfm.getSensorCollection().setQuadraturePosition(0, 0);
		this.navx.reset();
	}
}