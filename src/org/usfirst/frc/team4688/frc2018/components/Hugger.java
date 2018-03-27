// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018.components;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.*;

/**
 * Manages the intake system and tilt motor.
 * 
 * @author Jacob
 */
public class Hugger
{
	// Intake/eject speed factor
	private final double BASE_INTAKE_SPD = 1d;
	
	// Upwards tilt speed
	private final double TILT_UP_SPD = 1d;
	
	// Downwards tilt speed
	private final double TILT_DOWN_SPD = -1d;
	
	// Intake motor CAN indices
	private final int INTAKE_L_CAN = 5;
	private final int INTAKE_R_CAN = 6;
	
	// Tilt motor PWM index
	private final int TILT_PWM = 0;
	
	// Limit switch DIO indices
	private final int LOWLIM_DIO = 0;
	private final int HIGHLIM_DIO = 1;
	
	// Encoder DIO channel A (channel B is this + 1)
	private final int TILTENC_DIO = 4;
	
	// Encoder resolution (pulses per revolution)
	private final double ENC_RESOLUTION = 2048d;
	
	// Intake motors
	private TalonSRX intakeL, intakeR;
	
	// Tilt motor
	private Spark tilt;
	
	// Limit switches
	private DigitalInput lowLim, highLim;
	
	// Tilt motor encoder
	private Encoder tiltEnc;
	
	// Current tilt speed
	private double tiltSpd;
	
	/**
	 * Constructor.
	 */
	public Hugger()
	{
		// Intake motors
		this.intakeL = new TalonSRX(INTAKE_L_CAN);
		this.intakeR = new TalonSRX(INTAKE_R_CAN);
		
		// Tilt motor and encoder
		this.tilt = new Spark(TILT_PWM);
		this.tiltEnc = new Encoder(TILTENC_DIO, TILTENC_DIO + 1);
		this.tiltEnc.setDistancePerPulse(1d / ENC_RESOLUTION);
		
		// Limit switches
		this.lowLim = new DigitalInput(LOWLIM_DIO);
		this.highLim = new DigitalInput(HIGHLIM_DIO);
	}
	
	/**
	 * Sets the intake motor speeds; -1 means full speed eject and 1 means full
	 * speed intake.
	 * 
	 * @param spd Intake speed, between -1 and 1
	 */
	public void setIntakeSpd(double spd)
	{
		// Clamp speeds to +/-100%
		spd = Math.min(Math.max(spd, -1d), 1d);
		
		// Set speeds
		this.intakeL.set(ControlMode.PercentOutput, spd);
		this.intakeR.set(ControlMode.PercentOutput, -spd);
	}
	
	/**
	 * Sets the tilt motor speed taking limit switches into consideration; -1
	 * means full speed tilt down, 0 means no motion, and 1 means full speed
	 * tilt up.
	 * 
	 * @param spd Tilt motor speed, between -1 and 1
	 */
	public void setTiltSpd(double spd)
	{
		spd = Math.min(Math.max(spd, -1d), 1d);
		this.tilt.set(spd);
		this.tiltSpd = spd;
	}
	
	/**
	 * Sets drive motor speeds based on driver input.
	 * 
	 * @param matt The Matt that is driving the robot
	 */
	public void control(MattDupuis matt)
	{
		// Intake speeds
		double intake = matt.getIntake() * BASE_INTAKE_SPD;
		this.setIntakeSpd(intake);
		
		// Tilt actions
		double spd;
		switch (matt.getTilt())
		{
			case Up:
				spd = TILT_UP_SPD;
				break;
			case Down:
				spd = TILT_DOWN_SPD;
				break;
			case None:
			default:
				spd = 0d;
		}
		this.setTiltSpd(spd);
	}
	
	/**
	 * Calculates a very approximate angle at which a cube would be tilted.
	 * 
	 * @return Approximate angle above the horizontal, in degrees
	 */
	public double getAngle()
	{
		return this.tiltEnc.getDistance() * 100;
	}
	
	/**
	 * Gets the current tilt motor speed; -1 means full speed tilting up, 0
	 * means no motion, 1 means full speed tilting down.
	 * 
	 * @return Tilt speed, between -1 and 1
	 */
	public double getTiltSpeed()
	{
		return this.tiltSpd;
	}
	
	/**
	 * Returns the INVERTED value of the bottom limit switch; true means the
	 * switch is being made and false means it is not.
	 * 
	 * @return True if the switch is being made, false otherwise.
	 */
	public boolean getLowLim()
	{
		return !this.lowLim.get();
	}
	
	/**
	 * Returns the INVERTED value of the top limit switch; true means the switch
	 * is being made and false means it is not.
	 * 
	 * @return True if the switch is being made, false otherwise.
	 */
	public boolean getHighLim()
	{
		return !this.highLim.get();
	}
}