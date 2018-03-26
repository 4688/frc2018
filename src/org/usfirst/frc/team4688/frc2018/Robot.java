// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018;

import org.usfirst.frc.team4688.frc2018.components.*;
import edu.wpi.first.wpilibj.*;

/**
 * Manages and represents the robot components operating as a whole.
 * 
 * @author Jacob
 */
public class Robot extends IterativeRobot
{
	// Components
	private Dashboard dashboard;
	private MattDupuis matt;
	private DriveTrain drive;
	private Hugger hugger;
	
	/**
	 * This method is called once each time the robot starts up. Here it
	 * initializes each of the individual components.
	 */
	public void robotInit()
	{
		this.dashboard = new Dashboard("SaintsBotDS");
		this.matt = new MattDupuis();
		this.drive = new DriveTrain();
		this.hugger = new Hugger();
	}
	
	/**
	 * This method is called 50 times per second while the robot is on,
	 * regardless of operating mode.
	 */
	public void robotPeriodic()
	{
		// Continuously update match/robot info
		this.dashboard.updateMatch();
		
		// Continuously update component info
		this.dashboard.updateMatt(this.matt);
		this.dashboard.updateDrive(this.drive);
		this.dashboard.updateHugger(this.hugger);
	}
	
	/**
	 * This method is called once each time the robot enters a disabled state.
	 */
	public void disabledInit()
	{
		this.dashboard.updateMode(Dashboard.Mode.Disabled);
	}
	
	/**
	 * This method is called once each time the robot enters Teleop mode.
	 */
	public void teleopInit()
	{
		this.dashboard.updateMode(Dashboard.Mode.Teleop);
	}
	
	/**
	 * This method is called 50 times per second while the robot is in Teleop
	 * mode.
	 */
	public void teleopPeriodic()
	{
		// Control components
		this.drive.control(this.matt);
		this.hugger.control(this.matt);
	}
	
	/**
	 * This method is called once each time the robot enters Autonomous mode.
	 */
	public void autonomousInit()
	{
		this.dashboard.updateMode(Dashboard.Mode.Auto);
	}
	
	/**
	 * This method is called once each time the robot enters Test mode.
	 */
	public void testInit()
	{
		this.dashboard.updateMode(Dashboard.Mode.Test);
	}
}