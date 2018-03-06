package org.usfirst.frc.team7034.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

public class Drive extends IterativeRobot {
	//drive
		Spark front_left;
		Spark back_left;	
		Spark front_right;
		Spark back_right;
		SpeedControllerGroup left_motors;
		SpeedControllerGroup right_motors;
		DifferentialDrive robot;
		
		Joystick stick;
		
		//sensors
		//AHRS gyro;
		AHRS navX;
		
		//pid
		PIDController PIDControl;
		PIDController autoPID;
		
		//driverstation
		DriverStation ds;
		SmartDashboard dash;
		
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
			
			navX = new AHRS(I2C.Port.kMXP);
			
			//pid
			/*
			PIDControl = new PIDController(0.025,0.025,0.025,0.025, gyro, arm);
			PIDControl.setOutputRange(-0.01, 0.6);
			gyro.reset(); //sets start angle to 0
			PIDControl.setSetpoint(gyro.getAngle()+90); //sets target angle to 90
			*/
			
			autoPID = new PIDController(0.025,0.025,0.025,0.025, navX, right_motors);
			autoPID.setOutputRange(-1,1);
			navX.reset(); //sets start angle to 0;
			autoPID.setSetpoint(navX.getAngle()-90); //sets target angle 90 degrees to the left
			
			dash = new SmartDashboard();
		}
		
	public void autonomousInit()
	{
			
			 
	}
	
	public void goForward(float meters)
	{
		float displacement = 0;
		navX.resetDisplacement();
		while(displacement < meters)
		{

			left_motors.set(-0.3);
			right_motors.set(0.32);
			displacement = navX.getDisplacementX();
			float vel = navX.getVelocityX();
			float ydisp = navX.getDisplacementY();
			dash.putNumber("navDisp",  displacement);
			dash.putNumber("vel", vel);
			dash.putNumber("y", ydisp);
		}
		
		left_motors.set(0);
		right_motors.set(0);
	}
		
	public void autonomousPeriodic()
	{
		String gameMessage = "yeet";
			
		boolean completed = false;
		double initAng = navX.getAngle();
			
		//Relevant note:
		// 
		//This code assumes you are starting on the right most position at the beginning of the match
		//
		
		if (!completed)
		{		
			/*
			gameMessage = ds.getGameSpecificMessage();
					
			else if (gameMessage.charAt(0) == 'R') //only works right if starting on right side
			{
				//left auto code here
					
				goForward(3);
					
				completed = true;
			}
			else
			{
				//right auto code here
				
				goForward(3);
				autoPID.enable();
				
				while(navX.getAngle() != -90)
				{
					right_motors.set(autoPID.get());
					left_motors.set(-autoPID.get());
				}
					
				autoPID.disable();
					
				goForward(3);
					
				completed = true;
			}
				*/
				
			//goForward(1);
				
			//left_motors.set(.3);
			double power = .3;
			double delta = .05 *(initAng - navX.getAngle());
			
			left_motors.set(-(power-delta));
			right_motors.set(power+delta);
			completed = true;
		}
	}
		
	public void teleopPeriodic()
	{
		//left_motors.set(0);
		//right_motors.set(0);
		//drive
		double speed = ((stick.getThrottle()+1)/2);
		dash.putNumber("sped",  speed);
		robot.arcadeDrive(-stick.getY() * speed, stick.getX() * speed);
		dash.putNumber("x", stick.getX());
		dash.putNumber("y", stick.getY());
		
		//left_motors.set(Math.sqrt(stick.getX()*Math.abs(stick.getX()) + stick.getY()*Math.abs(stick.getY())));
		//right_motors.set(-Math.sqrt(stick.getX()*stick.getX() + stick.getY()*stick.getY()));
			
		
	}
		
	public void testPeriodic()
	{
		robot.arcadeDrive(0.35, 0.35);
	}
}