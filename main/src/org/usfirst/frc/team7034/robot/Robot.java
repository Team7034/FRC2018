package org.usfirst.frc.team7034.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	PWMTalonSRX front_left;
	PWMTalonSRX back_left;
	SpeedControllerGroup left_motors;
	
	PWMTalonSRX front_right;
	PWMTalonSRX back_right;
	SpeedControllerGroup right_motors;
	
	DifferentialDrive robot;
	Joystick stick;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		front_left = new PWMTalonSRX(0);
		back_left = new PWMTalonSRX(1);
		left_motors = new SpeedControllerGroup(front_left, back_left);
		
		front_right = new PWMTalonSRX(2);
		back_right = new PWMTalonSRX(3);
		right_motors = new SpeedControllerGroup(front_right, back_right);
		
		robot = new DifferentialDrive(left_motors, right_motors);
		stick = new Joystick(0);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		robot.arcadeDrive(stick.getY(), stick.getX());
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

