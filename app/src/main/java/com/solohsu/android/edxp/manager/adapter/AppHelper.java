package com.solohsu.android.edxp.manager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import com.solohsu.android.edxp.manager.R;
import com.topjohnwu.superuser.Shell;

import java.util.List;

public class AppHelper {

    private static final String BASE_PATH = "/data/misc/riru/modules/edxposed/";
    private static final String WHITE_LIST_PATH = BASE_PATH + "whitelist/";
    private static final String BLACK_LIST_PATH = BASE_PATH + "blacklist/";
    private static final String WHITE_LIST_MODE = BASE_PATH + "usewhitelist";
    private static final String FORCE_GLOBAL_MODE = BASE_PATH + "forceglobal";

    public static boolean makeSurePath() {
        return checkRetCode(Shell.su("mkdir " + WHITE_LIST_PATH,
                "mkdir " + BLACK_LIST_PATH).exec().getCode());
    }

    public static boolean isWhiteListMode() {
        return Shell.su("test -e " + WHITE_LIST_MODE + "; echo $?").exec()
                .getOut().get(0).equals("0");
    }

    public static boolean setWhiteListMode(boolean isWhiteListMode) {
        return isWhiteListMode ?
                checkRetCode(Shell.su("touch " + WHITE_LIST_MODE).exec().getCode()) :
                checkRetCode(Shell.su("rm " + WHITE_LIST_MODE).exec().getCode());
    }

    public static boolean addWhiteList(String packageName) {
        return checkRetCode(Shell.su(whiteListFileName(packageName, true)).exec().getCode());
    }

    public static boolean addBlackList(String packageName) {
        return checkRetCode(Shell.su(blackListFileName(packageName, true)).exec().getCode());
    }

    public static boolean removeWhiteList(String packageName) {
        return checkRetCode(Shell.su(whiteListFileName(packageName, false)).exec().getCode());
    }

    public static boolean removeBlackList(String packageName) {
        return checkRetCode(Shell.su(blackListFileName(packageName, false)).exec().getCode());
    }

    public static List<String> getBlackList() {
        return Shell.su("ls " + BLACK_LIST_PATH).exec().getOut();
    }

    public static List<String> getWhiteList() {
        return Shell.su("ls " + WHITE_LIST_PATH).exec().getOut();
    }

    private static String whiteListFileName(String packageName, boolean isAdd) {
        return (isAdd ? "touch " : "rm ") + WHITE_LIST_PATH + packageName;
    }

    private static String blackListFileName(String packageName, boolean isAdd) {
        return (isAdd ? "touch " : "rm ") + BLACK_LIST_PATH + packageName;
    }

    private static boolean checkRetCode(int retCode) {
        return retCode != com.topjohnwu.superuser.Shell.Result.JOB_NOT_EXECUTED;
    }

    public static boolean addPackageName(boolean isWhiteListMode, String packageName) {
        return isWhiteListMode ? addWhiteList(packageName) : addBlackList(packageName);
    }

    public static boolean removePackageName(boolean isWhiteListMode, String packageName) {
        return isWhiteListMode ? removeWhiteList(packageName) : removeBlackList(packageName);
    }

    public static boolean setForceGlobalMode(boolean isForceGlobal) {
        return checkRetCode(Shell.su((isForceGlobal ? "touch " : "rm ") + FORCE_GLOBAL_MODE).exec().getCode());
    }

    @SuppressLint("RestrictedApi")
    public static void showMenu(@NonNull Context context,
                                @NonNull FragmentManager fragmentManager,
                                @NonNull View anchor,
                                @NonNull ApplicationInfo info) {
        PopupMenu appMenu = new PopupMenu(context, anchor);
        appMenu.inflate(R.menu.menu_app_item);
        appMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.app_menu_compile_reset:
                    CompileUtils.reset(context, fragmentManager, info);
                    break;
                case R.id.app_menu_compile_speed:
                    CompileUtils.compileSpeed(context, fragmentManager, info);
                    break;
            }
            return true;
        });
        MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) appMenu.getMenu(), anchor);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }
}