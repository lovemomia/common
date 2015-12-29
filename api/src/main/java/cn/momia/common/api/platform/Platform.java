package cn.momia.common.api.platform;

public class Platform {
    public static final int APP = 1;
    public static final int WAP = 2;

    public static boolean isApp(int platform) {
        return platform == Platform.APP;
    }

    public static boolean isWap(int platform) {
        return platform == Platform.WAP;
    }
}
