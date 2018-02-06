// JACOB CAZABON <3 SAINTS BOT 2018

package org.usfirst.frc.team4688.frc2018;

import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot
{
	private Dashboard dashboard;
	
	public void robotInit()
	{
		this.dashboard = new Dashboard();
	}
	
	private class Dashboard
	{
		NetworkTable table;
		
		public Dashboard()
		{
			this.table = NetworkTableInstance.getDefault().getTable("SaintsBotDS");
		}
	}
}