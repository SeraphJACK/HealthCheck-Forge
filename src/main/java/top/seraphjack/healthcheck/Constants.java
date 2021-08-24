package top.seraphjack.healthcheck;

public final class Constants {
    public static final String SERVER_NAME = System.getenv("HC_SERVER_NAME");
    public static final String AUTHORIZE_TOKEN = System.getenv("HC_TOKEN");
    public static final String HC_ENDPOINT = System.getenv("HC_ENDPOINT");
}
