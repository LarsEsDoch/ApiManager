package dev.lars.apimanager.apis.limitAPI;

public class LimitAPI {
    private static ILimitAPI api;

    public static ILimitAPI getApi() {
        return api;
    }

    public static void setApi(ILimitAPI api) {
        LimitAPI.api = api;
    }
}