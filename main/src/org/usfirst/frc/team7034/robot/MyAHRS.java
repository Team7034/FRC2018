package org.usfirst.frc.team7034.robot;

import com.kauailabs.navx.frc.*;

import edu.wpi.first.wpilibj.I2C;

public class MyAHRS extends AHRS {
	
	public MyAHRS(I2C.Port port) {
		super(port);
	}
	
	
	public void update() {
		//x_angle_tracker.nextAngle(super.getPitch());
	}
	
	public double getXAngle() {
		//return x_angle_tracker.getAngle();
		return 0.0;
	}

	public double getXRate() {
		//return x_angle_tracker.getRate();
		return 0.0;
	}
}
