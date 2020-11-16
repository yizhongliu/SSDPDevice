package pri.tool.upnp;

import java.io.File;
import java.net.InetAddress;

import pri.tool.upnp.device.Advertiser;
import pri.tool.upnp.ssdp.SSDP;
import pri.tool.upnp.ssdp.SSDPSearchSocketList;

public class DeviceData {
    public final static int HTTP_DEFAULT_PORT = 8093;
    public final static String DEFAULT_DESCRIPTION_URI = "deviceDescription";
    public final static int DEFAULT_LEASE_TIME = 30 * 60;

    private InetAddress[] httpBinds = null;

    public void setHTTPBindAddress(InetAddress[] inets){
        this.httpBinds=inets;
    }

    public InetAddress[] getHTTPBindAddress(){
        return this.httpBinds;
    }


    ////////////////////////////////////////////////
    // description
    ////////////////////////////////////////////////

    private String descriptionURI = DEFAULT_DESCRIPTION_URI;
    private File descriptionFile = null;

    public File getDescriptionFile() {
        return descriptionFile;
    }

    public String getDescriptionURI() {
        return descriptionURI;
    }

    public void setDescriptionFile(File descriptionFile) {
        this.descriptionFile = descriptionFile;
    }

    public void setDescriptionURI(String descriptionURI) {
        this.descriptionURI = descriptionURI;
    }

    ////////////////////////////////////////////////
    //	httpPort
    ////////////////////////////////////////////////

    private int httpPort = HTTP_DEFAULT_PORT;

    public int getHTTPPort() {
        return httpPort;
    }

    public void setHTTPPort(int port) {
        httpPort = port;
    }


    ////////////////////////////////////////////////
    //	LeaseTime
    ////////////////////////////////////////////////

    private int leaseTime = DEFAULT_LEASE_TIME;

    public int getLeaseTime()
    {
        return leaseTime;
    }

    public void setLeaseTime(int val)
    {
        leaseTime = val;
    }

    ////////////////////////////////////////////////
    // Advertiser
    ////////////////////////////////////////////////

    private Advertiser advertiser = null;

    public void setAdvertiser(Advertiser adv)
    {
        advertiser = adv;
    }

    public Advertiser getAdvertiser()
    {
        return advertiser;
    }


    ////////////////////////////////////////////////
    // SSDPSearchSocket
    ////////////////////////////////////////////////

    private SSDPSearchSocketList ssdpSearchSocketList = null;
    private String ssdpMulticastIPv4 = SSDP.ADDRESS;
    private String ssdpMulticastIPv6 = SSDP.getIPv6Address();
    private int ssdpPort = SSDP.PORT;
    private InetAddress[] ssdpBinds = null;

    public SSDPSearchSocketList getSSDPSearchSocketList() {
        if(this.ssdpSearchSocketList==null){
            this.ssdpSearchSocketList = new SSDPSearchSocketList(this.ssdpBinds,ssdpPort,ssdpMulticastIPv4,ssdpMulticastIPv6);
        }
        return ssdpSearchSocketList;
    }
}
