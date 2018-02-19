package org.usfirst.frc.team7034.robot;

import edu.wpi.first.wpilibj.DriverStation;
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

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.usfirst.frc.team7034.robot.Controller;

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
public class Robot extends IterativeRobot {	
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
	
	//winch
	WPI_TalonSRX winch_one;
	WPI_TalonSRX winch_two;
	SpeedControllerGroup winch_motors;
	
	float winch_power = 0;
	boolean winch_disabled = false;
	
	//controllers
	Joystick stick;
	Controller cont;
	
	//sensors
	AHRS gyro;
	
	//pneumatics
	Compressor compressor;
	DoubleSolenoid mainPiston;
	DoubleSolenoid secondaryPiston;
	
	//pid
	PIDController PIDControl;

	//misc
	SmartDashboard dash;
	boolean manual = true;
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
		
		//winch
		winch_one = new WPI_TalonSRX(2);
		winch_two = new WPI_TalonSRX(3);
		
		winch_one.configOpenloopRamp(2.0, 200);
		winch_one.configClosedloopRamp(0.8,200);
		winch_two.configOpenloopRamp(2.0, 200);
		winch_two.configClosedloopRamp(0.8,200);
		
		winch_motors = new SpeedControllerGroup(winch_one, winch_two);
		
		//controllers
		cont = new Controller(0);
		stick = new Joystick(1);
		stick.setThrottleChannel(3);
		
		//sensors
		try {
			gyro = new AHRS(SerialPort.Port.kUSB);
		} catch(RuntimeException e) {
			DriverStation.reportError("Error Instantiating the NavX Micro: " + e.getMessage(), true);
		}
		//pid
		
		PIDControl = new PIDController(0.025,0.025,0.025,0.025, gyro, arm);
		PIDControl.setOutputRange(-0.01, 0.6);
		gyro.reset(); //sets start angle to 0
		//PIDControl.setSetpoint(gyro.getAngle()+90); //sets target angle to 90
		
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
	/**	
		if (cont.getYB())
		{
			robot.arcadeDrive(0,0);
		}
		
		 int totalDisplacement = 0;
		 if (totalDisplacement < 12)
		  {
		  	navX.resetDisplacement();
		   robot.arcadeDrive(-0.35, 0.35);
		  	totalDisplacement += navX.getDisplacementX();
		  }
		  else if (totalDisplacement >= 12)
		  {
		  	robot.arcadeDrive(0,0);
		  }
		  
		  */
		 /**
		String gameMessage = ds.getGameSpecificMessage();
		
		if (gameMessage.equalsIgnoreCase("RBB") || gameMessage.equalsIgnoreCase("RRB");
		
	*/	 
	}
 
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		//update sensor values
			
		double angle = gyro.getAngle();
		double rate = gyro.getRate();
		double angle2 = gyro.getPitch();
		angle2 = angle2 + 90;
		double rate2 = gyro.getRawGyroX();
		double arm_power = .8*-cont.getRY();
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
			PIDControl.setSetpoint(gyro.getAngle());
			PIDControl.enable();
		}
		else
			PIDControl.disable();
		
		if (PIDControl.isEnabled())
		{
			arm.set(PIDControl.get());
		}
		
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
		
		//end affecter
		if(manual) {
			end_affecter.set(cont.getLY());
		}
		else {
			end_affecter.set(0);
		}
		
		//winch
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
		
		if (cont.getRB()) {		//run winch with set power when right bumper held
			winch_motors.set(winch_power);
		}
		else {
			winch_motors.set(0);
		}
		//old winch code
		/*if(cont.getRY() < 0)
		 {
			 if(cont.getLB())
			 winchControl.set(0.5 * cont.getRY());
			 else
		     winchControl.set(0.5 * cont.getRY());
				 
		 }
		 if(cont.getRB())
		 {
			 winchControl.set(0.25*Math.abs(cont.getLY()));
		 }*/
		
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
		dash.putNumber("angle2",  angle2);
		dash.putNumber("rate2", rate2);
		dash.putNumber("speed",  speed);
		dash.putNumber("sppedjoy", speed-cont.getRY());
		dash.putNumber("power",  winch_power);
		dash.putNumber("arm power", arm_power);
		dash.putBoolean("manual arm", manual);
	}
		
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
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
