package cn.cc1w.app.ui.constants;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;

import cn.cc1w.app.ui.BuildConfig;
import cn.cc1w.app.ui.widget.video.LiveItemVideoPlayer;

/**
 * APP 的常量类
 *
 * @author kpinfo
 */
public class Constant {
    /**
     * 音频转换器是否初始化成功
     */
    public static boolean IS_AUDIO_CONVERT_FINISHED = false;
    //====================常量begin========================//
    /**
     * 测试地址
     */
//    private static final String DOMAIN = "https://apisaastest.kpinfo.cn/api/";

    //正式地址
//    private static final String DOMAIN = "https://appkptest.ccwb.cn/api/";
    private static final String DOMAIN = "https://appkp.ccwb.cn/api/";
    /**
     * 聊天室Socket地址
     */
    public static String HOST_SOCKET = "wss://socketkp.ccwb.cn";
    /**
     * 上传趣看 的资源服务器地址
     */
    public static String DOMAIN_FILE_UPLOAD_QUKAN = "https://filekp.ccwb.cn";

    public static boolean IS_POLICY_NEED_AUTHORIZATION = true;

    /**
     * 平台对应的id
     */
    public static final String ID_APP = "8";
    /**
     * 平台对应的key
     */
    public static final String KEY_APP = "kp529b8af5def5b889";
    /**
     * 平台对应的secret
     */
    public static final String SECRET_APP = "9e0847b96382b0cd5e9d066646f421f8";

    /**
     * 友盟 的Key
     */
    public static final String KEY_YOUMENG = "5f17ea2bd62dd10bc71ad48f";

    /**
     * 微信 的appid
     */
    public static final String WX_APP_ID = "wx40e15e8bcce18854";
    /**
     * 微信的appSecret
     */
    public static final String WX_APP_SECRET = "0269aebaa2a77888d93fbb353776f342";
    /**
     * 新浪微博的 appid
     */
    public static final String SINA_APP_ID = "853638293";
    /**
     * 新浪微博的 appKey
     */
    public static final String SINA_APP_KEY = "85e79e5c8d10ebaca8ec13a27cc4f4ce";
    /**
     * QQ的 appid
     */
    public static final String QQ_APP_ID = "1101463256";
    /**
     * QQ的 appKey
     */
    public static final String QQ_APP_KEY = "ujIK1wAbhkZofcUs";

    public static boolean IS_LOCATION_INIT_SUCCESS = false;
    public static boolean IS_PUSH_INIT_SUCCESS = false;

    public static boolean isHasFetchVersion = false;
    /**
     * 日志系统 的 ID
     */
    public static final String ID_LOG = "8";

    public static final String SECRET_LOG = "4a3bb1de48cf4d0cb4e6a96db8d55e26";

    public static final long FREQUENCY_LOCATION_UPDATE = 30 * 60 * 1000;
    /**
     * 退出登录
     */
    public static final String TAG_LOGOUT = "logout";
    /**
     * 从 Paikew 录制过来
     */
    public static final String RECORD_PAIKEW = "paikewRecord";
    /**
     * 爆料过来
     */
    public static final String RECORD_BROKE = "broke";
    /**
     * 从 Paikew 评论过来
     */
    public static final String TAG_COMMENT_PAIKEW = "paikewComment";
    /**
     * 从 web登录过来
     */
    public static final String LOGIN_WEB = "webLogin";

    /**
     * 绑定手机号
     */
    public static final String PHONE_BIND = "bindPhoneNumber";
    /**
     * 网络请求成功
     */
    public static final int CODE_REQUEST_SUCCESS = 200;
    /**
    /**
     * 服务端出问题
     */
    public static final String EXCEPTION_SERVER = "网络异常，请稍后再试";
    /**
     * 评论失败
     */
    public static final String ERROR_COMMENT = "网络异常，请稍后再试";
    /**
     * 收藏失败
     */
    public static final String ERROR_COLLECTION = "网络异常，请稍后再试";
    /**
     * 密码最小长度˙
     */
    public static final int MIN_LENGTH_PASSWORD = 6;// 密码的最少长度

    /**
     * 定位频率
     */
    public static final int TIME_DURATION_LOCATION = 30 * 60 * 1000;
    /**
     * 退出的时间
     */
    public static final int TIME_QUIT = 2 * 1000;
    /**
     * 倒计时总时长
     */
    public static final int TOTAL_TIME = 60 * 1000; // 倒计时的总时长
    /**
     * 时间间隔
     */
    public static final int TIME_INTERVAL = 1000; //  倒计时的 时间间隔
    /**
     * 关闭 Splash Tag
     */
    public static final String TAG_SPLASH_FINISH = "finishSplashActivity";
    /**
     * 更新 应用号状态
     */
    public static final String STATE_APPS_UPDATE = "updateAppsState";
    /**
     * 倒计时提示文字
     */
    public static final String SEND_VERIFICATION_TRY = "s后重新获取";
    /**
     * 获取验证码提示文字
     */
    public static final String SEND_VERIFICATION = "获取验证码";
    /**
     * 是否首次使用
     */
    public static final String IS_FIRST_USE = "isFirstUse";
    /**
     * 输入法的高度
     */
    public static final String HEIGHT_INPUT_METHOD = "height_input_method";
    /**
     * 输入法默认高度
     */
    public static final int HEIGHT_INPUT_METHOD_DEF = 803;
    /**
     * 当前的 输入法高度
     */
    public static int HEIGHT_INPUT_METHOD_CURRENT;

    /**
     * pageIndex
     */
    public static final String STR_INDEX_PAGE = "pageindex";
    /**
     * 访问终端
     */
    public static final String CW_CLIENT = "android";
    /**
     * 客户端
     */
    public static final String STR_CW_CLIENT = "cw_client";
    /**
     * 访问设备
     */
    public static final String CW_DEVICE = "android";
    /**
     * 设备信息
     */
    public static final String STR_CW_DEVICE = "cw_device";
    /**
     * 访问设备的系统
     */
    public static final String CW_OS = "android";
    /**
     * 系统类型
     */
    public static final String STR_CW_OS = "cw_os";
    /**
     * 设备类型
     */
    public static final String CW_MACHINE_TYPE = "app";
    /**
     * 机器码
     */
    public static final String STR_CW_MACHINE_TYPE = "cw_machine_type";
    /**
     * 设备唯一码 字段
     */
    public static final String STR_CW_MACHINE_ID = "cw_machine_id";
    /**
     * 用户当前处于的国家 字段
     */
    public static final String STR_CW_COUNTRY = "cw_country";
    /**
     * 用户当前处于的 省份 字段
     */
    public static final String STR_CW_PROVINCE = "cw_province";
    /**
     * 用户当前处于的 城市 字段
     */
    public static final String STR_CW_CITY = "cw_city";
    /**
     * 用户当前处于的区 字段
     */
    public static final String STR_CW_AREA = "cw_area";
    /**
     * 用户当前处于的 纬度 字段
     */
    public static final String STR_CW_LATITUDE = "cw_latitude";
    /**
     * 用户当前处于的 经度 字段
     */
    public static final String STR_CW_LONGITUDE = "cw_longitude";
    /**
     * 网络连接超时时间
     */
    public static final int TIME_OUT_REQUEST = 15 * 1000;
    /**
     * http header的连接
     */
    public static final String CONNECTION = "Connection";
    /**
     * http header的连接 的默认值
     */
    public static final String STR_CONNECTION = "close";
    /**
     * 设置 网络请求最大的 重试数量
     */
    public static final int TIMES_RETRY_NETWORK = 0;
    /**
     * 当前的 token
     */
    public static String CW_AUTHORIZATION = "";
    /**
     * 用户头像
     */
    public static String CW_AVATAR = "";
    /**
     * MainActivity 是否执行了 onCreate方法
     */
    public static boolean isMainActivityCreated = false;
    /**
     * 其他拍客的uid
     */
    public static String UID_PAIKEW_OTHER = "";
    /**
     * 用户 uid 【主系统】
     */
    public static String CW_UID_SYSTEM = "";
    /**
     * uid
     */
    public static final String STR_CW_UID_SYSTEM = "uid";

    /**
     * page
     */
    public static final String STR_PAGE = "page";
    /**
     * 拍客中的 uid
     */
    public static String CW_UID_PAIKEW = "";
    /**
     * 拍客Uid
     */
    public static String STR_CW_UID_PAIKEW = "paikewUid";

    /**
     * 开屏推送 id
     */
    public static final String STR_CW_ID_KPUSH = "kpush_regId";

    public static String CW_ID_KPUSH = "";

    /**
     * 用户名称
     */
    public static String CW_USERNAME = "";
    /**
     * 请求接口的时候的 授权token
     */
    public static final String STR_CW_AUTHORIZATION = "cw-authorization";
    /**
     * 如果拿到的 响应码 为40403 则。需要去绑定手机号码
     */
    public static final int CODE_FOR_PHONE_BIND = 40403;
    /**
     * 如果拿到的 未登录
     */
    public static final int CODE_FOR_LOGIN = 401;
    /**
     * 推送是否被打开
     */
    public static boolean isPushMessageOpened = false;
    /**
     * 下载完成 TAG
     */
    public static final String TAG_DOWNLOAD_COMPLETE = "downloadComplete";
    /**
     * 刷新数据
     */
    public static final String TAG_REFRESH = "refresh";

    /**
     * 收藏新闻
     */
    public static final String TAG_COLLECTION = "collection";

    /**
     * 上报
     */
    public static final String TAG_REPORT = "report";

    /**
     * 缓存地址
     */
    public static final String DIR_CACHE = "ccwb";
    /**
     * 缓存 后缀
     */
    public static final String SUFFIX_CACHE = ".cache";
    /**
     * 两次点击最小时间间隔
     */
    public static final long MIN_TIME_INTERVAL = 500;
    //====================常量end========================//
    /**
     * 分享的title
     */
    public static final String TILE_SHARE = "理想生活，即刻开屏";
    /**
     * 分享 内容
     */
    public static final String SUMMARY_SHARE = "理想生活，即刻开屏";
    /**
     * 用户当前使用的网络的 ip 字段
     */
    public static final String STR_CW_IP = "cw_ip";
    /**
     * 设备ID
     */
    public static String CW_MACHINE_ID = "";
    /**
     * 手机类型
     */
    public static String TYPE_MOBILE = "";
    /**
     * 趣看的userId
     */
    public static final String ID_USER_QUKAN = "1546852074723973";
    /**
     * 趣看的 appKey
     */
    public static final String APPKEY_QUKAN = "hmbtivyaxi0zk3hz";

    public static final String KEY_SM4 = "de459e3528b7e8a062e16d261fab191c";
    /**
     * 爆料中录音的使用状态
     */
    public static boolean STATE_USE_BROKE = false;
    /**
     * 爆料中 录音使用的情况
     */
    public static final String STATE_USE_AUDIO_BROKE = "state_use_audio_broke";
    /**
     * 用户当前处于的国家
     */
    public static String CW_COUNTRY = "";
    /**
     * 用户当前 处于的省份
     */
    public static String CW_PROVINCE = "";
    /**
     * 用户当前处于的 城市
     */
    public static String CW_CITY = "";
    /**
     * 用户当前处于的 区
     */
    public static String CW_AREA = "";
    /**
     * 用户当前处于的 纬度
     */
    public static String CW_LATITUDE = "";
    /**
     * 用户当前处于的 经度
     */
    public static String CW_LONGITUDE = "";
    /**
     * 网页调用 原生用  webView重新加载的 url
     */
    public static String URL_RELOAD_WEBVIEW = "";
    /**
     * 地址
     */
    public static String ADDRESS = "";
    /**
     * 用户当前的ip
     */
    public static String CW_IP = "";
    /**
     * APP 当前的版本号
     */
    public static String CW_VERSION = BuildConfig.VERSION_NAME;
    /**
     * Tab最小滑动条目
     */
    public static final int CNT_SCROLL_MIN = 4;
    /**
     * 版本
     */
    public static final String STR_CW_VERSION = "cw_version";
    /**
     * 网络型号
     */
    public static String CW_NETWORKTYPE = "WIFI";
    /**
     * 网络类型
     */
    public static final String STR_CW_NETWORKTYPE = "cw_networktype";
    /**
     * 设备model
     */
    public static final String STR_CW_DEVICEMODEL = "cw_devicemodel";
    /**
     * 直播播放器
     */
    public static LiveItemVideoPlayer mCurrentVideoPlayer;
    /**
     * 定位的执行间隔时间
     */
    public static final int TIME_INTERVAL_LOCATION = 60 * 1000;
    /**
     * 分享的类型 ----- 新闻
     */
    public static final String TYPE_SHARE_NEWS = "news";
    //================== 接口 begin =========================
    /**
     * 发送短信验证码
     */
    public static final String MESSAGE_SEND = DOMAIN.concat("v1/auth/sms");
    /**
     * 手机快捷登录
     */
    public static final String LOGIN_FAST_USER = DOMAIN.concat("v1/auth/login");
    /**
     * 第三方登录
     */
    public static final String LOGIN_THREE_PARTY = DOMAIN.concat("v1/auth/login");
    /**
     * 获取用户 【用户中心】
     */
    public static final String USER_INFO = DOMAIN.concat("v1/user/getUser");
    /**
     * 获取用户 信息【账号管理】
     */
    public static final String USER_INFO_ACCONT_MANAGE = DOMAIN.concat("v1/user/getUserInfo");
    /**
     * 手机账号密码注册
     */
    public static final String USER_ACCOUNT_REGISTER = DOMAIN.concat("v1/auth/registerMobile");
    /**
     * 获取天气预报
     */
    public static final String WEATHER = DOMAIN.concat("v1/getWeather");
    /**
     * 更新头像 【用户信息】】
     */
    public static final String AVATAR_UPDATE = DOMAIN.concat("v1/user/updateHeadpic");
    /**
     * 更新昵称 【用户信息】
     */
    public static final String NICKNAME_UPDATE = DOMAIN.concat("v1/user/updateNickname");
    /**
     * 更新手机号码 【用户信息】
     */
    public static final String MOBILE_UPDATE = DOMAIN.concat("v1/user/updateMobile");
    /**
     * 绑定手机号码 【用户信息】
     */
    public static final String MOBILE_BIND = DOMAIN.concat("v1/user/bindMobile");
    /**
     * 绑定 QQ ,微博,微信等第三方平台
     */
    public static final String THREE_PARTY_BIND = DOMAIN.concat("v1/user/bindThird");
    /**
     * 更新密码
     */
    public static final String PASSWORD_UPDATE = DOMAIN.concat("v1/user/updatePassword");
    /**
     * 手机密码登录
     */
    public static final String LOGIN = DOMAIN.concat("v1/auth/login");
    /**
     * 密码 找回
     */
    public static final String PASSWORD_FIND = DOMAIN.concat("v1/auth/findPassword");
    /**
     * 获取热门话题标签接口
     */
    public static final String LIST_TOPIC_HOT = DOMAIN.concat("v1/shoot/getHotTopic");
    /**
     * 动新闻列表
     */
    public static final String NEWSY_ACTIVE_LIST = DOMAIN.concat("v1/news/getDynamicNewsLists");
    /**
     * 获取 开机广告
     */
    public static final String ADVERTISEMENT_START_UP = DOMAIN.concat("v1/ad/getAdStartupLists");
    /**
     * 获取用户收藏列表
     */
    public static final String LIST_COLLECTION = DOMAIN.concat("v1/news/getUserNewsCollectionLists");
    /**
     * 获取浏览的新闻历史记录
     */
    public static final String LIST_HISTORY = DOMAIN.concat("v1/news/getUserNewsHistoryLists");
    /**
     * 意见反馈
     */
    public static final String FEED_BACK = DOMAIN.concat("v1/system/addFeedback");
    /**
     * 获取滚动的应用号
     */
    public static final String LIST_APPS_CHANNEL_SCROLL = DOMAIN.concat("v1/channel/getChannelScrollLists");
    /**
     * 取消新闻收藏 【收藏列表中】
     */
    public static final String DELETE_COLLECTION_ITEM_LIST = DOMAIN.concat("v1/news/cancelUserNewsCollection");
    /**
     * 清空收藏列表
     */
    public static final String CLEAR_COLLECTION_LIST = DOMAIN.concat("v1/news/cancelAllUserNewsCollection");
    /**
     * 删除 历史记录条目
     */
    public static final String DELETE_HISTORY_ITEM_LIST = DOMAIN.concat("v1/news/cancelUserNewsHistory");
    /**
     * 清空 历史记录条目
     */
    public static final String CLEAR_HISTORY_ITEM_LIST = DOMAIN.concat("v1/news/cancelAllUserNewsHistory");
    /**
     * 获取 我的通知列表
     */
    public static final String USER_NOTICE_LIST = DOMAIN.concat("v1/notice/getNoticeLists");
    /**
     * 获取 用户新闻评论 列表 【用户作出的评论】
     */
    public static final String USER_COMMENT_LIST = DOMAIN.concat("v1/news/getUserNewsCommentLists");
    /**
     * 新闻评论 点赞【消息与通知中】
     */
    public static final String APPRECIATE_LIST_COMMENT = DOMAIN.concat("v1/news/addNewsCommentPraise");
    /**
     * 应用号分类列表[应用号顶部的 title]
     */
    public static final String TITLE_APPS_LIST = DOMAIN.concat("v1/channel/getChannelGroupRecommendLists");
    /**
     * 获取 应用号 title对应的列表
     */
    public static final String APPS_LIST = DOMAIN.concat("v1/channel/getChanelLists");
    /**
     * 我的最爱中的 应用号的分类[热门推荐]
     */
    public static final String RECOMMEND_LIST_APPS = DOMAIN.concat("v1/channel/getChannelGroupLists");
    /**
     * 关注应用号
     */
    public static final String ADD_APPS = DOMAIN.concat("v1/channel/addChannel");
    /**
     * 取消关注 应用号
     */
    public static final String CANCEL_APPS = DOMAIN.concat("v1/channel/cancelChannel");
    /**
     * 应用号详情
     */
    public static final String DETAIL_APPS = DOMAIN.concat("v1/channel/getChannelDetail");
    /**
     * 获取应用号详情中tab对应的新闻推荐
     */
    public static final String NEWS_RECOMMEND_DETAILS_APPS = DOMAIN.concat("v1/news/getNewsLists");
    /**
     * 获取普通新闻详情
     */
    public static final String DETAIL_NEWS_NORMAL = DOMAIN.concat("v1/news/getNewsDetail");
    /**
     * 获取更多 热点新闻
     */
    public static final String LIST_NEWS_HOT = DOMAIN.concat("v1/news/getHotNewsLists");
    /**
     * 图集详情
     */
    public static final String DETAIL_ALBUM = DOMAIN.concat("v1/news/getNewsPhotoDetail");
    /**
     * 取消关注 应用号
     */
    public static final String APPS_FOCUS_CANCEL = DOMAIN.concat("v1/channel/cancelChannel");
    /**
     * 视频详情
     */
    public static final String DETAIL_VIDEO = DOMAIN.concat("v1/news/getNewsVideoDetail");
    /**
     * 专题详情 接口
     */
    public static final String DETAIL_SPECIAL = DOMAIN.concat("v1/news/getNewsDetail");
    /**
     * 专题 列表接口
     */
    public static final String LIST_DETAIL_SPECIAL = DOMAIN.concat("v1/news/getTopicLists");
    /**
     * 添加评论新闻详情
     */
    public static final String COMMENT_DETAIL_NEWS_ADD = DOMAIN.concat("v1/news/addNewsComment");
    /**
     * 获取7.0首页的推荐新闻
     */
    public static final String NEWS_RECOMMEND_HOME = DOMAIN.concat("v1/getIndexData2");
    /**
     * 获取更多 列表数据
     */
    public static final String LIST_MORE = DOMAIN.concat("v1/news/getMoreLists");
    /**
     * 视频组详情
     */
    public static final String DETAIL_VIDEO_GROUP = DOMAIN.concat("v1/news/getNewsVideoGroupDetail");
    /**
     * 新闻详情收藏
     */
    public static final String COLLECTION_NEWS = DOMAIN.concat("v1/news/addNewsCollection");
    /**
     * 取消详情的收藏
     */
    public static final String COLLECTION_CANCEL = DOMAIN.concat("v1/news/cancelNewsCollection");
    /**
     * 新闻详情点赞
     */
    public static final String PRAISE_ADD_DETAIL = DOMAIN.concat("v1/news/addNewsPraise");
    /**
     * 获取新闻详情中的评论列表
     */
    public static final String LIST_COMMENT_DETAIL_NEWS = DOMAIN.concat("v1/news/getNewsCommentLists");
    /**
     * 评论列表点赞
     */
    public static final String APPRECIATE_LIST_COMMENT_DETAIL_NEWS = DOMAIN.concat("v1/news/addNewsCommentPraise");
    /**
     * 获取用户关注的应用号
     */
    public static final String LIST_APPS_USER = DOMAIN.concat("v1/channel/getUserChannelLists");
    /**
     * 获取关注过的功能区列表
     */
    public static final String LIST_FUNCTION_FOCUSED = DOMAIN.concat("v1/functions/getUserFunctionsLists");
    /**
     * 获取全部功能区列表
     */
    public static final String LIST_FUNCTION_ALL = DOMAIN.concat("v1/functions/getFunctionsAllLists");
    /**
     * 关注多个功能区
     */
    public static final String LIST_FUNCTION_FORMULATE = DOMAIN.concat("v1/functions/addAllFunctions");
    /**
     * 推送新闻列表
     */
    public static final String LIST_NEWS_PUSH = DOMAIN.concat("v1/news/getPushNewsLists");
    /**
     * 获取 积分 规则
     */
    public static final String RULE_CREDIT = DOMAIN.concat("v1/credits/getCreditsRule");
    /**
     * 获取 签到列表信息
     */
    public static final String INFO_SIGN_INTEGRAL = DOMAIN.concat("v1/credits/getSigninLists");
    /**
     * 签到
     */
    public static final String SIGN = DOMAIN.concat("v1/credits/addUserSignin");
    /**
     * 积分排名
     */
    public static final String RANK_INTEGRAL = DOMAIN.concat("v1/credits/getCreditsRankingLists");
    /**
     * 首次打开 推荐 应用号
     */
    public static final String CHANNEL_FIRST = DOMAIN.concat("v1/channel/getIndexFirstChannelRecommendLists");
    /**
     * 搜索应用号
     */
    public static final String CHANNEL_SEARCH = DOMAIN.concat("v1/channel/getChannelSearchLists");
    /**
     * 获取 电视台 分类
     */
    public static final String CLASSIFY_STATION_TELEVISON = DOMAIN.concat("v1/tv/getTvGroupLists");
    /**
     * 获取电视台列表
     */
    public static final String LIST_STATION_TELEVISON = DOMAIN.concat("v1/tv/getTvLists");
    /**
     * 获取话题排行榜列表接口
     */
    public static final String LIST_TOPIC_RECOMMEND = DOMAIN.concat("v1/shoot/getTopicList");
    /**
     * 获取话题详情
     */
    public static final String DETAIL_TOPIC = DOMAIN.concat("v1/shoot/getTopicInfo");
    /**
     * 新闻分享成功后的 回调
     */
    public static final String SUCCESS_SHARE = DOMAIN.concat("v1/news/userNewsShare");
    /**
     * 版本升级
     */
    public static final String UPDATE_APP = DOMAIN.concat("v1/system/updateVersion");
    /**
     * 拍客停留时间行为接口
     */
    public static final String REMAIN_PAIKEW = DOMAIN.concat("v1/shoot/addShootSiteTimeAction");
    //============================== 拍客 begin ==============================//
    /**
     * 获取拍客的 【视频列表】
     */
    public static final String LIST_VIDEO_PAIKEW = DOMAIN.concat("v1/shoot/getVideoList");
    /**
     * 获取拍客中的 【照片列表】
     */
    public static final String LIST_PHOTO_PAIKEW = DOMAIN.concat("v1/shoot/getPhotoList");
    /**
     * 获取拍客中的 【照片详情】
     */
    public static final String DETAIL_PHOTO_PAIKEW = DOMAIN.concat("v1/shoot/getPhotoInfo");
    /**
     * 获取拍客 中的 【个人信息】
     */
    public static final String USERINFO_PAIKEW = DOMAIN.concat("v1/shoot/getMyInfo");
    /**
     * 获取拍客个人信息接口
     */
    public static final String USER_INFO_PAIKEW = DOMAIN.concat("v1/shoot/getUserInfo");
    /**
     * 拍客 中的 【个人中心的视频列表】
     */
    public static final String LIST_VIDEO_USER_PAIKEW = DOMAIN.concat("v1/shoot/getUserVideoList");
    /**
     * 拍客 中的 【个人中心的照片列表】
     */
    public static final String LIST_PHOTO_USER_PAIKEW = DOMAIN.concat("v1/shoot/getUserPhotoList");
    /**
     * 拍客 中的 【个人中心的 喜欢列表接口】
     */
    public static final String LIST_FAVORITE_USER_PAIKEW = DOMAIN.concat("v1/shoot/getUserLikeList");
    /**
     * 拍客中的【个人中心的 关注列表接口】
     */
    public static final String LIST_FOCUS_PAIKEW = DOMAIN.concat("v1/shoot/getFollowList");
    /**
     * 拍客中 每一条视频播放时调用（默认第一条不需要）
     */
    public static final String CALL_BACK_PLAY_VIDEO_PAIKE = DOMAIN.concat("v1/shoot/setVideoClickNum");
    /**
     * 获取视频详情列表接口
     */
    public static final String LIST_DETAIL_VIDEO_PAIKEW = DOMAIN.concat("v1/shoot/getVideoInfoList");
    /**
     * 视频/照片 点赞接口
     */
    public static final String PRISE_PAIKEW = DOMAIN.concat("v1/shoot/shootPraise");
    /**
     * 获取评论列表接口
     */
    public static final String LIST_COMMENT_PAIKEW = DOMAIN.concat("v1/shoot/getCommentList");
    /**
     * 视频/照片评论接口
     */
    public static final String COMMENT_PAIKEW = DOMAIN.concat("v1/shoot/comment");
    /**
     * 评论回复接口
     */
    public static final String REPLY_COMMENT_PAIKEW = DOMAIN.concat("v1/shoot/reply");
    /**
     * 视频/照片评论点赞接口
     */
    public static final String LIST_COMMENT_PAIKEW_PRISE = DOMAIN.concat("v1/shoot/commentPraise");
    /**
     * 拍客 个人中的 的粉丝列表接口
     */
    public static final String LIST_FANS_PAIKEW = DOMAIN.concat("v1/shoot/getFansList");
    /**
     * 个人中心-关注/取关接口
     */
    public static final String FOLLOW_PAIKEW = DOMAIN.concat("v1/shoot/follow");
    /**
     * 修改我的签名
     */
    public static final String TAG_UPDATE_PAIKEW = DOMAIN.concat("v1/shoot/editMyTag");
    /**
     * 上传视频 拍客
     */
    public static final String VIDEO_UPLOAD_PAIKEW = DOMAIN.concat("v1/shoot/upVideo");
    /**
     * 上传 照片
     */
    public static final String PIC_UPLOAD_PAIKEW = DOMAIN.concat("v1/shoot/upPhoto");
    /**
     * 获取话题推荐列表接口
     */
    public static final String LIST_RECOMMEND_TOPIC = DOMAIN.concat("v1/shoot/getHotTopicList");
    /**
     * 获取拍客中 视频/ 图片上传的分类
     */
    public static final String CATEGORY_PAIKEW_UPLOAD = DOMAIN.concat("v1/shoot/getCategoryList");
    //============================== 拍客 end   ==============================//
    /**
     * 获取 用户 参加 活动 列表
     */
    public static final String LIST_ACTIVE_ACTIVE = DOMAIN.concat("v1/activity/getUserActivityLists");
    /**
     * 获取配置
     */
    public static final String CONFIG_APP = DOMAIN.concat("v1/config/getConfig");

    //============================== 直播 begin   ==============================//
    /**
     * 直播详情
     */
    public static final String DETAIL_LIVE_NEWS = DOMAIN.concat("v1/live/getNewsLiveDetails");
    /**
     * 获取直播 中【主播厅】的信息
     */
    public static final String DETAIL_HOST_LIVE = DOMAIN.concat("v1/live/getLiveItemLists");
    /**
     * 获取更多 直播列表
     */
    public static final String LIVE_LIST_MORE = DOMAIN.concat("v1/news/getNewsLiveLists");
    //============================== 直播 end   ==============================//
    /**
     * 获取首页频道
     */
    public static final String CHANNEL_INDEX = DOMAIN.concat("v1/index/getIndexTopChannelLists");
    /**
     * 获取首页频道新闻列表
     */
    public static final String LIST_INDEX_HOME = DOMAIN.concat("v1/index/getIndexNewsLists");
    /**
     * 获取频道的数据汇总
     */
    public static final String CHANNEL_INDEX_TOTAL = DOMAIN.concat("v1/index/getIndexChannelLists");
    /**
     * 获取用户关注的频道列表
     */
    public static final String CHANNEL_FOCUS_USER = DOMAIN.concat("v1/index/getIndexUserChannelLists");

    /**
     * 获取用户关注的频道列表新
     */
    public static final String CHANNEL_FOCUS_USER2 = DOMAIN.concat("v2/index/getIndexUserChannelLists");
    /**
     * 添加首页关注频道
     */
    public static final String CHANNEL_HOME_ADD = DOMAIN.concat("v1/index/addIndexChannelAll");
    //================== 扫码 end =========================
    /**
     * 扫码请求接口
     */
    public static final String URL_SCAN = DOMAIN.concat("v1/scan/scanCurl");
    /**
     * 添加扫码记录
     */
    public static final String RECORD_SCAN = DOMAIN.concat("v1/scan/scanRecord");
    //================== 扫码 end =========================
    //================== 新闻行为 begin =========================
    /**
     * 添加 新闻 停留时间行为
     */
    public static final String NEWS_ACTION_REMAIN = DOMAIN.concat("v1/news/addNewsSiteTimeAction");
    /**
     * 添加 新闻视频 观看行为
     */
    public static final String NEWS_ACTION_VIDEO_PLAY_TIME = DOMAIN.concat("v1/news/addNewsVideoPlayTimeAction");
    /**
     * 添加 直播 停留时间行为
     */
    public static final String REMAIN_LIVE_VIDEO = DOMAIN.concat("v1/live/addLiveSiteTimeAction");
    /**
     * 添加 直播 回放时间行为
     */
    public static final String PLAYBACK_LIVE_VIDEO = DOMAIN.concat("v1/live/addLivePlayTimeAction");
    /**
     * 获取 弹窗广告 列表
     */
    public static final String LIST_DIALOG_AD_HOME = DOMAIN.concat("v1/ad/getAdDialogLists");
    /**
     * 获取 新闻详情的相关信息
     */
    public static final String LIST_NEWS_DETAIL_RELATE = DOMAIN.concat("v1/news/getNewsDetailRelated");
    /**
     * 新闻详情中的广告
     */
    public static final String AD_DETAIL_NEWS = DOMAIN.concat("v1/ad/getAdPasteLists");
    //================== 新闻行为 end =========================

    /**
     * 上传趣看视频 ---- 自己的网页中 调用上传视频的时候； 客户端将获取的视频上传到此处； 然后将结果传给webView
     */
    public static final String UPLOAD_VIDEO_QUKAN = DOMAIN_FILE_UPLOAD_QUKAN.concat("/upload/qukanUpload");

    //================== 接口 end =========================
    /**
     * 当前 正在播放语音的 ImageView [用户的]
     */
    public static View IMG_AUDIO_PATH_PLAYING_USER = null;
    /**
     * 当前 正在播放的动画  [用户的]
     */
    public static AnimationDrawable ANIMATION_BROKE_USER = null;
    /**
     * 当前 正在播放语音的 ImageView [系统的]
     */
    public static View IMG_AUDIO_PATH_PLAYING_SYSTEM = null;
    /**
     * 当前 正在播放的动画  [系统的]
     */
    public static AnimationDrawable ANIMATION_BROKE_SYSTEM = null;
    /**
     * 是否全屏
     */
    public static boolean IS_FULL_SCREEN;
    /**
     * 最大上传的图片数量
     */
    public static final int CNT_MAX_PIC_UPLOAD = 9;
    /**
     * 话题 的 id
     */
    public static int TOPIC_ID = 0;
    /**
     * title Tag
     */
    public static final String TAG_TITLE = "title";
    /**
     * id Tag
     */
    public static final String TAG_ID = "id";
    /**
     * url Tag
     */
    public static final String TAG_URL = "url";
    /**
     * summary Tag
     */
    public static final String TAG_SUMMARY = "summary";
    /**
     * topic Id
     */
    public static final String TAG_TOPIC = "topicId";
    /**
     * 普通新闻类型
     */
    public static final String TYPE_NEWS_NORMAL = "news";
    /**
     * 新闻组类型
     */
    public static final String TYPE_NEWS_GROUP = "newslist";
    /**
     * 视频类型
     */
    public static final String TYPE_VIDEO_NORMAL = "video";
    /**
     * 视频组
     */
    public static final String TYPE_VIDEO_GROUP = "videogroup";
    /**
     * 图集类型
     */
    public static final String TYPE_PHOTO = "photo";
    /**
     * 直播类型
     */
    public static final String TYPE_LIVE = "live";
    /**
     * 活动类型
     */
    public static final String TYPE_URL = "url";
    /**
     * 专题类型
     */
    public static final String TYPE_TOPIC = "topic";

    public static final String TYPE_SPECIAL = "special";
    /**
     * 图集组
     */
    public static final String TYPE_PHOTO_GROUP = "photogroup";
    /**
     * 焦点类型
     */
    public static final String TYPE_FOCUS = "focus";
    /**
     * 开屏观察
     */
    public static final String TYPE_OBS = "kp_obs";
    /**
     * 开屏热议
     */
    public static final String TYPE_BUZZ = "kp_buzz";
    /**
     * 小视频
     */
    public static final String TYPE_VIDEO_DYNAMIC_NEWS = "videoDynamicNews";
    /**
     * 组新闻
     */
    public static final String TYPE_TEAM_GROUP = "teamgroup";
    /**
     * 普通新闻组
     */
    public static final String TYPE_NEWS_NORMAL_GROUP = "normaltopic";

    public static final String TYPE_NEWS_BROKE = "broke";
    public static final String TYPE_NEWS_PAIKEW = "paikew";
    /**
     * 新闻组
     */
    public static final String TYPE_NEWS_NEWSLIST = "newslist";
    /**
     * 视频
     */
    public static final String TYPE_NEWS_VIDEO = "video";
    /**
     * 视频组
     */
    public static final String TYPE_NEWS_VIDEOGROUP = "videogroup";
    /**
     * 图集
     */
    public static final String TYPE_NEWS_PHOTO = "photo";
    /**
     * 直播
     */
    public static final String TYPE_NEWS_LIVE = "live";
    /**
     * 活动
     */
    public static final String TYPE_NEWS_ACTIVITY = "url";
    /**
     * 专题
     */
    public static final String TYPE_NEWS_SPECIAL = "topic";
    /**
     * 普通专题
     */
    public static final String TYPE_NEWS_SPEICAL_NORMAL = "normaltopic";
    /**
     * 图集组
     */
    public static final String TYPE_NEWS_PHOTOGROUP = "photogroup";
    /**
     * 拍客视频
     */
    public static final String TYPE_NEWS_VIDEO_PAIKEW = "shootvideo";
    /**
     * 拍客图片
     */
    public static final String TYPE_NEWS_PHOTO_PAIKEW = "shootphoto";
    /**
     * 拍客话题
     */
    public static final String TYPE_NEWS_TOPIC_PAIKEW = "shoottopic";
    /**
     * 拍客用户中
     */
    public static final String TYPE_NEWS_USERCETER_PAIKEW = "shootuser";


    /**
     * 采集喜欢
     */
    public static final String TYPE_GUESS_LIKE = "GuessLike";
    /**
     * 动新闻
     */
    public static final String TYPE_DYNAMIC_NEWS = "DynamicNews";
    /**
     * 拍客照片
     */
    public static final String TYPE_SHOOT_PHOTO = "shootphoto";
    /**
     * 拍客视频
     */
    public static final String TYPE_SHOOT_VIDEO = "shootvideo";
    /**
     * 开屏置顶
     */
    public static final String TYPE_KP_TOPIC = "kp_topic";
    /**
     * 功能区
     */
    public static final String TYPE_FUNCTIONS = "functions";
    /**
     * 滚动新闻
     */
    public static final String TYPE_ROLLING_NEWS = "RollingNews";
    /**
     * 应用号分类
     */
    public static final String TYPE_CHANNEL_GROUP = "ChannelGroup";
    /**
     * 应用号查看更多
     */
    public static final String TYPE_CHANNEL = "channel";
    /**
     * 更多
     */
    public static final String TYPE_MORE = "more";
    /**
     * 突发
     */
    public static final String TYPE_SUDDEN = "sudden";

    /**
     * shareContent Tag
     */

    public static final String TAG_SHARE_CONTENT = "shareContent";
    /**
     * 更换 用户名称
     */
    public static final String USERNAME_UPDATE = "updateName";

    /**
     * 用户信息 更新
     */
    public static final String USERINFO_UPDATE = "updateUserInfo";

    /**
     * group_id Tag
     */
    public static final String TAG_GROUP_ID = "group_id";

    /**
     * 扫描结果
     */
    public static final String TAG_RESULT_SCAN = "scanResult";

    /**
     * 网页端上传的视频  最小的 时长
     */
    public static final int TIME_MIN_VIDEO_UPDATE_WEB = 1;
    /**
     * 网页端上传的视频  最大的时长
     */
    public static final int TIME_MAX_VIDEO_UPDATE_WEB = 60 * 15;

    /**
     * 资源服务器上传成功响应码
     */
    public static final int CODE_SUCCESS_UPLOAD_RESOURCE = 1;


    /**
     * 上传趣看 单个文件
     */
    public static final String FILE_SINGLE_UPLOAD_QUKAN = DOMAIN_FILE_UPLOAD_QUKAN.concat("/upload/upload");

    /**
     * 上传批量文件到趣看
     */
    public static final String FILE_MULTIPLE_UPLOAD_QUKAN = DOMAIN_FILE_UPLOAD_QUKAN.concat("/upload/uploadMulti");
    /**
     * 上传视频 到趣看
     */
    public static final String VIDEO_UPLOAD_QUKAN = DOMAIN_FILE_UPLOAD_QUKAN.concat("/upload/qukanUpload");

    /**
     * 是否显示黑白模式
     */
    public static boolean IS_SHOW_GARY_MODE = false;

    /**
     * 飘红
     */
    public static boolean IS_SHOW_RED_MODE = false;

    /**
     * 是否显示 黑白模式
     */
    public static final String MOURN = DOMAIN.concat("v1/config/getMourn");

    /**
     * 新闻详情
     */
    public static final String TAG_TYPE_DETAIL_NEWS_PUSH = "newsDetail";
    /**
     * 组新闻
     */
    public static final String TAG_NEWS_GROUP = "newsGroup";
    /**
     * 获取当前用户的邀请码
     */
    public static final String CODE_INVITATION = DOMAIN.concat("v1/user/getUserCode");

    /**
     * 添加邀请码
     */
    public static final String CODE_INVITATION_ADD = DOMAIN.concat("v1/user/addInvitationCode");

    /**
     * 扫码 处理 邀请码
     */
    public static final String CODE_SCAN = DOMAIN.concat("v1/user/scanCode");
    /**
     * 获取 所有 功能区 [首页 底部Tab 中 的 功能]
     */
    public static final String FUNCTION_ALL = DOMAIN.concat("v1/functions/getFunctionsAllLists");

    /**
     * 获取  功能区广告 [首页 底部Tab 中 的 功能]
     */
    public static final String FUNCTION_ADVERTISEMENT = DOMAIN.concat("v2/functions/getAdlists");

    public static final String SERVER_PUSH = "https://push.ccwb.cn/api/alloc";

    public static final String HOST_API_PUSH = "https://push.ccwb.cn";

    public static final String KEY_PUBLIC_PUSH =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCID9gy4225cgKOoWjQU/E6utsYdvau2nllwip/TnFjTL6gviKjykWeT1whnHHmAqOeuRxA"
                    + "+0tKX1RNtHs9qWRq1I7o46LtXporUAY7SYJh7JTGniZsu5ta5Ni2pYwcQMZ3s3Qq7Kp1OepQs60WM6ip3cm7QiwGK7o0FYHs2XNSbwIDAQAB";

    public static List<String> LIST_FILE_DOWNLOAD = new ArrayList<>();

    /**
     * 开屏推送
     */
    public static final String TAG_PUSH_KAI_PIN = DOMAIN.concat("v1/kpush/addKPush");

    /**
     * 新闻组详情
     */
    public static final String DETAIL_NEWS_GROUP = DOMAIN.concat("v2/news/getNewsGroudDetail");

    /**
     * 新闻组列表
     */
    public static final String LIST_NEWS_GROUP = DOMAIN.concat("v2/news/getNewsGroudLists");

    /**
     * 获取首页的推荐新闻 --新  2021-07-14 改
     */
    public static final String NEWS_RECOMMEND_HOME_MORE_NEW = DOMAIN.concat("v2/news/recommendFeeds");

    /**
     * 通过关键词搜索 ---新  2021-07-14 改
     */
    public static final String SEARCH_FEED_NEW = DOMAIN.concat("v2/news/searchFeeds");
    /**
     * 获取专属焦点 [开屏热点]---新  2021-07-14 改
     */
    public static final String EXCLUSIVE_NEWS_NEW = DOMAIN.concat("v2/news/exclusiveFeeds");
    /**
     * 关键词---新  2021-07-14 改
     */
    public static final String KEY_SEARCH_NEW = DOMAIN.concat("v2/news/getHotkeys");

    /**
     * 隐私政策
     */
//    public static String PRIVACY_POLICY = "https://apisaastest.kpinfo.cn/app/html/8/policy-index.html";
    public static String PRIVACY_POLICY = "https://appkp.ccwb.cn/policy/index.html";
    /**
     * 用户协议
     */
//    public static String USER_AGREEMENT = "https://apisaastest.kpinfo.cn/app/html/8/app-agreement.html";
    public static String USER_AGREEMENT = "https://appkp.ccwb.cn/app/user/app-agreement.html";
    /**
     * 注销协议
     */
//    public static String PROTOCOL_CANCEL_USER = "https://apisaastest.kpinfo.cn/app/html/8/cancel-user.html";
    public static String PROTOCOL_CANCEL_USER = "https://appkp.ccwb.cn/app/cancel-user.html";

    /**
     * 注销用户
     */
    public static final String CANCEL_USER = DOMAIN.concat("v1/user/cancelUser");

    public static final String ADD_MACHINE = DOMAIN.concat("v1/system/addMachine");
}