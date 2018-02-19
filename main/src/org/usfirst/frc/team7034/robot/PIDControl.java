package org.usfirst.frc.team7034.robot;
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.AHRS.SerialDataType;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;

public class PIDControl {
	
	double p;
	double i;
	double d;
	double pErr;
	double pGain;
	double iErr;
	double iGain;
	double dErr;
	double dGain;
	AHRS navX;
	
	
	public PIDControl()
	{
		pGain = 1;
		iGain = 1;
		dGain = 1;
		pErr = 1;
		iErr = 1;
		dErr = 1;
		
		p = pGain * pErr;
		i = iGain * iErr;
		d = dGain * dErr;
		
		navX = new AHRS(SerialPort.Port.kMXP, SerialDataType.kProcessedData, new Byte((byte) 10));
		navX.getDisplacementX();
	}
	
	// @override
	public void calculate()
	{
		p = getP();
		i = getI();
		d = getD();
	}
	
	public double getVelocity()
	{
		double aNought = navX.getRawAccelX();
		
		double aFinal = navX.getRawAccelX();
		
		double time = 0.1; //in seconds between refresh
		
		return 0;
	}
	
	public double getP()
	{
		return 0;
	}
	
	public double getI()
	{
		return 0;
	}
	
	public double getD()
	{
		return 0;
	}

}
