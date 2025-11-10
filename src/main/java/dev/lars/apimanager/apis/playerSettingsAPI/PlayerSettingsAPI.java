package dev.lars.apimanager.apis.playerSettingsAPI;

public class PlayerSettingsAPI {
    private static IPlayerSettingsAPI api;

    public static IPlayerSettingsAPI getApi() {
        return api;
    }

    public static void setApi(IPlayerSettingsAPI api) {
        PlayerSettingsAPI.api = api;
    }
}