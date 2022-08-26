package cn.cc1w.app.ui.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import cn.cc1w.app.ui.application.AppContext;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.AppConfigEntity;
import cn.cc1w.app.ui.entity.AppModeEntity;
import cn.cc1w.app.ui.entity.UserInfoResultEntity;

/**
 * Created by kpinfo on 2018/8/8.
 */

public class SharedPreferenceUtil {
    private static final String PREFERNCE_FILE_NAME = "obj"; // 缓存文件名
    private static final String USER_GUIDE_FILE_NAME = "guide"; // 用户引导
    private static final String USER_INFO = "userInfo"; // 用户引导
    private static final String USER_TOKEN = "token";// token
    private static final String USER_AVATAR = "userAvatar";// 用户头像
    private static final String USER_NAME = "userName";// 用户名称
    private static final String HEIGHT_INPUT_METHOD = "height_input_method"; // 输入法高度
    private static final String STATE_USE_AUDIO_BROKE = "broke_audio_use_state"; // 爆料功能是否使用过
    private static final String OPEN_PUSH = "openPush";
    private static final String CONTENT_COMMENT = "comment"; // 评论
    private static final String OPEN_NOTIFICATION = "openNotification"; // 开启通知
    private static final String STATUS_POLICY = "policy"; // 隐私政策
    private static final String GRAY_MODE = "grayMode"; // 黑白模式
    private static final String KP_PUSH_REGISTER_STATUS = "kpPushRegister";
    private static final String KP_PUSH_REGISTER_ID = "kpPushRegisterId";
    private static final String APP_MODE_INFO = "appModeInfo";
    private static final String APP_CONFIG_INFO = "appConfigInfo";

    private static final String SPLASH_PERMISSION_STATUS = "splashPermissionStatus";

    /**
     * @param context 上下文对象
     *
     * @return 获取到的 对象信息
     */
    public static Object readObj(Context context, String key) {
        Object obj = null;
        SharedPreferences prefe = context.getSharedPreferences(
                PREFERNCE_FILE_NAME, 0);
        String replysBase64 = prefe.getString(key, "");
        if (TextUtils.isEmpty(replysBase64)) {
            return obj;
        }
        // 读取字节
        byte[] base64 = Base64CoderUtils.decode(replysBase64);
        // 封装到字节读取流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            // 封装到对象读取流
            ObjectInputStream ois = new ObjectInputStream(bais);
            try {
                // 读取对象
                obj = ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 存储一个对象
     *
     * @param context 上下文对象
     * @param obj     对象
     * @param key     保存的 key
     */
    public static <T> void saveObj(Context context, T obj, String key) {
        T _obj = obj;

        SharedPreferences prefe = context.getSharedPreferences(
                PREFERNCE_FILE_NAME, 0);
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流,封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(_obj);

            // 将字节流编码成base64的字符串
            String list_base64 = new String(Base64CoderUtils.encode(baos.toByteArray()));
            Editor editor = prefe.edit();
            editor.putString(key, list_base64);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存全局信息
     */
    public static void saveUserInfo(UserInfoResultEntity.UserInfo userInfo) {

        Context context = AppContext.getAppContext();
        if (null != context) {
            saveObj(context, userInfo, Constant.USER_INFO);
        }
    }

    /**
     * 取出全局信息
     *
     * @return 全局信息
     */
    public static UserInfoResultEntity.UserInfo getUserInfo() {

        Context context = AppContext.getAppContext();
        if (null != context) {
            return (UserInfoResultEntity.UserInfo) readObj(context, Constant.USER_INFO);
        }
        return null;
    }

    /**
     * 保存用户的 评论内容
     *
     * @param userCommentContent 评论内容
     */
    public static void saveUserCommentContent(String userCommentContent) {

        if (!TextUtils.isEmpty(userCommentContent)) {
            Context context = AppContext.getAppContext();
            if (null != context) {
                saveObj(context, userCommentContent, CONTENT_COMMENT);
            }
        }
    }

    /**
     * 清除用户的 评论内容
     */
    public static void clearUserCommentContent() {

        Context context = AppContext.getAppContext();
        if (null != context) {
            saveObj(context, "", CONTENT_COMMENT);
        }
    }

    /**
     * 获取用户保存的 评论内容
     *
     * @return 用户保存的评论内容
     */
    public static String getUserSaveCommentContent() {
        String content = "";
        Context context = AppContext.getAppContext();
        if (null != context) {

            return (null == readObj(context, CONTENT_COMMENT) ? "" : (String) readObj(context, CONTENT_COMMENT));
        }
        return content;
    }

    /**
     * 设置 APP的用户引导 状态
     *
     * @param isFirstUse 是否为第一场使用APP
     */
    public static void setUserGuide(boolean isFirstUse) {

        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USER_GUIDE_FILE_NAME, Context.MODE_PRIVATE);

            Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constant.IS_FIRST_USE, isFirstUse);
            editor.commit();
        }
    }

    /**
     * 获取 APP的引导状态 APP是否为第一次使用
     *
     * @return APP的使用状态
     */
    public static boolean getUserGuide() {
        boolean isFirstUse = true;

        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USER_GUIDE_FILE_NAME, Context.MODE_PRIVATE);
            isFirstUse = sharedPreferences.getBoolean(Constant.IS_FIRST_USE, true);
        }
        return isFirstUse;
    }

    /**
     * 设置 输入法的高度 状态
     *
     * @param height 输入法的高度
     */
    public static void setInputMethodHeight(int height) {

        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HEIGHT_INPUT_METHOD, Context.MODE_PRIVATE);

            Editor editor = sharedPreferences.edit();
            editor.putInt(Constant.HEIGHT_INPUT_METHOD, height);
            editor.commit();
        }
    }

    /**
     * 获取输入法的高度
     *
     * @return 输入法的高度
     */
    public static int getInputMethodHeight() {
        int height = Constant.HEIGHT_INPUT_METHOD_DEF;
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HEIGHT_INPUT_METHOD, Context.MODE_PRIVATE);
            height = sharedPreferences.getInt(Constant.HEIGHT_INPUT_METHOD, Constant.HEIGHT_INPUT_METHOD_DEF);
        }
        return height;
    }

    /**
     * 设置用户的 token
     *
     * @param token 当前保存的token
     */
    public static void setUserToken(String token) {

        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USER_TOKEN, Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            LogUtil.e("setUserToken = " + token);

            editor.putString("userToken", token);

            editor.commit();
        }
    }

    /**
     * 获取用户保存的 token
     *
     * @return 用户保存的token
     */
    public static String getUserToken() {
        String userToken = "";
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USER_TOKEN, Context.MODE_PRIVATE);
            userToken = sharedPreferences.getString("userToken", "");

        }
        return userToken;
    }

    /**
     * 设置用户的 头像
     *
     * @param userAvatar 用户的头像
     */
    public static void setUserAvatar(String userAvatar) {
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USER_AVATAR, Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();

            LogUtil.e("setUserAvatar = " + userAvatar);
            editor.putString(USER_AVATAR, TextUtils.isEmpty(userAvatar) ? "" : userAvatar);
            editor.commit();
        }
    }

    /**
     * 获取用户保存的 头像
     *
     * @return 用户保存的头像
     */
    public static String getUserAvatar() {
        String userToken = "";
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USER_AVATAR, Context.MODE_PRIVATE);
            userToken = sharedPreferences.getString(USER_AVATAR, "");
        }
        return userToken;
    }

    /**
     * 设置用户的 用户名称
     *
     * @param userName 当前保存的用户名称
     */
    public static void setUserName(String userName) {
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            LogUtil.e("setUserName = " + userName);
            editor.putString(USER_NAME, TextUtils.isEmpty(userName) ? "" : userName);
            editor.commit();
        }
    }

    /**
     * 获取用户保存的 名称
     *
     * @return 用户保存的用户名称
     */
    public static String getUserName() {
        String userToken = "";
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE);
            userToken = sharedPreferences.getString(USER_NAME, "");
        }
        return userToken;
    }

    /**
     * 爆料的录音功能使用状态
     *
     * @param state 爆料的录音功能使用状态
     */
    public static void setBrokeAudioUseState(boolean state) {

        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(STATE_USE_AUDIO_BROKE, Context.MODE_PRIVATE);

            Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constant.STATE_USE_AUDIO_BROKE, state);
            editor.commit();
        }
    }

    /**
     * 获取爆料中的额录音使用状态
     *
     * @return 爆料功能的录音的使用状态
     */
    public static boolean getBrokeAudioUseState() {
        boolean useState = false;

        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(STATE_USE_AUDIO_BROKE, Context.MODE_PRIVATE);
            useState = sharedPreferences.getBoolean(Constant.STATE_USE_AUDIO_BROKE, false);
        }
        return useState;
    }

    /**
     * 设置 是否开启 推送
     *
     * @param state push 是否开启
     */
    public static void setPushState(boolean state) {

        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(OPEN_PUSH, Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.putBoolean("pushState", state);
            editor.commit();
        }
    }

    /**
     * 获取push 是否开启
     *
     * @return push 开启状态
     */
    public static boolean getPushState() {
        boolean useState = true;

        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(OPEN_PUSH, Context.MODE_PRIVATE);
            useState = sharedPreferences.getBoolean("pushState", false);
        }
        return useState;
    }

    /**
     * 设置 是否以后会继续提示推送弹窗
     *
     * @param state 用户设置以后是否会继续出现 推送弹窗提示
     */
    public static void setNeverAlterPhoneNotification(boolean state) {
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(OPEN_PUSH, Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(OPEN_NOTIFICATION, state);
            editor.commit();
        }
    }

    /**
     * 获取 是否以后会继续提示推送弹窗
     *
     * @return 用户设置以后是否会继续出现 推送弹窗提示
     */
    public static boolean isNeverAlterPhoneNotification() {
        boolean useState = false;
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(OPEN_PUSH, Context.MODE_PRIVATE);
            useState = sharedPreferences.getBoolean(OPEN_NOTIFICATION, false);
        }
        return useState;
    }

    /**
     * 设置隐私政策dialog 是否显示
     *
     * @param state 用户设置以后是否会继续出现 推送弹窗提示
     */
    public static void setPolicyState(boolean state) {
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(STATUS_POLICY, Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(STATUS_POLICY, state);
            editor.commit();
        }
    }

    /**
     * 获取隐私政策dialog 的显示情况
     *
     * @return 用户设置以后是否会继续出现 推送弹窗提示
     */
    public static boolean getPolicyState() {
        boolean isShowPolicyState = false;
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(STATUS_POLICY, Context.MODE_PRIVATE);
            isShowPolicyState = sharedPreferences.getBoolean(STATUS_POLICY, true);
        }
        return isShowPolicyState;
    }

    /**
     * 设置是否显示黑白模式
     */
    public static synchronized void setGrayModeState(boolean state) {
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(GRAY_MODE, Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(GRAY_MODE, state);
            editor.commit();
        }
    }

    /**
     * 获取黑白模式的 的显示情况
     */
    public static synchronized boolean getGrayModeState() {
        boolean isShowPolicyState = false;
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(GRAY_MODE, Context.MODE_PRIVATE);
            isShowPolicyState = sharedPreferences.getBoolean(GRAY_MODE, false);
        }
        return isShowPolicyState;
    }

    /**
     * 设置开屏推送的推送状态
     */
    public static synchronized void setKPushRegisterState(boolean state) {
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(KP_PUSH_REGISTER_STATUS, Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(KP_PUSH_REGISTER_STATUS, state);
            editor.commit();
        }
    }

    /**
     * 获取开屏推送的推送状态
     */
    public static synchronized boolean getKPushRegisterState() {
        boolean isShowPolicyState = false;
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(KP_PUSH_REGISTER_STATUS, Context.MODE_PRIVATE);
            isShowPolicyState = sharedPreferences.getBoolean(KP_PUSH_REGISTER_STATUS, false);
        }
        return isShowPolicyState;
    }

    /**
     * 设置开屏推送的推送 id
     */
    public static synchronized void setKPushRegisterId(String pushId) {
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(KP_PUSH_REGISTER_ID, Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.putString(KP_PUSH_REGISTER_ID, pushId);
            editor.commit();
        }
    }

    /**
     * 获取开屏推送的推送id
     */
    public static synchronized String getKPushRegisterId() {
        String kPushId = "";
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(KP_PUSH_REGISTER_ID, Context.MODE_PRIVATE);
            kPushId = sharedPreferences.getString(KP_PUSH_REGISTER_ID, "");
        }
        return kPushId;
    }

    /**
     * 保存APP模式信息
     */
    public static void setAppModeInfo(AppModeEntity.DataBean appModeInfo) {
        Context context = AppContext.getAppContext();
        if (null != context) {
            saveObj(context, appModeInfo, APP_MODE_INFO);
        }
    }

    /**
     * 取出APP模式信息
     */
    public static AppModeEntity.DataBean getAppModeInfo() {
        Context context = AppContext.getAppContext();
        if (null != context) {
            return (AppModeEntity.DataBean) readObj(context, APP_MODE_INFO);
        }
        return null;
    }

    /**
     * 取出APP配置信息
     */
    public static AppConfigEntity.AppConfigDetail getAppConfigInfo() {
        Context context = AppContext.getAppContext();
        if (null != context) {
            return (AppConfigEntity.AppConfigDetail) readObj(context, APP_CONFIG_INFO);
        }
        return null;
    }

    /**
     * 保存APP配置信息
     */
    public static void setAppConfigInfo(AppConfigEntity.AppConfigDetail appConfigInfo) {
        Context context = AppContext.getAppContext();
        if (null != context && appConfigInfo!= null) {
            saveObj(context, appConfigInfo, APP_CONFIG_INFO);
        }
    }

    /**
     * 设置 Splash 权限是点击 状态
     */
    public synchronized static void setSplashPermissionStatus(boolean isClicked) {
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SPLASH_PERMISSION_STATUS, Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(SPLASH_PERMISSION_STATUS, isClicked);
            editor.apply();
        }
    }

    /**
     * 取出Splash权限是点击 状态 [Splash 是否点击过 拒绝]
     */
    public synchronized static boolean getSplashPermissionStatus() {
        boolean isShowPolicyState = false;
        Context context = AppContext.getAppContext();
        if (null != context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SPLASH_PERMISSION_STATUS, Context.MODE_PRIVATE);
            isShowPolicyState = sharedPreferences.getBoolean(SPLASH_PERMISSION_STATUS, false);
        }
        return isShowPolicyState;
    }
}