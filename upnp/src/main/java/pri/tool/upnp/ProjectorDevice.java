package pri.tool.upnp;

import android.os.Build;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import pri.tool.upnp.device.InvalidDescriptionException;
import pri.tool.upnp.net.HostInterface;

public class ProjectorDevice extends Device {
    public final static int DEFAULT_HTTP_PORT = 39520;

    public final static String DESCRIPTION =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<root xmlns=\"urn:schemas-upnp-org:device-1-0\">\n" +
                    "   <specVersion>\n" +
                    "      <major>1</major>\n" +
                    "      <minor>0</minor>\n" +
                    "   </specVersion>\n" +
                    "   <device>\n" +
                    "      <deviceType>urn:projector-iview-com:device:projector:1</deviceType>\n" +
                    "      <friendlyName>Cyber Garage Media Render</friendlyName>\n" +
                    "      <manufacturer>Cyber Garage</manufacturer>\n" +
                    "      <manufacturerURL>http://www.cybergarage.org</manufacturerURL>\n" +
                    "      <modelDescription>Provides content through UPnP ContentDirectory service</modelDescription>\n" +
                    "      <modelName>Cyber Garage Media Render</modelName>\n" +
                    "      <modelNumber>1.0</modelNumber>\n" +
                    "      <modelURL>http://www.cybergarage.org</modelURL>\n" +
                    "      <UDN>uuid:362d9414-31a0-48b6-b684-2b4bd38391d0</UDN>\n" +
                    "      <serviceList>\n" +
                    "         <service>\n" +
                    "            <serviceType>urn:schemas-upnp-org:service:RenderingControl:1</serviceType>\n" +
                    "            <serviceId>RenderingControl</serviceId>\n" +
                    "         </service>\n" +
                    "         <service>\n" +
                    "            <serviceType>urn:schemas-upnp-org:service:ConnectionManager:1</serviceType>\n" +
                    "            <serviceId>ConnectionManager</serviceId>\n" +
                    "         </service>\n" +
                    "         <service>\n" +
                    "            <serviceType>urn:schemas-upnp-org:service:AVTransport:1</serviceType>\n" +
                    "            <serviceId>AVTransport</serviceId>\n" +
                    "         </service>\n" +
                    "      </serviceList>\n" +
                    "   </device>\n" +
                    "</root>";

    public ProjectorDevice()
    {
        super();
        try {
            initialize(DESCRIPTION);
        } catch (InvalidDescriptionException e) {
            e.printStackTrace();
        }

    }


    private void initialize(String description) throws InvalidDescriptionException
    {
        loadDescription(description);
        initialize();
    }

    private void initialize()
    {
        // Netwroking initialization
        UPnP.setEnable(UPnP.USE_ONLY_IPV4_ADDR);
//        String firstIf = HostInterface.getHostAddress(0);
//        setInterfaceAddress(firstIf);
        setHTTPPort(DEFAULT_HTTP_PORT);
    //    setUDN("sn:"+);
        UUID uuid = UUID.nameUUIDFromBytes(getDeviceSN().getBytes());
        setUDN("uuid:" + "123456789");

        String firstIfv = HostInterface.getHostAddressByType("wlan0");
        Log.e(TAG, "firstIfv:" + firstIfv);
        if (firstIfv != null) {
            setInterfaceAddress(firstIfv);
        }
    }

    ////////////////////////////////////////////////
    // HostAddress
    ////////////////////////////////////////////////

    public void setInterfaceAddress(String ifaddr)
    {
        HostInterface.setInterface(ifaddr);
    }

    public static String getDeviceSN() {
        String sn = null;
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                sn = Build.getSerial();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            sn = Build.SERIAL;
        }
        return sn;
    }

}
