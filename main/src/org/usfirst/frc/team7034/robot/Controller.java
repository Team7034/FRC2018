package org.usfirst.frc.team7034.robot;

import edu.wpi.first.wpilibj.GenericHID;

public class Controller extends GenericHID {

	public Controller(final int port) {
		super(port);
	}
	
	public final double getLX() {
		return getRawAxis(0);
	}
	
	public final double getLY() {
		return getRawAxis(1);
	}
	
	public final double getRX() {
		return getRawAxis(4);
	}
	
	public final double getRY() {
		return getRawAxis(5);
	}
	
	public final double getLT() {
		return getRawAxis(2);
	}
	
	public final double getRT() {
		return getRawAxis(3);
	}

	@Override
	public double getX(Hand hand) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getY(Hand hand) {
		// TODO Auto-generated method stub
		return 0;
	}
}
