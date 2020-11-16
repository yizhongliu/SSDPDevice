package com.iview.common.utils;

public class CommandUtil {
    public static final int COMMAND_SET_URI = 0;
    public static final int COMMAND_PLAY = 1;
    public static final int COMMAND_STOP = 2;
    public static final int COMMAND_PAUSE = 3;
    public static final int COMMAND_ADD_PLAYLIST = 4;
    public static final int COMMAND_DEL_PLAYLIST = 5;
    public static final int COMMAND_SET_PLAYMODE = 6;
    public static final int COMMAND_DEL_CURRENT_PLAYFILE = 7;
    public static final int COMMAND_PLAY_INDEX = 8;


    public static final String COMMAND_TYPE_MAINTAIN = "maintain";
    public static final String COMMAND_MSG_DOWNLOAD = "download";
    public static final String COMMAND_MSG_DELETE = "delete";
    public static final String COMMAND_MSG_STATUS = "status";

    public static final String COMMAND_TYPE_PLAYLIST = "playlist";
    public static final String COMMAND_MSG_INSERT = "insert";
    public static final String COMMAND_MSG_APPEND = "append";
    public static final String COMMAND_MSG_PLAYMODE = "playMode";
    public static final String COMMAND_MSG_PLAYORDER = "playOrder";
    public static final String COMMAND_MSG_FORCEUPDATE = "forceUpdate"; //  后台和logo灯同步播放列表的消息

    public static final String COMMAND_TYPE_OPERATION = "operation";

    public static final String COMMAND_MSG_PLAY = "play";
    public static final String COMMAND_MSG_STOP = "stop";
    public static final String COMMAND_MSG_PAUSE = "pause";
    public static final String COMMAND_MSG_NEXT = "next";
    public static final String COMMAND_MSG_PREVIOUS = "previous";

    public static final String COMMAND_MSG_SETKEYSTONE = "setKeystone";

    public static final String COMMAND_TYPE_STATE = "stateChange";

    //websocket
    public static final String SOCKET_TYPE_CONTROL = "Control";
    public static final String SOCKET_TYPE_PATHPLANING = "PathPlanning";
    public static final String SOCKET_TYPE_AUTORUNNING = "AutoRunning";

    public static final String SOCKET_ACTION_MOVE = "Move";
    public static final String SOCKET_ACTION_SHOW = "Show";
    public static final String SOCKET_ACTION_START = "Start";
    public static final String SOCKET_ACTION_STOP = "Stop";
    public static final String SOCKET_ACTION_SETPARAM = "SetParam";
    public static final String SOCKET_ACTION_PREVIEW = "Preview";
    public static final String SOCKET_ACTION_CANCEL = "Cancel";

    public static final String COMMAND_TYPE_PATHLIST = "Pathlist";
}
