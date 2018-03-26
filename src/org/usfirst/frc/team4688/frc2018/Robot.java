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
	
	/**
	 * This method is called once each time the robot starts up.
	 */
	public void robotInit()
	{
		this.dashboard = new Dashboard("SaintsBotDS");
	}
}