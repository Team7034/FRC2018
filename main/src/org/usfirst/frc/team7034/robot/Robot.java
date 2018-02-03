package org.usfirst.frc.team7034.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team7034.robot.Controller;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {	
	Spark front_left;
	Spark back_left;	
	Spark front_right;
	Spark back_right;
	
	WPI_TalonSRX winchTalon;
	
	SpeedController winchControl;
	
	SpeedControllerGroup left_motors;
	SpeedControllerGroup right_motors;
	
	DifferentialDrive robot;
	Joystick stick;
	Compressor compressor;
	DoubleSolenoid doubleSolenoid;
	Controller cont;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		front_left = new Spark(0);
		back_left = new Spark(1);
		left_motors = new SpeedControllerGroup(front_left, back_left);
		
		front_right = new Spark(2);
		back_right = new Spark(3);
		right_motors = new SpeedControllerGroup(front_right, back_right);
		
		robot = new DifferentialDrive(left_motors, right_motors);
		stick = new Joystick(0);
		stick.setThrottleChannel(3);
		
		compressor = new Compressor(0);
		compressor.setClosedLoopControl(true);
		
		doubleSolenoid = new DoubleSolenoid(0,5);
		doubleSolenoid.set(DoubleSolenoid.Value.kOff);
	
		
		cont = new Controller(0);
		
		winchTalon = new WPI_TalonSRX(2);
		winchTalon.configOpenloopRamp(2.0, 200);
		winchTalon.configClosedloopRamp(2.0,200);
		winchControl = winchTalon;
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
		double speed = ((stick.getThrottle()+1)/2);
		robot.arcadeDrive(-stick.getY()*speed, stick.getX()*speed);
		//DoubleSolenoid.Value state = DoubleSolenoid.Value.kOff;
		if (cont.getXB())
		{
			doubleSolenoid.set(DoubleSolenoid.Value.kOff);
		}
		else if (cont.getA())
		{
			doubleSolenoid.set(DoubleSolenoid.Value.kForward);
			try { Thread.sleep(15);}
			catch(InterruptedException e) {	}
			doubleSolenoid.set(DoubleSolenoid.Value.kOff);
		}
		else if (cont.getB())
		{
			doubleSolenoid.set(DoubleSolenoid.Value.kReverse);
		}
		
	}
		
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
