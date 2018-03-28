// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018.components;

import edu.wpi.first.wpilibj.*;

/**
 * Manages the cube elevator and associated sensors.
 * 
 * @author Jacob
 */
public class Lift
{
	// Raising lift speed
	private final double RAISE_SPD = 0.9d;
	
	// Lowering lift speed
	private final double LOWER_SPD = -0.6d;
	
	// Unlocked and locked servo positions
	private final double LOCKED_POS = 0.2d;
	private final double UNLOCKED_POS = 1d;
	
	// Delay before allowing chain to move downwards, in 1/50s of a second
	private final int UNLOCK_DELAY = 15;
	
	// Approximate inches raised per revolution of the lift motor
	private final double ENC_IPR = 8.1d;
	
	// Lift motor PWM index
	private final int LIFT_PWM = 1;
	
	// Lock servo PWM index
	private final int LOCK_PWM = 2;
	
	// Limit switch DIO indices
	private final int LOWLIM_DIO = 2;
	private final int HIGHLIM_DIO = 3;
	
	// Lift motor encoder channel A (channel B is this + 1)
	private final int LIFTENC_DIO = 6;
	
	// Encoder resolution (pulses per revolution)
	private final double ENC_RESOLUTION = 2048d;
	
	// Motor deadband
	private final double DEADBAND = 0.04d;
	
	// Lift motor and encoder
	private Spark lifty;
	private Encoder liftEnc;
	
	// Chain lock servo
	private Servo lock;
	
	// Limit switches
	private DigitalInput lowLim, highLim;
	
	// Lock delay timer
	private int lockTimer;
	
	/**
	 * Constructor.
	 */
	public Lift()
	{
		// Lift motor
		this.lifty = new Spark(LIFT_PWM);
		
		// Lift motor encoder
		this.liftEnc = new Encoder(LIFTENC_DIO, LIFTENC_DIO + 1);
		this.liftEnc.setDistancePerPulse(1d / ENC_RESOLUTION);
		
		// Chain lock servo
		this.lock = new Servo(LOCK_PWM);
		
		// Limit switches
		this.lowLim = new DigitalInput(LOWLIM_DIO);
		this.highLim = new DigitalInput(HIGHLIM_DIO);
		
		// Initialize timer
		this.lockTimer = 0;
	}
	
	/**
	 * Sets lift speeds based on driver input.
	 * 
	 * @param matt The Matt that is operating the lift
	 */
	public void control(MattDupuis matt)
	{
		System.out.println(this.getHeight());
		// Lift actions
		switch (matt.getLift())
		{
			case Raise:
				this.disengageLock();
				this.setLiftSpd(RAISE_SPD);
				break;
			case Lower:
				this.disengageLock();
				if (this.lockTimer >= UNLOCK_DELAY)
				{
					this.setLiftSpd(LOWER_SPD);
				}
				else
				{
					this.setLiftSpd(0d);
				}
				break;
			case None:
			default:
				this.engageLock();
				this.setLiftSpd(0d);
		}
	}
	
	/**
	 * Sets the lift motor speed; -1 means to lower at full speed and 1 means to
	 * raise at full speed.
	 * 
	 * @param spd The lift speed, between -1 and 1
	 */
	public void setLiftSpd(double spd)
	{
		// Clamp speeds to +/-100%
		spd = Math.min(Math.max(spd, -1d), 1d);
		
		// Consider limit switches
		if (
			(spd <= -DEADBAND && this.lowLim.get()) || 
			(spd >= DEADBAND && this.highLim.get())
		)
		{
			// Set motor speed
			this.lifty.set(spd);
		}
		else
		{
			this.lifty.set(0d);
		}
	}
	
	/**
	 * Sets the chain lock to the locked position.
	 */
	public void engageLock()
	{
		this.lock.set(LOCKED_POS);
		this.lockTimer = 0;
	}
	
	/**
	 * Sets the chain lock to the unlocked position. Make sure that the chain is
	 * fully unlocked before moving the chain downwards.
	 */
	public void disengageLock()
	{
		this.lock.set(UNLOCKED_POS);
		this.lockTimer += 1;
	}
	
	/**
	 * Calculates an approximate height from the bottom of a grabbed cube to the
	 * floor based on encoder values.
	 * 
	 * @return The approximate height of a cube, in inches
	 */
	public double getHeight()
	{
		return Math.max(ENC_IPR * -this.liftEnc.getDistance(), 0);
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
	
	/**
	 * Zeroes encoder.
	 */
	public void zeroSensors()
	{
		this.liftEnc.reset();
	}
}