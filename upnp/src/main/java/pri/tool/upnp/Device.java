package pri.tool.upnp;


import android.util.Log;

import java.io.File;
import java.net.InetAddress;
import java.util.Calendar;

import pri.tool.upnp.device.Advertiser;
import pri.tool.upnp.device.Description;
import pri.tool.upnp.device.InvalidDescriptionException;
import pri.tool.upnp.device.NTS;
import pri.tool.upnp.device.ST;
import pri.tool.upnp.net.HostInterface;
import pri.tool.upnp.ssdp.SSDPNotifyRequest;
import pri.tool.upnp.ssdp.SSDPNotifySocket;
import pri.tool.upnp.ssdp.SSDPPacket;
import pri.tool.upnp.ssdp.SSDPSearchResponse;
import pri.tool.upnp.ssdp.SSDPSearchResponseSocket;
import pri.tool.upnp.ssdp.SSDPSearchSocketList;
import pri.tool.upnp.util.Debug;
import pri.tool.upnp.util.TimerUtil;
import pri.tool.upnp.xml.Node;
import pri.tool.upnp.xml.Parser;
import pri.tool.upnp.xml.ParserException;

import static pri.tool.upnp.DeviceData.DEFAULT_DESCRIPTION_URI;
import static pri.tool.upnp.DeviceData.DEFAULT_LEASE_TIME;
import static pri.tool.upnp.DeviceData.HTTP_DEFAULT_PORT;

public class Device implements SearchListener{

    // //////////////////////////////////////////////
    // Constants
    // //////////////////////////////////////////////
    public final static String TAG = "Device";

    public final static String ELEM_NAME = "device";
    public final static String UPNP_ROOTDEVICE = "upnp:rootdevice";

    public final static int DEFAULT_STARTUP_WAIT_TIME = 1000;
    public final static int DEFAULT_DISCOVERY_WAIT_TIME = 300;
    public final static int DEFAULT_LEASE_TIME = 30 * 60;

    public final static int HTTP_DEFAULT_PORT = 4004;

    public final static String DEFAULT_DESCRIPTION_URI = "/description.xml";
    public final static String DEFAULT_PRESENTATION_URI = "/presentation";

    DeviceData deviceData;

    public Device(Node root, Node device) {
        rootNode = root;
        deviceNode = device;
     //   setUUID(UPnP.createUUID());
        setWirelessMode(false);
    }


    public Device() {
     //   setUUID(UPnP.createUUID());
    }

    // //////////////////////////////////////////////
    // Member
    // //////////////////////////////////////////////

    private Node rootNode;
    private Node deviceNode;

    public Node getRootNode() {
        if (rootNode != null)
            return rootNode;
        if (deviceNode == null)
            return null;
        return deviceNode.getRootNode();
    }

    public Node getDeviceNode() {
        return deviceNode;
    }

    public void setRootNode(Node node) {
        rootNode = node;
    }

    public void setDeviceNode(Node node) {
        deviceNode = node;
    }


    // //////////////////////////////////////////////
    // Device UUID
    // //////////////////////////////////////////////

    private String devUUID;

    public void setUUID(String uuid) {
        this.devUUID = uuid;
    }

    public String getUUID() {
        return this.devUUID;
    }


    private void setAdvertiser(Advertiser adv) {
        getDeviceData().setAdvertiser(adv);
    }

    private Advertiser getAdvertiser() {
        return getDeviceData().getAdvertiser();
    }

    public boolean start() {
        stop(true);

        // //////////////////////////////////////
        // SSDP Seach Socket
        // //////////////////////////////////////

        final SSDPSearchSocketList ssdpSearchSockList = getSSDPSearchSocketList();
        if (ssdpSearchSockList.open() == false)
            return false;
        ssdpSearchSockList.addSearchListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ssdpSearchSockList.start();
            }
        }).start();


        // //////////////////////////////////////
        // BOOTID/CONFIGID.UPNP.ORG
        // //////////////////////////////////////

//        updateBootId();
//        updateConfigId();

        // //////////////////////////////////////
        // Announce
        // //////////////////////////////////////

        announce();

        // //////////////////////////////////////
        // Advertiser
        // //////////////////////////////////////

        Advertiser adv = new Advertiser(this);
        setAdvertiser(adv);
        adv.start();

        return true;
    }



    public final static void notifyWait() {
        TimerUtil.waitRandom(DEFAULT_DISCOVERY_WAIT_TIME);
    }

    private String getNotifyDeviceUSN() {
        if (isRootDevice() == false)
            return getUDN();
        return getUDN();
    }

    private String getNotifyDeviceTypeNT() {
        return getDeviceType();
    }

    public void announce(String bindAddr) {
        String devLocation = getLocationURL(bindAddr);

        SSDPNotifySocket ssdpSock = new SSDPNotifySocket(bindAddr);

        SSDPNotifyRequest ssdpReq = new SSDPNotifyRequest();
        ssdpReq.setServer(UPnP.getServerName());
        ssdpReq.setLeaseTime(getLeaseTime());
        ssdpReq.setLocation(devLocation);
        ssdpReq.setNTS(NTS.ALIVE);
        ssdpReq.setBootId(getBootId());

        // uuid:device-UUID(::upnp:rootdevice)*
//        if (isRootDevice() == true) {
//            String devNT = getNotifyDeviceNT();    //device type
//            String devUSN = getNotifyDeviceUSN();
//            ssdpReq.setNT(devNT);
//            ssdpReq.setUSN(devUSN);
//            ssdpSock.post(ssdpReq);
//
//            String devUDN = getUDN();  //独特的设备名称
//            ssdpReq.setNT(devUDN);
//            ssdpReq.setUSN(devUDN);
//            ssdpSock.post(ssdpReq);
//        }

        // uuid:device-UUID::urn:schemas-upnp-org:device:deviceType:v
        String devNT = getNotifyDeviceTypeNT();
        String devUSN = getNotifyDeviceUSN(); //服务名称USN：唯一标识一种服务实例
        ssdpReq.setNT(devNT);
        ssdpReq.setUSN(devUSN);
        ssdpSock.post(ssdpReq);

        // Thanks for Mikael Hakman (04/25/05)
        ssdpSock.close();
    }

    public void announce() {
        notifyWait();
        InetAddress[] binds = getDeviceData().getHTTPBindAddress();
        String[] bindAddresses;
        if (binds != null) {
            bindAddresses = new String[binds.length];
            for (int i = 0; i < binds.length; i++) {
                bindAddresses[i] = binds[i].getHostAddress();
            }
        } else {
            int nHostAddrs = HostInterface.getNHostAddresses();
            bindAddresses = new String[nHostAddrs];
            for (int n = 0; n < nHostAddrs; n++) {
                bindAddresses[n] = HostInterface.getHostAddress(n);
                Log.e(TAG, "bindAddress n:" + n + ", addr:" + bindAddresses[n]);
            }
        }
        for (int j = 0; j < bindAddresses.length; j++) {
            if (bindAddresses[j] == null || bindAddresses[j].length() == 0)
                continue;
            int ssdpCount = getSSDPAnnounceCount();
            for (int i = 0; i < ssdpCount; i++)
                announce(bindAddresses[j]);

        }
    }


    // //////////////////////////////////////////////
    // UserData
    // //////////////////////////////////////////////

    private DeviceData getDeviceData() {

        if (deviceData == null) {
            deviceData = new DeviceData();
        }
        return deviceData;
    }


    // //////////////////////////////////////////////
    // Wireless
    // //////////////////////////////////////////////

    private boolean wirelessMode;

    public void setWirelessMode(boolean flag) {
        wirelessMode = flag;
    }

    public boolean isWirelessMode() {
        return wirelessMode;
    }

    public int getSSDPAnnounceCount() {
        if (isWirelessMode() == true)
            return UPnP.INMPR03_DISCOVERY_OVER_WIRELESS_COUNT;
        return 1;
    }

    // //////////////////////////////////////////////
    // HTTP Server
    // //////////////////////////////////////////////

    public void setHTTPPort(int port) {
        getDeviceData().setHTTPPort(port);
    }

    public int getHTTPPort() {
        return getDeviceData().getHTTPPort();
    }

    // //////////////////////////////////////////////
    // Notify
    // //////////////////////////////////////////////

    public String getLocationURL(String host) {
        return HostInterface.getHostURL(host, getHTTPPort(),
                getDescriptionURI());
    }


    private String getDescriptionURI() {
        return getDeviceData().getDescriptionURI();
    }



    // //////////////////////////////////////////////
    // BootId
    // //////////////////////////////////////////////

    private int bootId;

    private void updateBootId() {
        this.bootId = UPnP.createBootId();
    }

    public int getBootId() {
        return this.bootId;
    }

    // //////////////////////////////////////////////
    // Description
    // //////////////////////////////////////////////

    private void setDescriptionFile(File file) {
        getDeviceData().setDescriptionFile(file);
    }

    private void setDescriptionURI(String uri) {
        getDeviceData().setDescriptionURI(uri);
    }

    public boolean loadDescription(String descString) throws InvalidDescriptionException{
        try {
            Parser parser = UPnP.getXMLParser();
            rootNode = parser.parse(descString);
            if (rootNode == null)
                throw new InvalidDescriptionException(
                        Description.NOROOT_EXCEPTION);
            deviceNode = rootNode.getNode(Device.ELEM_NAME);
            if (deviceNode == null)
                throw new InvalidDescriptionException(
                        Description.NOROOTDEVICE_EXCEPTION);
        } catch (ParserException e) {
            throw new InvalidDescriptionException(e);
        }

        if (initializeLoadedDescription() == false)
            return false;

        setDescriptionFile(null);

        return true;
    }

    private boolean initializeLoadedDescription() {
        setDescriptionURI(DEFAULT_DESCRIPTION_URI);
        setLeaseTime(DEFAULT_LEASE_TIME);
        setHTTPPort(HTTP_DEFAULT_PORT);

        // Thanks for Oliver Newell (03/23/04)
        if (hasUDN() == false)
            updateUDN();

        return true;
    }

    // //////////////////////////////////////////////
    // LeaseTime
    // //////////////////////////////////////////////

    public void setLeaseTime(int value) {
        getDeviceData().setLeaseTime(value);
//        Advertiser adv = getAdvertiser();
//        if (adv != null) {
//            announce();
//            adv.restart();
//        }
    }

    public int getLeaseTime() {
//        SSDPPacket packet = getSSDPPacket();
//        if (packet != null)
//            return packet.getLeaseTime();
        return getDeviceData().getLeaseTime();
    }

    // //////////////////////////////////////////////
    // UDN
    // //////////////////////////////////////////////

    private final static String UDN = "UDN";

    public void setUDN(String value) {
        getDeviceNode().setNode(UDN, value);
    }

    public String getUDN() {
        return getDeviceNode().getNodeValue(UDN);
    }

    public boolean hasUDN() {
        String udn = getUDN();
        if (udn == null || udn.length() <= 0)
            return false;
        return true;
    }


    private void updateUDN() {
        setUDN("uuid:" + getUUID());
    }

    // //////////////////////////////////////////////
    // Root Device
    // //////////////////////////////////////////////

    public boolean isRootDevice() {
        return getRootNode().getNode("device").getNodeValue(UDN)
                .equals(getUDN());
    }


    // //////////////////////////////////////////////
    // deviceType
    // //////////////////////////////////////////////

    private final static String DEVICE_TYPE = "deviceType";

    public void setDeviceType(String value) {
        getDeviceNode().setNode(DEVICE_TYPE, value);
    }

    public String getDeviceType() {
        return getDeviceNode().getNodeValue(DEVICE_TYPE);
    }

    public boolean isDeviceType(String value) {
        if (value == null)
            return false;
        return value.equals(getDeviceType());
    }

    private String getNotifyDeviceTypeUSN() {
        return getUDN() + "::" + getDeviceType();
    }

    private String getNotifyDeviceNT() {
        if (isRootDevice() == false)
            return getUDN();

        return UPNP_ROOTDEVICE;
    }

    private SSDPSearchSocketList getSSDPSearchSocketList() {
        return getDeviceData().getSSDPSearchSocketList();
    }


    @Override
    public void deviceSearchReceived(SSDPPacket ssdpPacket) {
        deviceSearchResponse(ssdpPacket);
    }

    public void deviceSearchResponse(SSDPPacket ssdpPacket) {
        String ssdpST = ssdpPacket.getST();

        Log.e(TAG, "receive ssdpPackage:" + ssdpST);

        if (ssdpST == null)
            return;

        boolean isRootDevice = isRootDevice();

        String devUSN = getUDN();
//        if (isRootDevice == true)
//            devUSN += "::" + USN.ROOTDEVICE;

        if (ST.isURNPri(ssdpST) == true) {
            String devType = getDeviceType();
            postSearchResponse(ssdpPacket, devType, devUSN);
        } else if (ST.isAllDevice(ssdpST) == true) {
            String devNT = getNotifyDeviceNT();
            int repeatCnt = (isRootDevice == true) ? 3 : 2;
            for (int n = 0; n < repeatCnt; n++)
                postSearchResponse(ssdpPacket, devNT, devUSN);
        } else if (ST.isRootDevice(ssdpST) == true) {
            if (isRootDevice == true)
                postSearchResponse(ssdpPacket, ST.ROOT_DEVICE, devUSN);
        } else if (ST.isUUIDDevice(ssdpST) == true) {
            String devUDN = getUDN();
            if (ssdpST.equals(devUDN) == true)
                postSearchResponse(ssdpPacket, devUDN, devUSN);
        } else if (ST.isURNDevice(ssdpST) == true) {
            String devType = getDeviceType();
            if (ssdpST.equals(devType) == true) {
                // Thanks for Mikael Hakman (04/25/05)
                devUSN = getUDN() + "::" + devType;
                postSearchResponse(ssdpPacket, devType, devUSN);
            }
        }
    }

    private static Calendar cal = Calendar.getInstance();

    public boolean postSearchResponse(SSDPPacket ssdpPacket, String st,
                                      String usn) {
        String localAddr = ssdpPacket.getLocalAddress();
        Device rootDev = getRootDevice();
        String rootDevLocation = rootDev.getLocationURL(localAddr);

        SSDPSearchResponse ssdpRes = new SSDPSearchResponse();
        ssdpRes.setLeaseTime(getLeaseTime());
        ssdpRes.setDate(cal);
        ssdpRes.setST(st);
        ssdpRes.setUSN(usn);
        ssdpRes.setLocation(rootDevLocation);
        ssdpRes.setBootId(getBootId());
        // Thanks for Brent Hills (10/20/04)
     //   ssdpRes.setMYNAME(getFriendlyName());

        int mx = ssdpPacket.getMX();
        TimerUtil.waitRandom(mx * 100);

        String remoteAddr = ssdpPacket.getRemoteAddress();
        int remotePort = ssdpPacket.getRemotePort();
        SSDPSearchResponseSocket ssdpResSock = new SSDPSearchResponseSocket();

        int ssdpCount = getSSDPAnnounceCount();
        for (int i = 0; i < ssdpCount; i++)
            ssdpResSock.post(remoteAddr, remotePort, ssdpRes);

        return true;
    }

    // //////////////////////////////////////////////
    // Root Device
    // //////////////////////////////////////////////

    public Device getRootDevice() {
        Node rootNode = getRootNode();
        if (rootNode == null)
            return null;
        Node devNode = rootNode.getNode(Device.ELEM_NAME);
        if (devNode == null)
            return null;
        return new Device(rootNode, devNode);
    }

    private boolean stop(boolean doByeBye) {
        if (doByeBye == true)
            byebye();

        SSDPSearchSocketList ssdpSearchSockList = getSSDPSearchSocketList();
        ssdpSearchSockList.stop();
        ssdpSearchSockList.close();
        ssdpSearchSockList.clear();

        Advertiser adv = getAdvertiser();
        if (adv != null) {
            adv.stop();
            setAdvertiser(null);
        }

        return true;
    }

    public boolean stop() {
        return stop(true);
    }

    public void byebye(String bindAddr) {
        SSDPNotifySocket ssdpSock = new SSDPNotifySocket(bindAddr);

        SSDPNotifyRequest ssdpReq = new SSDPNotifyRequest();
        ssdpReq.setNTS(NTS.BYEBYE);

        // uuid:device-UUID(::upnp:rootdevice)*
//        if (isRootDevice() == true) {
//            String devNT = getNotifyDeviceNT();
//            String devUSN = getNotifyDeviceUSN();
//            ssdpReq.setNT(devNT);
//            ssdpReq.setUSN(devUSN);
//            ssdpSock.post(ssdpReq);
//        }

        // uuid:device-UUID::urn:schemas-upnp-org:device:deviceType:v
        String devNT = getNotifyDeviceTypeNT();
        String devUSN = getNotifyDeviceTypeUSN();
        ssdpReq.setNT(devNT);
        ssdpReq.setUSN(devUSN);
        ssdpSock.post(ssdpReq);

        // Thanks for Mikael Hakman (04/25/05)
        ssdpSock.close();
    }

    public void byebye() {

        InetAddress[] binds = getDeviceData().getHTTPBindAddress();
        String[] bindAddresses;
        if (binds != null) {
            bindAddresses = new String[binds.length];
            for (int i = 0; i < binds.length; i++) {
                bindAddresses[i] = binds[i].getHostAddress();
            }
        } else {
            int nHostAddrs = HostInterface.getNHostAddresses();
            bindAddresses = new String[nHostAddrs];
            for (int n = 0; n < nHostAddrs; n++) {
                bindAddresses[n] = HostInterface.getHostAddress(n);
            }
        }

        for (int j = 0; j < bindAddresses.length; j++) {
            if (bindAddresses[j] == null || bindAddresses[j].length() <= 0)
                continue;
            int ssdpCount = getSSDPAnnounceCount();
            for (int i = 0; i < ssdpCount; i++)
                byebye(bindAddresses[j]);
        }
    }

}
