package dev.lars.apimanager.apis.nickAPI;

public class NickAPI {
    private static INickAPI api;

    public static INickAPI getApi() {
        return api;
    }

    public static void setApi(INickAPI api) {
        NickAPI.api = api;
    }
}