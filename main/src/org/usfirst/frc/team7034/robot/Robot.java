package org.usfirst.frc.team7034.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import java.lang.Math;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

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
	
	//timer
	Timer timer;
	
	//arm
	Spark arm;
	Spark end_affecter;

	//winch
	WPI_TalonSRX winch_one;
	WPI_TalonSRX winch_two;
	SpeedControllerGroup winch_motors;
	
	//controllers
	Joystick stick;
	Controller cont;
	Controller aCont;
	
	//sensors
	AHRS gyro;
	
	//pneumatics
	Compressor compressor;
	DoubleSolenoid mainPiston;
	DoubleSolenoid secondaryPiston;
	
	//pid
	PIDController leftPID;
	PIDController rightPID;

	//misc
	SmartDashboard dash;
	boolean reverse = false;
	boolean firstTime = true;
	boolean drivable = true;
	DigitalInput switch1;
	DigitalInput switch2;
	
	DriverStation ds;
	
	double armPower;
	double[] autoAng;
	String gameData;
	
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
		arm = new Spark(5);
		end_affecter = new Spark(4);
		
		//winch 
		
		winch_one = new WPI_TalonSRX(1);
		winch_two = new WPI_TalonSRX(2);
		
		winch_motors = new SpeedControllerGroup(winch_one, winch_two);
		
		//controllers
		cont = new Controller(0);
		stick = new Joystick(1);
		aCont = new Controller(2);
		stick.setThrottleChannel(3);
		
		//sensors
			
		try {
			gyro = new AHRS(SPI.Port.kMXP);
		} catch(RuntimeException e) {
			DriverStation.reportError("Error Instantiating the NavX Micro: " + e.getMessage(), true);
		}
		
		//pid
		
		leftPID = new PIDController(.039,1E-8,0.095,0, gyro, left_motors);
		rightPID = new PIDController(.039,1E-8,0.095,0, gyro, right_motors);
		//leftPID.setInputRange(-180, 180);
		//rightPID.setInputRange(-180, 180);
		//leftPID.setContinuous(true);
		//rightPID.setContinuous(true);
		
		
		//pneumatics
		compressor = new Compressor(0);
		compressor.setClosedLoopControl(true);
		
		mainPiston = new DoubleSolenoid(0,1);
		secondaryPiston = new DoubleSolenoid(2,3);
		
		mainPiston.set(DoubleSolenoid.Value.kReverse);
		secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		
		//misc
		dash = new SmartDashboard();
		switch1 = new DigitalInput(0);
		switch2 = new DigitalInput(1);
		autoAng = new double[2];
		new Thread(() -> {
			UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
			cam.setFPS(24);
			cam.setResolution(80, 60);
		}).start();
		
		dash.putNumber("turn",  .75);
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
		timer = new Timer();
		timer.reset();
		//timer.start();
		firstTime = true;
		gameData = ds.getInstance().getGameSpecificMessage();
		//send info to dash
		dash.putBoolean("Switch 1", switch1.get());
		dash.putBoolean("Switch 2",  switch2.get());
		dash.putNumber("pot", stick.getThrottle());
		
		//navx
		dash.putBoolean("Gyro Connected", gyro.isConnected());
		dash.putNumber("Gyro Angle", gyro.getAngle());
		
		//determine if reversing based on pot
		if(stick.getThrottle() <= 0) {
			reverse = false;
		}else {
			reverse = true;
		}
		
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
		double a_ = gyro.getAngle();
		if(switch1.get()) {				//we start center
			if(gameData.charAt(0) == 'L') {		//determine direction to go
				autoAng[0] = a_ - 45;
				autoAng[1] = a_;
			}else if(gameData.charAt(0) == 'R') {
				autoAng[0] = a_ + 45;
				autoAng[1] = a_;
			}
		}else {
			if(switch2.get()) {//left side
				if(gameData.charAt(0) == 'L') {
					autoAng[0] = a_ + 90;
					autoAng[1] = a_;
				}else if(gameData.charAt(0) == 'R') {
					autoAng[0] = a_;
					autoAng[1] = a_;
				}
			}else {				//right side
				if(gameData.charAt(0) == 'L') {
					autoAng[0] = a_;
					autoAng[1] = a_;
				}else if(gameData.charAt(0) == 'R') {
					autoAng[0] = a_ - 85;
					autoAng[1] = a_;
				}
			}
		}
		timer.start();
	}
	if(gameData.length() > 0) {
		if(switch1.get()) {		//we start center
			//drive and throw cube
			if(timer.get() <= 0.2) {
				PIDtoggle(false);
				if(drivable) {
					robot.arcadeDrive(-1, .33);//forward .2
				}
			}else if(timer.get() <= .9) {
				if(drivable) {
					robot.arcadeDrive(0, 0);//turn .7
				}
				turn(autoAng[0]);
				PIDtoggle(true);
			}else if(timer.get() <= 1.65) {
				PIDtoggle(false);
				if(drivable) {
					robot.arcadeDrive(-1, .33);//forward .6
				}
			}else if(timer.get() <= 1.75) {//stop .1
				PIDtoggle(false);
				if(drivable) {
					robot.arcadeDrive(0, 0);
				}
			}else if(timer.get() <= 2.45) {//turn .7
				if(drivable) {
					robot.arcadeDrive(0, 0);
				}
				turn(autoAng[1]);
				PIDtoggle(true);
			}else if(timer.get() <= 2.95) {//forward .1
				mainPiston.set(DoubleSolenoid.Value.kForward);
				secondaryPiston.set(DoubleSolenoid.Value.kForward);
				PIDtoggle(false);
				if(drivable) {
					robot.arcadeDrive(-1, .33);
				}
			}else if(!reverse){		//stop if not reversing
				PIDtoggle(false);
				if(drivable) {
					robot.arcadeDrive(0, 0);
				}
			}
			if(reverse) {		//drive us back
				if(timer.get() >= 2.23) {
					if(timer.get() <= 4) {
						//wait to let pistons throw
						PIDtoggle(false);
						if(drivable) {
							robot.arcadeDrive(0, 0);
						}
					}else if(timer.get() <= 4.05) {
						mainPiston.set(DoubleSolenoid.Value.kReverse);
						secondaryPiston.set(DoubleSolenoid.Value.kReverse);
						PIDtoggle(false);
						if(drivable) {
							robot.arcadeDrive(1, 0);
						}
					}else if(timer.get() <= 4.15) {
						if(drivable) {
							robot.arcadeDrive(0, 0);
						}
					}else if(timer.get() <= 4.84) {
						if(drivable) {
							robot.arcadeDrive(0, 0);
						}
						turn(autoAng[0]);
						PIDtoggle(true);
					}else if(timer.get() <= 5.5) {
						PIDtoggle(false);
						if(drivable) {
							robot.arcadeDrive(1, 0);
						}
					}else if(timer.get() <= 6.1) {
						if(drivable) {
							robot.arcadeDrive(0, 0);
						}
						turn(autoAng[1]);
						PIDtoggle(true);
					}else if(timer.get() <= 6.3) {
						PIDtoggle(false);
						if(drivable) {
							robot.arcadeDrive(1, 0);
						}
					}else {
						PIDtoggle(false);
						if(drivable) {
							robot.arcadeDrive(0, 0);
						}
					}
				}
			}
		}else {//we start edges
			if(switch2.get()) {//we start left side
				if(gameData.charAt(0) == 'L') {//switch is on left
					if(timer.get() <= 1.4) {
						PIDtoggle(false);
						if(drivable) {
							robot.arcadeDrive(-1, .4);
						}
					}else if(timer.get() <= 1.5) {
						PIDtoggle(false);
						if(drivable) {
							robot.arcadeDrive(0, 0);
						}
					}else if(timer.get() <= 2.3) {
						if(drivable) {
							robot.arcadeDrive(0, 0);
						}
						turn(autoAng[0]);
						PIDtoggle(true);
					}else if(timer.get() <= 2.65) {
						robot.arcadeDrive(-1,  .36);
					}else if(timer.get() <= 2.8) {
						robot.arcadeDrive(-1,  .36);
						mainPiston.set(DoubleSolenoid.Value.kForward);
						secondaryPiston.set(DoubleSolenoid.Value.kForward);
					}
				}else {		//switch not with us
					if(timer.get() <= 1.4) {
						PIDtoggle(false);
						if(drivable) {
							robot.arcadeDrive(-1, .35);
						}
					}else if(timer.get() <= 1.5) {
						PIDtoggle(false);
						if(drivable) {
							robot.arcadeDrive(0, 0);
						}
					}
				}
			}else {//we start right side
				if(gameData.charAt(0) == 'R') {//switch is on right
					dash.putNumber("auto0", autoAng[0]);
					if(timer.get() <= 1.4) {
						PIDtoggle(false);
						if(drivable) {
							robot.arcadeDrive(-1, .42);
						}
					}else if(timer.get() <= 1.5) {
						PIDtoggle(false);
						if(drivable) {
							robot.arcadeDrive(0, 0);
						}
					}else if(timer.get() <= 2.3) {
						if(drivable) {
							robot.arcadeDrive(0, 0);
						}
						turn(autoAng[0]);
						PIDtoggle(true);
					}else if(timer.get() <= 2.65) {
						robot.arcadeDrive(-1,  .36);
					}else if(timer.get() <= 2.8) {
						robot.arcadeDrive(-1,  .36);
						mainPiston.set(DoubleSolenoid.Value.kForward);
						secondaryPiston.set(DoubleSolenoid.Value.kForward);
					}
				}else {		//switch not with us ctl
					if(timer.get() <= 1.4) {
						PIDtoggle(false);
						if(drivable) {
							robot.arcadeDrive(-1, .34);
						}
					}else if(timer.get() <= 1.5) {
						if(drivable) {
							robot.arcadeDrive(0, 0);
						}
					}
				}
			}
		}
	}else{}
	dash.putNumber("Auto Time", timer.get());
	dash.putNumber("a0",  autoAng[0]);
	}

	
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	@SuppressWarnings("static-access")
	public void teleopPeriodic() {
		leftPID.disable();
		rightPID.disable();
		//arm controls
		if(cont.getA()) {
			mainPiston.set(DoubleSolenoid.Value.kForward);
			secondaryPiston.set(DoubleSolenoid.Value.kForward);
		}else if(cont.getXB()) {
			mainPiston.set(DoubleSolenoid.Value.kForward);
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		}else if(cont.getB()) {
			mainPiston.set(DoubleSolenoid.Value.kReverse);
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		}else if (cont.getYB()) { //left bumper button dumps a cube then returns arm to normal position
			mainPiston.set(DoubleSolenoid.Value.kForward);
			secondaryPiston.set(DoubleSolenoid.Value.kForward);
			try { 
				Thread.sleep(1600);
			}catch(InterruptedException e) {
			
			}
			mainPiston.set(DoubleSolenoid.Value.kReverse);
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		}
		
		//things that don't change regardless of manual mode
		
		//winch
		if(cont.getRB()){
			dash.putBoolean("winchOn", true);
			winch_motors.set(-1);
		}
		else if(cont.getLB()) {
			dash.putBoolean("winchOn", true);
			winch_motors.set(.25);
		}
		else {
			dash.putBoolean("winchOn", false);
			winch_motors.set(0);
		}
		
		//climb 
		double speed = ((stick.getThrottle()+1)/2);
		if(stick.getRawButton(12)){
			arm.set(-speed);
		}else if(stick.getRawButton(11)) {
			arm.set(speed);
		}else {
			arm.set(0);
		}

		//drive
		robot.arcadeDrive(stick.getY(), stick.getX());
		
		end_affecter.set(-cont.getLY());
		
		//Aaron Drive
		/*if(cont.getXB()) {
			reverse = true;
		}else if(cont.getB()) {
			reverse = false;
		}
		
		double turnCoe = dash.getNumber("turn", .75);
		double drivePower = 2 * cont.getRT();
		if(!reverse) {
			robot.arcadeDrive(-drivePower, turnCoe * cont.getLX());
		}else {
			robot.arcadeDrive(drivePower, turnCoe * -cont.getLX());
		}
	
		//arm controls
		if(stick.getRawButton(5)) {
			mainPiston.set(DoubleSolenoid.Value.kForward);
			secondaryPiston.set(DoubleSolenoid.Value.kForward);
		}else if(stick.getRawButton(2)) {
			mainPiston.set(DoubleSolenoid.Value.kForward);
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		}else if(stick.getRawButton(3)) {
			mainPiston.set(DoubleSolenoid.Value.kReverse);
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		}else if (stick.getRawButton(1)) { //left bumper button dumps a cube then returns arm to normal position
			mainPiston.set(DoubleSolenoid.Value.kForward);
			secondaryPiston.set(DoubleSolenoid.Value.kForward);
			try { 
				Thread.sleep(1600);
			}catch(InterruptedException e) {}
			mainPiston.set(DoubleSolenoid.Value.kReverse);
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		}
				
		//things that don't change regardless of manual mode
				
		//winch
		if(stick.getRawButton(9)){
			winch_motors.set(-1);
		}
		else if(stick.getRawButton(7)) {
			winch_motors.set(.25);
		}
		else {
			winch_motors.set(0);
		}
				
		//climb 
		double speed = ((stick.getThrottle()+1)/2);
		if(stick.getRawButton(12)){
			arm.set(-speed);
		}else if(stick.getRawButton(11)) {
			arm.set(speed);
		}else {
			arm.set(0);
		}
				
		end_affecter.set(-stick.getY());*/
		
		//send info to dash
		dash.putBoolean("switch1", switch1.get());
		dash.putBoolean("switch2",  switch2.get());
		
		//navx
        dash.putBoolean("Gyro Connected", gyro.isConnected());
        dash.putNumber("Gyro Angle", gyro.getAngle());
        
		}
	private double limit(double d) {
		if(d>1)
			return 1;
		else if(d<-1)
			return -1;
		else 
			return 0;
	}
	
	private void turn(double target) {
		leftPID.setSetpoint(target);
		rightPID.setSetpoint(target);
		dash.putNumber("target", target);
	}
	
	private void PIDtoggle(boolean pid_on) {
		if(pid_on) {
			drivable = false;
			leftPID.enable();
			rightPID.enable();
		}else {
			leftPID.disable();
			rightPID.disable();
			drivable = true;
		}
	}
	
	private void revToggle(boolean on) {
		if(on) {
			reverse = true;
		}else {
			reverse = false;
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	@SuppressWarnings("static-access")
	public void testPeriodic() {
		
	}
}
