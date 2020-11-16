package com.iview.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.iview.common.base.BaseApplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class DeviceUtil {
    public static final String TAG = "DeviceUtil";

    public static final int PLAY_MODE_SINGLE = 0;
    public static final int PLAY_MODE_SINGLE_LOOP = 1;
    public static final int PLAY_MODE_ORDER = 2;
    public static final int PLAY_MODE_ALL_LOOP = 3;

    public static final String PLAY_MODE = "playMode";
    public static final String PREFERENCES_FILE = "play_preferences";

    public static final String PERFERENCES_KEYSTONE = "keystone";

    public static final int CONFIT_DISCONNECTED = -1;
    public static final int CONFIT_CONNECTED = 0;
    public static final int CONFIT_CONNECTING = 1;

    public static final int PLAYER_STATE_STOP = 0;
    public static final int PLAYER_STATE_PAUSE = 1;
    public static final int PLAYER_STATE_PLAY = 2;

  //  public static final String BASE_URL = "http://47.107.119.132:80/";
 //   public static final String BASE_URL = "http://47.107.243.155:80/";  //test server
    public static final String BASE_URL = "https://www.magneticshow.com/";  //release server
  //public static final String BASE_URL = "http://192.168.0.141:8090/";  //test server

    public static final String WEBSOCKET_URL = "ws://47.107.243.155:8080/echo?";
    public static final String CHECK_WEBSOCKET_URL = "http://47.107.243.155:8080";

    public static final int MONITOR_INTERVAL_TIME = 1000 * 60 * 5;  //  ms  心跳时间

    public static enum eRunningMode {
        FREE_CONTROL,
        PATH_PLANNING,
        AUTO_RUNNING
    };

    public enum eFileResopne {
        success,
        failed,
        partial_success
    };

    //给手机端反馈的播放器状态
    public enum ePlayState {
        playing,
        paused,
        stopped
    };

    public static boolean isEmpty(String s) {
        if (null == s)
            return true;
        if (s.length() == 0)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
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

    public static String getAndroidOsSystemProperties(String key) {
        Method systemProperties_get = null;
        String ret;
        try {
            systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get",
                    String.class);
            if ((ret = (String) systemProperties_get.invoke(null, key)) != null)
                return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return "";
    }

    public static byte[] intToButeArray(int n) {
        byte[] byteArray = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteOut);
            dataOut.writeInt(n);
            byteArray = byteOut.toByteArray();
            for (byte b : byteArray) {
                System.out.println(" " + b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @return 当前可用内存。
     */
    public static long getAvailableMemory() {
        long availableMemorySize = 0;
        String dir = "/proc/meminfo";

        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(dir);
            br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            while (memoryLine != null) {
                if (memoryLine.contains("MemAvailable:")) {
                    String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemAvailable:"));
                    availableMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
                    break;
                }

                memoryLine = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }

                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return availableMemorySize;
    }
    /**
     * 获取系统总内存,返回字节单位为KB
     * @return 系统总内存
     */
    public static long getTotalMemory() {
        long totalMemorySize = 0;
        String dir = "/proc/meminfo";

        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(dir);
            br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            //将非数字的字符替换为空
            totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }

                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return totalMemorySize;
    }

    /**
     * 获取总的CPU使用率
     * @return CPU使用率
     */
    public static float getTotalCpuRate() {
        String dir = "/proc/stat";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            LogUtils.e(TAG, "read stat:" + memoryLine);
            //       String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            //将非数字的字符替换为空
            //      totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;

//        float totalCpuTime1 = getTotalCpuTime();
//        float idletime1 = getIdleCpuTime();
//        float totalUsedCpuTime1 = totalCpuTime1 - idletime1;
//        try {
//            Thread.sleep(360);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        float totalCpuTime2 = getTotalCpuTime();
//        float idletime2 = getIdleCpuTime();
//        float totalUsedCpuTime2 = totalCpuTime2 - idletime2;
//        float cpuRate = 100 * (totalUsedCpuTime2 - totalUsedCpuTime1)
//                / (totalCpuTime2 - totalCpuTime1);
//        return cpuRate;
    }
    /**
     * 获取系统总CPU使用时间
     * @return 系统CPU总的使用时间
     */
    public static long getTotalCpuTime()
    {
        String[] cpuInfos = null;
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
       long totalCpu = Long.parseLong(cpuInfos[2])
       + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
       + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
       + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);

        return totalCpu;
    }

    /**
     * 获取系统总CPU空闲时间
     * @return 系统CPU总的使用时间
     */
    public static long getIdleCpuTime()
    {
        String[] cpuInfos = null;
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        long idletime = Long.parseLong(cpuInfos[5]);
        return idletime;
    }

    public static String getVersionName() throws Exception{
        PackageManager packageManager = BaseApplication.getContext().getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(BaseApplication.getContext().getPackageName(), 0);
        return packInfo.versionName;
    }


    public static String getSystemTime(){
        long time = DateUtils.getCurTimeLong();
        String sTime = null;
        try {
            sTime = DateUtils.getDateToString(time, "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sTime;
    }

    public static void bubbleSort(int[] datas) {
        int size = datas.length;
        if (size <= 1) {
            return;
        }

        int temp;
        for (int i = 0; i < size-1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                if (datas[j] > datas[j+1]) {
                    temp = datas[j];
                    datas[j] = datas[j+1];
                    datas[j+1] = temp;
                }
            }
        }
    }

    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        String token = preferences.getString("token", "");
        return token;
    }
}
