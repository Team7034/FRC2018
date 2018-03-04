package org.usfirst.frc.team7034.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

public class Robot extends IterativeRobot {
	//drive
		Spark front_left;
		Spark back_left;	
		Spark front_right;
		Spark back_right;
		SpeedControllerGroup left_motors;
		SpeedControllerGroup right_motors;
		DifferentialDrive robot;
		
		Joystick stick;
		
		public void robotInit()
		{
	
			front_left = new Spark(0);
			back_left = new Spark(1);
			left_motors = new SpeedControllerGroup(front_left, back_left);
			
			front_right = new Spark(2);
			back_right = new Spark(3);
			right_motors = new SpeedControllerGroup(front_right, back_right);
			
			robot = new DifferentialDrive(left_motors, right_motors);
			
			stick = new Joystick(0);
			stick.setThrottleChannel(3);
		}
		
		public void autonomousInit()
		{
			
			
		}
		
		public void autonomousPeriodic()
		{
		}
		
		public void teleopPeriodic()
		{
			//drive
			double speed = ((stick.getThrottle()+1)/2);
			robot.arcadeDrive(stick.getY() * speed, -stick.getX() * speed);
			
//			left_motors.set(Math.sqrt(stick.getX()*Math.abs(stick.getX()) + stick.getY()*Math.abs(stick.getY())));
//			right_motors.set(-Math.sqrt(stick.getX()*stick.getX() + stick.getY()*stick.getY()));
			
			
		}
		
		public void testPeriodic()
		{
			
		}
}