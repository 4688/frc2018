// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018.components;

import edu.wpi.first.wpilibj.*;

/**
 * Reads and processes input from the robot driver (after whom this class is
 * honourably named) through both the main Xbox controller and the button board.
 */
public class MattDupuis
{
	// Xbox controller and button board USB ports
	private final int DRIVER_USB = 0;
	private final int BOARD_USB = 1;
	
	// Driver axis indices
	private final int FORWARD_DAXIS = 1;
	private final int TURN_DAXIS = 4;
	private final int TURBO_DAXIS = 2;
	
	// Xbox controller and button board
	private Joystick driver, board;
	
	/**
	 * Constructor.
	 */
	public MattDupuis()
	{
		// Initialize joysticks
		this.driver = new Joystick(DRIVER_USB);
		this.board = new Joystick(BOARD_USB);
	}
	
	/**
	 * Gets the inverted forward axis between -1 and 1; 1 means 100% throttle
	 * forward, 0 means no forward motion, and -1 means 100% throttle backwards.
	 * 
	 * @return The inverted forward axis, between -1 and 1
	 */
	public double getForward()
	{
		return -this.driver.getRawAxis(FORWARD_DAXIS);
	}
	
	/**
	 * Gets the turning axis between -1 and 1; 1 means a full speed left turn, 0
	 * means no turning motion, and -1 means a full speed right turn.
	 * 
	 * @return The turning axis, between -1 and 1
	 */
	public double getTurn()
	{
		return this.driver.getRawAxis(TURN_DAXIS);
	}
	
	/**
	 * Gets the turbo axis between 0 and 1; 1 means full turbo and 0 means no
	 * turbo (regular speed).
	 * 
	 * @return The turbo axis, between 0 and 1
	 */
	public double getTurbo()
	{
		return this.driver.getRawAxis(TURBO_DAXIS);
	}
}