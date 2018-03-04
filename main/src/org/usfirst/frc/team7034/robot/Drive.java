


package org.usfirst.frc.team7034.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.I2C;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.AHRS.SerialDataType;

import edu.wpi.first.wpilibj.PIDController;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Drive extends IterativeRobot {	
	//drive
	Spark front_left;
	Spark back_left;	
	Spark front_right;
	Spark back_right;
	SpeedControllerGroup left_motors;
	SpeedControllerGroup right_motors;
	DifferentialDrive robot;
	
	//arm
	Spark arm;
	Spark end_affecter;
	PIDController pid;
	Encoder enc;
	
	//winch
	//WPI_TalonSRX winch_one;
	//WPI_TalonSRX winch_two;
	//SpeedControllerGroup winch_motors;
	
	float winch_power = 0;
	boolean winch_disabled = false;
	
	//controllers
	Joystick stick;
	Controller cont;
	
	//sensors
	AHRS gyro;
	AHRS navX;
	
	//pneumatics
	Compressor compressor;
	DoubleSolenoid mainPiston;
	DoubleSolenoid secondaryPiston;
	
	//pid
	PIDController PIDControl;
	PIDController autoPID;

	//misc
	SmartDashboard dash;
	boolean manual = true;
	
	DriverStation ds;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//drive
		front_left = new Spark(0);
		back_left = new Spark(1);
		left_motors = new SpeedControllerGroup(front_left, back_left);
		
		front_right = new Spark(2);
		back_right = new Spark(3);
		right_motors = new SpeedControllerGroup(front_right, back_right);
		
		robot = new DifferentialDrive(left_motors, right_motors);
		
		//arm
		arm = new Spark(4);
		end_affecter = new Spark(5);
		enc = new Encoder(0,1);
		
		//winch 
		/*
		winch_one = new WPI_TalonSRX(0);
		winch_two = new WPI_TalonSRX(1);
		
		//winch_one.configOpenloopRamp(2.0, 200);
		//winch_one.configClosedloopRamp(0.8,200);
		//winch_two.configOpenloopRamp(2.0, 200);
		//winch_two.configClosedloopRamp(0.8,200);
		
		winch_motors = new SpeedControllerGroup(winch_one, winch_two); */
		
		//controllers
		cont = new Controller(0);
		stick = new Joystick(1);
		stick.setThrottleChannel(3);
		
		//sensors
		
		navX = new AHRS(I2C.Port.kMXP);
		
		
		try {
			gyro = new AHRS(SerialPort.Port.kUSB);
		} catch(RuntimeException e) {
			DriverStation.reportError("Error Instantiating the NavX Micro: " + e.getMessage(), true);
		}
		
		//pid
		
		PIDControl = new PIDController(0.025,0.025,0.025,0.025, gyro, arm);
		PIDControl.setOutputRange(-0.01, 0.6);
		gyro.reset(); //sets start angle to 0
		PIDControl.setSetpoint(gyro.getAngle()+90); //sets target angle to 90
		
		autoPID = new PIDController(0.025,0.025,0.025,0.025, navX, right_motors);
		autoPID.setOutputRange(-1,1);
		navX.reset(); //sets start angle to 0;
		autoPID.setSetpoint(navX.getAngle()-90); //sets target angle 90 degrees to the left
		
		
		
		//pneumatics
		compressor = new Compressor(0);
		compressor.setClosedLoopControl(true);
		
		mainPiston = new DoubleSolenoid(0,1);
		mainPiston.set(DoubleSolenoid.Value.kOff);
		secondaryPiston = new DoubleSolenoid(2,3);
		secondaryPiston.set(DoubleSolenoid.Value.kOff);
		
		//misc
		dash = new SmartDashboard(); 

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
		
		String gameMessage = null;
		
		boolean completed = false;
		
		//Relevant note:
		// 
		//This code assumes you are starting on the right most position at the beginning of the match
		//
	
		if (!completed)
		{	
			try {
				gameMessage = ds.getGameSpecificMessage();
			}
			catch (NullPointerException e)
			{
				
				goForward(3);
				
				autoPID.enable();
				
				while(navX.getAngle() != -90)
				{
				right_motors.set(autoPID.get());
				left_motors.set(-autoPID.get());
				}
				
				autoPID.disable();
					
				completed = true; 
		
			}
			if (gameMessage != null)
			{
			if (gameMessage.charAt(0) == 'R') //only works right if starting on right side
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
		}	 
	}
	}
	
	public void goForward(int meters)
	{
		int displacement = 0;
		navX.resetDisplacement();
		while(displacement < meters)
		{
			robot.arcadeDrive(0.35, 0.35);
			displacement = (int) navX.getDisplacementX();
		}
		
		robot.arcadeDrive(0,0);
		return;
	}
 
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		
		//update sensor values
		
		double pidval = 0;
		double angle = gyro.getAngle();
		double rate = gyro.getRate();
		double angle2 = gyro.getPitch();
		angle2 = angle2 + 90;
		double rate2 = gyro.getRawGyroX();
		double arm_power = .8*-cont.getRY();
		double navXAngle = navX.getAngle();
		
		//check to see if arm is in manual control mode
		if (cont.getStart()) {
			manual = true;
		}
		else if (cont.getBack()) {
			manual = false;
		}
		
		
		//drive
		double speed = ((stick.getThrottle()+1)/2);
		robot.arcadeDrive(stick.getY()*speed, stick.getX()*speed);
	
		
		//PID arm
		
		PIDControl.setSetpoint(0);
		
		if (!manual)
		{
			PIDControl.enable();
		}
		else {
			PIDControl.disable();
			arm.set(-speed);
		}
		if (PIDControl.isEnabled())
		{
			//arm.set(PIDControl.get());
			pidval = PIDControl.get();
		}
		
		/*
		//arm
		if(!manual && angle2 >= 0) {	//hold arm in place with gyro.
			arm.set(speed*angle2/90);
		}
		else if (!manual && angle2 < 0) {
			arm.set(speed*angle2/180);
		}
		else if (manual) {
			arm.set(speed-cont.getRY());
		}
		*/
		
		//end affecter
		if(manual) {
			end_affecter.set(1/8*(cont.getLY()));
		}
		else {
			end_affecter.set(0);
		}
	
		//winch
		/*
		if(cont.getDPAD("up")) {	//up and down dpad change speed
			if(winch_power < 1 && !winch_disabled) {
				winch_power += .1;
				disWinch();
			}
		}
		else if(cont.getDPAD("down")) {
			if(winch_power > -1 && !winch_disabled) {
				winch_power -= .1;
				disWinch();
			}
		}
		if(winch_power > 1) {
			winch_power = 1;
		}
		else if(winch_power < -1) {
			winch_power = -1;
		}
		*/
		//pneumatics
		
		if (cont.getB())	//B = down
		{
			mainPiston.set(DoubleSolenoid.Value.kReverse);
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		}
		else if (cont.getXB())	//X = half
		{
			mainPiston.set(DoubleSolenoid.Value.kForward);
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		}
		else if (cont.getA()) {		//A = up
			mainPiston.set(DoubleSolenoid.Value.kForward);
			secondaryPiston.set(DoubleSolenoid.Value.kForward);
		}
		else if (cont.getDPAD("left")) {
			secondaryPiston.set(DoubleSolenoid.Value.kForward);;
			try {
				Thread.sleep(1500);
			}catch(InterruptedException e) {
				
			}
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		}
		
		//send info to dashboard
		
		dash.putNumber("angle",  angle);
		dash.putNumber("rate", rate);
		dash.putNumber("encRate",  enc.get());
		dash.putNumber("speed",  speed);
		dash.putNumber("pidval", pidval);
		dash.putNumber("power",  winch_power);
		dash.putNumber("arm power", arm_power);
		dash.putBoolean("manual arm", manual);
		dash.putNumber("navX angle", navXAngle);

		
	}
		
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		
		goForward(3);
		
	}
	
	private void disWinch() {
		winch_disabled = true;
		try {
			Thread.sleep(100);
		}catch(InterruptedException e) {
			
		}
		winch_disabled = false;
	
	}
}

