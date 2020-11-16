/******************************************************************
*
*	CyberUPnP for Java
*
*	Copyright (C) Satoshi Konno 2002
*
*	File: Advertiser.java
*
*	Revision;
*
*	12/24/03
*		- first revision.
*	06/18/04
*		- Changed to advertise every 25%-50% of the periodic notification cycle for NMPR;
*	
******************************************************************/

package pri.tool.upnp.device;

import android.util.Log;

import pri.tool.upnp.*;
import pri.tool.upnp.util.*;

public class Advertiser extends ThreadCore
{
	private final static String TAG = "Advertiser";
	////////////////////////////////////////////////
	//	Constructor
	////////////////////////////////////////////////

	public Advertiser(Device dev)
	{
		setDevice(dev);
	}
	
	////////////////////////////////////////////////
	//	Member
	////////////////////////////////////////////////

	private Device device;

	public void setDevice(Device dev)
	{
		device = dev;
	}
	
	public Device getDevice()
	{
		return device;
	}

	////////////////////////////////////////////////
	//	Thread
	////////////////////////////////////////////////
	
	public void run() 
	{
		Device dev = getDevice();
		long leaseTime = dev.getLeaseTime();
		long notifyInterval;
		while (isRunnable() == true) {
			notifyInterval = (leaseTime/4) + (long)((float)leaseTime * (Math.random() * 0.25f));
			notifyInterval *= 1000;
			Log.e(TAG, "notifyInterval:" + notifyInterval + ", leaseTime:" + leaseTime);
			try {
				Thread.sleep(notifyInterval);
			} catch (InterruptedException e) {}
			dev.announce();
		}
	}
}
