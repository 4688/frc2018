// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018.components;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;

/**
 * Manages deploying and retracting the climber.
 * 
 * @author Jacob
 */
public class Climber
{
	// Motor speed for cutting the string
	private final double CUTTER_SPD = 1d;
	
	// Motor speed for climbing
	private final double CLIMB_SPD = 1d;
	
	// Iterations taken to to cut the release string, in 1/50ths of a second
	private final int DEPLOY_TIME = 25;
	
	// Climb motor CAN index (both motors on same controller)
	private final int CLIMBER_CAN = 7;
	
	// Climb motors
	private VictorSPX reacher;
	
	// Iterations since deploy (1/50ths of a second)
	private int deployTimer;
	
	// Climber deploy tracker (true if deployed)
	private boolean deployed;
	
	/**
	 * Constructor.
	 */
	public Climber()
	{
		// Climb motors (aptly named by Scott)
		this.reacher = new VictorSPX(CLIMBER_CAN);
		
		// Deploy tracking things
		this.deployTimer = 0;
		this.deployed = false;
	}
	
	/**
	 * Operates the climber based on button input. Note that any climb inputs
	 * are suppressed before 30 seconds remaining in the match.
	 * 
	 * @param matt The Matt operating the robot
	 */
	public void control(MattDupuis matt, Lift lift)
	{
		// Climber action
		MattDupuis.Climber climb = matt.getClimber();
		
		// If the robot is being deployed, set the deploy timer
		if (climb == MattDupuis.Climber.Deploy && this.deployTimer <= 0 && lift.getLowLim())
		{
			this.deployTimer = DEPLOY_TIME;
		}
		
		// If the robot is currently being deployed:
		if (this.deployTimer > 0 && !this.deployed)
		{
			// Set motor speed to cut the release string
			this.reacher.set(ControlMode.PercentOutput, CUTTER_SPD);
			
			// Finish deploying
			if (this.deployTimer <= 0) this.deployed = true;
			
			// Decrement timer
			this.deployTimer -= 1;
		}
		// If the robot is retracting the climber and is finished deploying:
		else if (climb == MattDupuis.Climber.Climb && this.deployed)
		{
			this.reacher.set(ControlMode.PercentOutput, CLIMB_SPD);
		}
		else
		{
			this.reacher.set(ControlMode.PercentOutput, 0d);
		}
	}
	
	/**
	 * Returns whether the climber is deployed.
	 * 
	 * @return True if the climber is deployed, false otherwise
	 */
	public boolean isDeployed()
	{
		return this.deployed;
	}
}