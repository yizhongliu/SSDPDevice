/******************************************************************
*
*	CyberXML for Java
*
*	Copyright (C) Satoshi Konno 2002
*
*	File: XercesParser.java
*
*	Revision;
*
*	11/26/02
*		- first revision.
*	12/26/03
*		- Changed to the file name from Parser.java to XercesParser.java.
*		- Changed to implement org.cybergarage.xml.Parser interface.
*	02/09/05
*		- Stefano Lenzi <kismet-sl@users.sourceforge.net>
*		- Fixed a bug in XercesParser::parse(Node,Node,int) that is when you faound an xml like that <test></test> 
*		  you crate a node with name="test" and value=null that is non correct. It should had name="test" and value="". 
*
******************************************************************/

package pri.tool.upnp.xml.parser;

import org.apache.xerces.parsers.DOMParser;
import pri.tool.upnp.xml.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.InputStream;

public class XercesParser extends pri.tool.upnp.xml.Parser
{
	////////////////////////////////////////////////
	//	Constructor
	////////////////////////////////////////////////

	public XercesParser()
	{
	}

	////////////////////////////////////////////////
	//	parse (Node)
	////////////////////////////////////////////////

	public pri.tool.upnp.xml.Node parse(pri.tool.upnp.xml.Node parentNode, org.w3c.dom.Node domNode, int rank)
	{
		int domNodeType = domNode.getNodeType();
//		if (domNodeType != Node.ELEMENT_NODE)
//			return;

		String domNodeName = domNode.getNodeName();
		String domNodeValue = domNode.getNodeValue();
		NamedNodeMap attrs = domNode.getAttributes();
		int arrrsLen = (attrs != null) ? attrs.getLength() : 0;

//		Debug.message("[" + rank + "] ELEM : " + domNodeName + ", " + domNodeValue + ", type = " + domNodeType + ", attrs = " + arrrsLen);

		if (domNodeType == Node.TEXT_NODE) {
			parentNode.setValue(domNodeValue);
			return parentNode;
		}

		if (domNodeType != Node.ELEMENT_NODE)
			return parentNode;

		pri.tool.upnp.xml.Node node = new pri.tool.upnp.xml.Node();
		node.setName(domNodeName);
		node.setValue(domNodeValue);

		if (parentNode != null)
			parentNode.addNode(node);

		NamedNodeMap attrMap = domNode.getAttributes();
		int attrLen = attrMap.getLength();
		//Debug.message("attrLen = " + attrLen);
		for (int n = 0; n<attrLen; n++) {
			Node attr = attrMap.item(n);
			String attrName = attr.getNodeName();
			String attrValue = attr.getNodeValue();
			node.setAttribute(attrName, attrValue);
		}

		// Thanks for Stefano Lenzi (02/10/05)
		org.w3c.dom.Node  child = domNode.getFirstChild();
		if(child==null){
			node.setValue("");
			return node;
		}
		do{
			parse(node, child, rank+1);
			child = child.getNextSibling();
		}while (child != null);


		return node;
	}

	public pri.tool.upnp.xml.Node parse(pri.tool.upnp.xml.Node parentNode, org.w3c.dom.Node domNode)
	{
		return parse(parentNode, domNode, 0);
	}

	////////////////////////////////////////////////
	//	parse
	////////////////////////////////////////////////

	public pri.tool.upnp.xml.Node parse(InputStream inStream) throws ParserException
	{
		pri.tool.upnp.xml.Node root = null;

		try {
			DOMParser parser = new DOMParser();
			InputSource inSrc = new InputSource(inStream);
			parser.parse(inSrc);

			Document doc = parser.getDocument();
			Element docElem = doc.getDocumentElement();

			if (docElem != null)
				root = parse(root, docElem);
/*
			NodeList rootList = doc.getElementsByTagName("root");
			Debug.message("rootList = " + rootList.getLength());
			
			if (0 < rootList.getLength())
				root = parse(root, rootList.item(0));
*/
		}
		catch (Exception e) {
			throw new ParserException(e);
		}
		
		return root;
	}
	
}

