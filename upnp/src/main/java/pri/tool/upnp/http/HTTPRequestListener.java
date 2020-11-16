/******************************************************************
*
*	CyberHTTP for Java
*
*	Copyright (C) Satoshi Konno 2002
*
*	File: HTTPRequestListener.java
*
*	Revision;
*
*	12/13/02
*		- first revision.
*	
******************************************************************/

package pri.tool.upnp.http;

public interface HTTPRequestListener
{
	public void httpRequestRecieved(HTTPRequest httpReq);
}
