package dev.lars.apimanager.apis.homeAPI;

public class HomeAPI {
    private static IHomeAPI api;

    public static IHomeAPI getApi() {
        return api;
    }

    public static void setApi(IHomeAPI api) {
        HomeAPI.api = api;
    }
}