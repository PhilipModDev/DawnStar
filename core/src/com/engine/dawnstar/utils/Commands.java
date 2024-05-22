package com.engine.dawnstar.utils;

import com.engine.dawnstar.server.DawnStarServer;

public class Commands {

    public static boolean allowUserCommand(String command, DawnStarServer dawnStarServer) {
        int index = command.indexOf(' ');
        if (index == -1) return true;
        index += 1;
        String username = command.substring(index);
        if (username.isBlank()) return true;
        if (dawnStarServer.userList.isAllow(username)) {
            DawnStarServer.LOGGER.info("User already added "+ username);
            return true;
        }
        dawnStarServer.userList.setAllow(username);
        DawnStarServer.LOGGER.info("Added user "+ username);
        return false;
    }
}
