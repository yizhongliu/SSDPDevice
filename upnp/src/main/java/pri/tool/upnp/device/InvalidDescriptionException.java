/******************************************************************
*
*	CyberUPnP for Java
*
*	Copyright (C) Satoshi Konno 2002
*
*	File: InvalidDescriptionException.java
*
*	Revision;
*
*	12/26/02
*		- first revision.
*	
******************************************************************/

package pri.tool.upnp.device;

import java.io.File;

public class InvalidDescriptionException extends Exception
{
	public InvalidDescriptionException()
	{
		super();
	}
	
	public InvalidDescriptionException(String s)
	{
		super(s);
	}

	public InvalidDescriptionException(String s, File file)
	{
		super(s + " (" + file.toString() + ")");
	}

	public InvalidDescriptionException(Exception e)
	{
		super(e.getMessage());
	}
}
