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
import edu.wpi.first.wpilibj.Compressor;
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
	
	/*
	//winch
	WPI_TalonSRX winch_one;
	WPI_TalonSRX winch_two;
	SpeedControllerGroup winch_motors;
	
	float winch_power = 0;
	boolean winch_disabled = false;
*/
	
	//controllers
	Joystick stick;
	Controller cont;
	//Controller cont2;
	
	//sensors
	//AHRS gyro;
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
	boolean manual = true;
	boolean reverse = false;
	boolean firstTime = true;
	
	DriverStation ds;
	
	double armPower;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
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
		//end_affecter = new Spark(5);
		//enc = new Encoder(0,1);
		//enc.reset();
		
		//winch 
	/*	
		winch_one = new WPI_TalonSRX(1);
		winch_two = new WPI_TalonSRX(2);
		*/
		/*winch_one.configOpenloopRamp(2.0, 200);
		winch_one.configClosedloopRamp(0.8,200);
		winch_two.configOpenloopRamp(2.0, 200);
		winch_two.configClosedloopRamp(0.8,200);
		
		winch_motors = new SpeedControllerGroup(winch_one, winch_two);
*/		
		//controllers
		cont = new Controller(0);
		//cont2 = new Controller(2);
		stick = new Joystick(1);
		stick.setThrottleChannel(3);
		
		//sensors
		
		//navX = new AHRS(I2C.Port.kMXP);
		
	/*	
		try {
			gyro = new AHRS(SerialPort.Port.kUSB);
		} catch(RuntimeException e) {
			DriverStation.reportError("Error Instantiating the NavX Micro: " + e.getMessage(), true);
		}
		*/
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
		timer.start();
		
	}

		/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
	
	/*
		if(firstTime) {
		firstTime = false;
		try {
			Thread.sleep(13250);
		}catch(InterruptedException e) {
		
		}}
	robot.arcadeDrive(-.85, 0);
	//left_motors.set(.85);
	//right_motors.set(-.85);
	*/
		dash.putNumber("timer time", timer.get());
		
	 if (timer.get() > 10.000 && timer.get() < 11.500)	 
	 	robot.arcadeDrive(-.85,0);
	 
	
	}
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		
		//update sensor values
		/*
		double pidval = 0;
		double angle = gyro.getAngle();
		double rate = gyro.getRate();
		double angle2 = gyro.getPitch();
		angle2 = angle2 + 90;
		double rate2 = gyro.getRawGyroX();
		double arm_power = .8*-cont.getRY();
		double navXAngle = navX.getAngle();
		boolean slowMo = false;
		*/
	
		//check to see if arm is in manual control mode && slow mode
		if (cont.getStart())
			manual = true;
		else if (cont.getBack()) 
			manual = false;
		if(stick.getRawButton(8)) {}
			//slowMo = true;
		else if(stick.getRawButton(7)) {}
			//slowMo = false;

		//drive
		double speed = ((stick.getThrottle()+1)/2);
		//double speed = stick.getThrottle();
		//if(slowMo)
			//robot.arcadeDrive(stick.getY()*.6, stick.getX()*.6);
		//else 
			robot.arcadeDrive(-stick.getY(), stick.getX());
	
		
				
		//driver-aid buttons
		
		if (cont.getYB()) //collapses pneumatics and holds arm locked up for CLIMBING
		{
			arm.set(.3); //driver controls arm automatically in reverse 
			try {
				Thread.sleep(450);
			}catch(InterruptedException e) {
			}
			arm.set(0);
			armPower = 0;
			mainPiston.set(DoubleSolenoid.Value.kReverse);
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
			
		}
		
		
		
		if (cont.getLB()) //left bumper button dumps a cube then returns arm to normal position
		{
			mainPiston.set(DoubleSolenoid.Value.kForward);
			secondaryPiston.set(DoubleSolenoid.Value.kForward);
			try { 
				Thread.sleep(1000);
			}catch(InterruptedException e) {
			
			}
			mainPiston.set(DoubleSolenoid.Value.kReverse);
			secondaryPiston.set(DoubleSolenoid.Value.kReverse);
		}
		
		if (cont.getLT() > 0.5)
		{
			mainPiston.set(DoubleSolenoid.Value.kForward);
			secondaryPiston.set(DoubleSolenoid.Value.kForward);
			try { 
				Thread.sleep(750);
			}catch(InterruptedException e) {}
			arm.set(-.3);
			try { 
				Thread.sleep(1000);
			}catch(InterruptedException e) {}
			arm.set(0);
			armPower = 0;
		}
		
		
		
		
		//if(cont2.getB()) { reverse = true; }
		//if(cont2.getA()) { reverse = false; }
		
		/*
		 *if(!reverse) {
		 	if(slowMo){
				robot.arcadeDrive(cont2.getRT()*.65, cont2.getRX()*.65);
			}else{
				robot.arcadeDrive(cont2.getRT(), cont2.getRX());
			}
		}else {
			if(slowMo){
				robot.arcadeDrive(-cont2.getRT()*.65, cont2.getRX()*.65);
			}else{
				robot.arcadeDrive(-cont2.getRT(), cont2.getRX());
			}
		}
		 */
		
		double Pval = -speed*.4;
	
		
		//PID arm
		
		//PIDControl.setSetpoint(0);
		
		if (!manual)
		{
			//PIDControl.enable();
		//	arm.set(limit(Pval*(gyroInit - gyro.getRoll())));
		}
		else {
			
			
			//PIDControl.disable();
			
		
			if (stick.getRawButton(11)) //reversed
			{
				armPower += cont.getRY() * 1;
				arm.set(armPower);
			}
			else if (cont.getRT() > 0.5)
				 { armPower = 0; }
			else
			{
				armPower += (int) (cont.getRY())/100.0 * -1;
				arm.set(armPower);
			}
			
			//test
			/*
			PIDControl.disable();
			if(stick.getRawButton(11)) //reversed 
				arm.set(speed*0.65);
			else
				arm.set(-speed*0.65);
			//arm.set(speed*0.8);
		*/
		}
		/*
		if (PIDControl.isEnabled())
		{
			//arm.set(PIDControl.get());
			//pidval = PIDControl.get();
		}
		*/
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
		/*
		//end affecter
		if(manual) {
			if (enc.getDistance() > -2400 && enc.getDistance() <= 0)
				end_affecter.set(cont.getLY());
			else if( enc.getDistance() <= -2400)
				end_affecter.set(.3);
			else if (enc.getDistance() > 0)
				end_affecter.set(-.3);
		}
		*/
	/*
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
		
		if(winch_power > 1) 
			winch_power = 1;
		else if(winch_power < -1)
			winch_power = -1;
		if(cont.getRB()) 
			winch_motors.set(winch_power);
		else 
			winch_motors.set(0);
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
		
		//send info to dashboard
		
		//double encDistance = enc.getDistance();
		/*
		dash.putNumber("angle",  angle);
		//dash.putNumber("rate", rate);
		dash.putNumber("encRate",  enc.get());
		dash.putNumber("speed",  speed);
		dash.putNumber("pidval", Pval);
		//dash.putNumber("power",  winch_power);
		dash.putNumber("arm power", arm_power);
		dash.putBoolean("manual arm", manual);
		//dash.putBoolean("winchon", cont.getRB());
		//dash.putNumber("navX angle", navXAngle);
		dash.putNumber("encoder", encDistance);
		
	*/
		
		dash.putNumber("armPower", armPower);
		dash.putNumber("RY", cont.getRY());
		dash.putNumber("right trigger", cont.getRT());
		
		}
	private double limit(double d) {
		if(d>1)
			return 1;
		else if(d<-1)
			return -1;
		else 
			return 0;
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
