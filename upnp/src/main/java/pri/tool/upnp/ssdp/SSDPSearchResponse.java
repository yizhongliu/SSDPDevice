/******************************************************************
*
*	CyberUPnP for Java
*
*	Copyright (C) Satoshi Konno 2002
*
*	File: SSDPSearchResponse.java
*
*	Revision;
*
*	01/14/03
*		- first revision.
*	
******************************************************************/

package pri.tool.upnp.ssdp;

import pri.tool.upnp.http.*;
import pri.tool.upnp.*;

public class SSDPSearchResponse extends SSDPResponse
{
	////////////////////////////////////////////////
	//	Constructor
	////////////////////////////////////////////////
	
	public SSDPSearchResponse()
	{
		setStatusCode(HTTPStatus.OK);
		setCacheControl(Device.DEFAULT_LEASE_TIME);
		setHeader(HTTP.SERVER, UPnP.getServerName());
		setHeader(HTTP.EXT, "");
	}
}
