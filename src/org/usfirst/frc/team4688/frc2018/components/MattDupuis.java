// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018.components;

import edu.wpi.first.wpilibj.*;

/**
 * Reads and processes input from the robot driver (after whom this class is
 * honourably named) through both the main Xbox controller and the button board.
 * 
 * @author Jacob
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
	private final int INTAKE_DAXIS = 3;
	
	// Driver button indices
	private final int EJECT_DBTN = 2;
	private final int RAISE_DBTN = 6;
	private final int LOWER_DBTN = 5;
	
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
	
	/**
	 * Gets the intake axis between -1 and 1; 1 means full eject speed, 0 means
	 * no motion, and -1 means full intake speed.
	 * 
	 * @return The intake axis, between -1 and 1
	 */
	public double getIntake()
	{
		boolean reverse = this.driver.getRawButton(EJECT_DBTN);
		return this.driver.getRawAxis(INTAKE_DAXIS) * (reverse ? -1d : 1d);
	}
	
	/**
	 * Enumeration of possible hugger tilt actions.
	 */
	public static enum Tilt
	{
		None, Up, Down
	}
	
	/**
	 * Returns what action is currently being performed with regards to the
	 * hugger tilt.
	 * 
	 * @return Whether the hugger should tilt up, down, or not at all
	 */
	public Tilt getTilt()
	{
		// Get DPad value
		int dpad = this.driver.getPOV();
		
		// If pressing DPad up, tilt up
		if (dpad == 0) return Tilt.Up;
		
		// If pressing DPad down, tilt down
		else if (dpad == 180) return Tilt.Down;
				
		// If pressing anywhere else, do not tilt
		return Tilt.None;
	}
	
	/**
	 * Enumeration of possible lift actions.
	 */
	public static enum Lift
	{
		None, Raise, Lower
	}
	
	/**
	 * Returns what action is currently being performed with the lift.
	 * 
	 * @return Whether the lift is being raised, being lowered, or not moving
	 */
	public Lift getLift()
	{
		// Check raise/lower buttons
		boolean raise = this.driver.getRawButton(RAISE_DBTN);
		boolean lower = this.driver.getRawButton(LOWER_DBTN);
		
		// If only one of the buttons are being pressed, return the action
		if (raise && !lower) return Lift.Raise;
		if (lower && !raise) return Lift.Lower;
		
		// Otherwise, do nothing
		return Lift.None;
	}
}