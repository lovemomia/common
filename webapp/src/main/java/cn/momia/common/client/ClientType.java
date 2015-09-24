package cn.momia.common.client;

public class ClientType {
    public static final int APP = 1;
    public static final int WAP = 2;

    public static boolean isApp(int clientType) {
        return clientType == ClientType.APP;
    }

    public static boolean isWap(int clientType) {
        return clientType == ClientType.WAP;
    }
}
