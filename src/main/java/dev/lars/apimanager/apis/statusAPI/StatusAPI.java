package dev.lars.apimanager.apis.statusAPI;

public class StatusAPI {
    private static IStatusAPI api;

    public static IStatusAPI getApi() {
        return api;
    }

    public static void setApi(IStatusAPI api) {
        StatusAPI.api = api;
    }
}