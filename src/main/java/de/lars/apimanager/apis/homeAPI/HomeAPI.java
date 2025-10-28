package de.lars.apimanager.apis.homeAPI;

public class HomeAPI {
    private static IHomeAPI api;

    public static de.lars.apimanager.apis.homeAPI.IHomeAPI getApi() {
        return api;
    }

    public static void setApi(de.lars.apimanager.apis.homeAPI.IHomeAPI api) {
        HomeAPI.api = api;
    }
}