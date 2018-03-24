package org.usfirst.frc.team7034.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.DriverStation;


import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.AHRS.SerialDataType;

import edu.wpi.first.wpilibj.PIDController;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory. The past tense of Yeet is Yate. Not Yote. That's just wrong in so many ways. 
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
	
	//timer
	Timer timer;
	
	
	//arm
	Spark arm;
	Spark end_affecter;
	PIDController pid;
	Encoder enc;
	

	//winch
	WPI_TalonSRX winch_one;
	WPI_TalonSRX winch_two;
	SpeedControllerGroup winch_motors;
	
	boolean winch_disabled = false;

	
	//controllers
	Joystick stick;
	Controller cont;
	//Controller cont2;
	
	//sensors
	AHRS gyro;
	//AHRS navX;
	
	//pneumatics
	Compressor compressor;
	DoubleSolenoid mainPiston;
	DoubleSolenoid secondaryPiston;
	
	//pid
	//double gyroInit = 0;
	//PIDController PIDControl;
	//PIDController autoPID;

	//misc
	SmartDashboard dash;
	boolean manual = false;
	boolean reverse = false;
	boolean firstTime = true;
	DigitalInput switch1;
	
	DriverStation ds;
	
	double armPower;
	String gameData;
	
	//AnalogInput camera;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		CameraServer.getInstance().startAutomaticCapture();
		
		//camera = new AnalogInput(3);
		
		//arm power
		armPower = 0;
		
		//drive
		front_left = new Spark(0);
		back_left = new Spark(1);
		left_motors = new SpeedControllerGroup(front_left, back_left);
		
		front_right = new Spark(2);
		back_right = new Spark(3);
		right_motors = new SpeedControllerGroup(front_right, back_right);
		
		robot = new DifferentialDrive(left_motors, right_motors);
		
		//arm
		arm = new Spark(5);
		end_affecter = new Spark(4);
		//enc = new Encoder(0,1);
		//enc.reset();
		
		//winch 
		
		winch_one = new WPI_TalonSRX(1);
		winch_two = new WPI_TalonSRX(2);

		//winch_one.configOpenloopRamp(2.0, 200);
		//winch_one.configClosedloopRamp(0.8,200);
		//winch_two.configOpenloopRamp(2.0, 200);
		//winch_two.configClosedloopRamp(0.8,200);
		
		winch_motors = new SpeedControllerGroup(winch_one, winch_two);
		
		//controllers
		cont = new Controller(0);
		//cont2 = new Controller(2);
		stick = new Joystick(1);
		stick.setThrottleChannel(3);
		
		//sensors
		
		//navX = new AHRS(I2C.Port.kMXP);
			
		//try {
		//gyro = new AHRS(SerialPort.Port.kUSB);
		//} catch(RuntimeException e) {
		//	DriverStation.reportError("Error Instantiating the NavX Micro: " + e.getMessage(), true);
		//}
		
		//pid
		
		//PIDControl = new PIDController(0.025,0.025,0.025,0.025, gyro, arm);
		//PIDControl.setOutputRange(-0.01, 0.6);
		//gyro.reset(); //sets start angle to 0
		//PIDControl.setSetpoint(gyro.getAngle()+90); //sets target angle to 90
		//gyroInit = gyro.getRoll();
		
		//autoPID = new PIDController(0.025,0.025,0.025,0.025, navX, right_motors);
		//autoPID.setOutputRange(-1,1);
		//navX.reset(); //sets start angle to 0;
		//autoPID.setSetpoint(navX.getAngle()-90); //sets target angle 90 degrees to the left
		
		
		
		//pneumatics
		compressor = new Compressor(0);
		compressor.setClosedLoopControl(true);
		
		mainPiston = new DoubleSolenoid(0,1);
		secondaryPiston = new DoubleSolenoid(2,3);
		
		mainPiston.set(DoubleSolenoid.Value.kReverse);
		secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		
		//misc
		dash = new SmartDashboard(); 
		armPower = 0;
		timer = new Timer();
		switch1 = new DigitalInput(2);

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
		
		timer.reset();
		//timer.start();
		firstTime = true;
		gameData = ds.getInstance().getGameSpecificMessage();
		
	}

		/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
	
	if(gameData.length() < 3) {	//in case of network lag, other connection issues
		gameData = ds.getInstance().getGameSpecificMessage();
	}
		
	if(firstTime) {
		firstTime = false;
		timer.start();
	}
	if(gameData.length() > 0) {
		if(gameData.charAt(0) == 'L') {	//switch is on left side
			if(switch1.get()) {			//we start left side
				//drive and throw cube
				if(timer.get() <= 0.750) {
					robot.arcadeDrive(1, 0);
				}else if(timer.get() <= 0.900) {
					robot.arcadeDrive(0.7, 0);
					mainPiston.set(DoubleSolenoid.Value.kForward);
					secondaryPiston.set(DoubleSolenoid.Value.kForward);
				}else if(timer.get() <= 1.600) {
					robot.arcadeDrive(0, 0);
				}else if(timer.get() <= 2.350) {
					robot.arcadeDrive(-1, 0);
					mainPiston.set(DoubleSolenoid.Value.kReverse);
					secondaryPiston.set(DoubleSolenoid.Value.kReverse);
				}else {
					robot.arcadeDrive(0, 0);
				}
			}else {						//we start right side (and switch is left)
				//drive and come back
				if(timer.get() <= 0.750) {
					robot.arcadeDrive(1, 0);
				}else if(timer.get() <= 0.900) {
					robot.arcadeDrive(0.7, 0);
				}else if(timer.get() <= 1.200) {
					robot.arcadeDrive(0, 0);
				}else if(timer.get() <= 1.950) {
					robot.arcadeDrive(-1, 0);
				}else {
					robot.arcadeDrive(0, 0);
				}
			}
		}
		else {							//switch is on the right side
			if(switch1.get()) {			//we are on left side
				//drive and come back
				if(timer.get() <= 0.750) {
					robot.arcadeDrive(1, 0);
				}else if(timer.get() <= 0.900) {
					robot.arcadeDrive(0.7, 0);
				}else if(timer.get() <= 1.200) {
					robot.arcadeDrive(0, 0);
				}else if(timer.get() <= 1.950) {
					robot.arcadeDrive(-1, 0);
				}else {
					robot.arcadeDrive(0, 0);
				}
			}else {						//we are on right side (and switch is right)
				//drive and throw cube
				if(timer.get() <= 0.750) {
					robot.arcadeDrive(1, 0);
				}else if(timer.get() <= 0.900) {
					robot.arcadeDrive(0.7, 0);
					mainPiston.set(DoubleSolenoid.Value.kForward);
					secondaryPiston.set(DoubleSolenoid.Value.kForward);
				}else if(timer.get() <= 1.200) {
					robot.arcadeDrive(0, 0);
				}else if(timer.get() <= 1.950) {
					robot.arcadeDrive(-1, 0);
					mainPiston.set(DoubleSolenoid.Value.kReverse);
					secondaryPiston.set(DoubleSolenoid.Value.kReverse);
				}else {
					robot.arcadeDrive(0, 0);
				}
			}
		}
	}
	
	dash.putNumber("timer time", timer.get());
		
	/*if (timer.get() > 10.000 && timer.get() < 11.500)	 
	 	robot.arcadeDrive(-.85,0);
	*/
	
	}
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		//check to see if arm is in manual control mode && slow mode
		if (cont.getStart()) {
			manual = true;
		}else if (cont.getBack()) {
			manual = false;
		}
		
		
		if(stick.getRawButton(11)){
			winch_motors.set(-1);
		}
		else if(stick.getRawButton(7)) {
			winch_motors.set(.15);
		}
		else {
			winch_motors.set(0);;
		}

		//drive
		double speed = ((stick.getThrottle()+1)/2);
		robot.arcadeDrive(-stick.getY(), stick.getX());
	
		end_affecter.set(cont.getLY());
		
		if(manual) {
			arm.set(-speed);
		}
		
		//driver-aid buttons
		
		if (cont.getB() && !manual) //collapses pneumatics and holds arm locked up for CLIMBING
		{
			arm.set(.25); //driver controls arm automatically in reverse 
			//armPower = .3;
			mainPiston.set(DoubleSolenoid.Value.kReverse);
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
			try {
				Thread.sleep(450);
			}catch(InterruptedException e) {
			}
			arm.set(0);
			//armPower = 0;
			
		}
		
		
		if (cont.getA() && !manual) //left bumper button dumps a cube then returns arm to normal position
		{
			mainPiston.set(DoubleSolenoid.Value.kForward);
			secondaryPiston.set(DoubleSolenoid.Value.kForward);
			try { 
				Thread.sleep(1250);
			}catch(InterruptedException e) {
			
			}
			mainPiston.set(DoubleSolenoid.Value.kReverse);
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		}
		
		if (cont.getYB() && !manual) //goes to half stage, then all the way up
		{
			mainPiston.set(DoubleSolenoid.Value.kForward);
			
			try { 
				Thread.sleep(300);
			}catch(InterruptedException e) {}
			
			arm.set(-.33);

			try { 
				Thread.sleep(1000);
			}catch(InterruptedException e) {}
			
			secondaryPiston.set(DoubleSolenoid.Value.kForward);
			
			try { 
				Thread.sleep(500);
			}catch(InterruptedException e) {}
			
			arm.set(-.6);

			try { 
				Thread.sleep(500);
			}catch(InterruptedException e) {}
			
			arm.set(-.1);
		}
		
		if (cont.getLB()){
			arm.set(-.22);
		}else if(cont.getRB()) {
			arm.set(.2);
		}else if (cont.getRT() > 0.5) {
			arm.set(0);
		}
		
		//send info to dash
		
		dash.putBoolean("joy7", stick.getRawButton(7));
		dash.putBoolean("joy12", stick.getRawButton(12));
		dash.putNumber("right trigger", cont.getRT());
		dash.putBoolean("switch", switch1.get());
		//dash.putNumber("camera", camera.getVoltage());
		
		}
	private double limit(double d) {
		if(d>1)
			return 1;
		else if(d<-1)
			return -1;
		else 
			return 0;
	}
	
	private void turn(char dir) {
		double angle_ = gyro.getAngle();
		if(dir=='r') {
			double target = angle_ + 90;
			while(gyro.getAngle() != target ) {
				double power = .01 * gyro.getAngle() - target;
				left_motors.set(power);
				right_motors.set(-power);
			}
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	
	}
/*	
	private void disWinch() {
		winch_disabled = true;
		try {
			Thread.sleep(100);
		}catch(InterruptedException e) {
			
		}
		winch_disabled = false;
	
	}
	*/
}
