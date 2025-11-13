package dev.lars.apimanager.apis.playerIdentityAPI;

public class PlayerIdentityAPI {
    private static IPlayerIdentityAPI api;

    public static IPlayerIdentityAPI getApi() {
        return api;
    }

    public static void setApi(IPlayerIdentityAPI api) {
        PlayerIdentityAPI.api = api;
    }
}