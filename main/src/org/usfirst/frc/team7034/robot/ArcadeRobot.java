package org.usfirst.frc.team7034.robot;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;

public class ArcadeRobot extends DifferentialDrive{

	public ArcadeRobot(SpeedController leftMotor, SpeedController rightMotor) {
		super(leftMotor, rightMotor);
		// TODO Auto-generated constructor stub
	}

	/*@Override
	public void arcadeDrive(double xSpeed, double zRotation, boolean squaredInputs) {
    if (!m_reported) {
      HAL.report(tResourceType.kResourceType_RobotDrive, 2, tInstances.kRobotDrive_ArcadeStandard);
      m_reported = true;
    }

    xSpeed = limit(xSpeed);
    xSpeed = applyDeadband(xSpeed, m_deadband);

    zRotation = limit(zRotation);
    zRotation = applyDeadband(zRotation, m_deadband);

    // Square the inputs (while preserving the sign) to increase fine control
    // while permitting full power.
    if (squaredInputs) {
      xSpeed = Math.copySign(xSpeed * xSpeed, xSpeed);
      zRotation = Math.copySign(zRotation * zRotation, zRotation);
    }

    double leftMotorOutput;
    double rightMotorOutput;

    double maxInput = Math.copySign(Math.max(Math.abs(xSpeed), Math.abs(zRotation)), xSpeed);

    if (xSpeed >= 0.0) {
      // First quadrant, else second quadrant
      if (zRotation >= 0.0) {
        leftMotorOutput = maxInput;
        rightMotorOutput = xSpeed - zRotation;
      } else {
        leftMotorOutput = xSpeed + zRotation;
        rightMotorOutput = maxInput;
      }
    } else {
      // Third quadrant, else fourth quadrant
      if (zRotation >= 0.0) {
        leftMotorOutput = xSpeed + zRotation;
        rightMotorOutput = maxInput;
      } else {
        leftMotorOutput = maxInput;
        rightMotorOutput = xSpeed - zRotation;
      }
    }

    m_leftMotor.set(limit(leftMotorOutput) * m_maxOutput);
    m_rightMotor.set(-limit(rightMotorOutput) * m_maxOutput);

    m_safetyHelper.feed();
  }*/
}
