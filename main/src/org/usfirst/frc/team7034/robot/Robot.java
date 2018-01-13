package org.usfirst.frc.team7034.robot;


public class Robot{
	static vision cam = new vision();
	public static void main(String[] args) {
		cam.test();
	}
}


/*
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

public class Robot extends IterativeRobot {
	
	PWMTalonSRX front_left;
	PWMTalonSRX back_left;
	SpeedControllerGroup left_motors;
	
	PWMTalonSRX front_right;
	PWMTalonSRX back_right;
	SpeedControllerGroup right_motors;
	
	DifferentialDrive robot;
	Joystick stick;

	//when robot starts, init code
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

	//init code before auto
	@Override
	public void autonomousInit() {
	}

	//called periodically in auto
	@Override
	public void autonomousPeriodic() {
	}

	//called periodically in tele
	@Override
	public void teleopPeriodic() {
		robot.arcadeDrive(stick.getY(), stick.getX());
	}

	//called periodic in test
	@Override
	public void testPeriodic() {
	}
}*/

