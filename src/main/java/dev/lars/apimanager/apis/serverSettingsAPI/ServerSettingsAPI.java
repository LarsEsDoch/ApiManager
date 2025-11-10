package dev.lars.apimanager.apis.serverSettingsAPI;

public class ServerSettingsAPI {
    private static IServerSettingsAPI api;

    public static IServerSettingsAPI getApi() {
        return api;
    }

    public static void setApi(IServerSettingsAPI api) {
        ServerSettingsAPI.api = api;
    }
}