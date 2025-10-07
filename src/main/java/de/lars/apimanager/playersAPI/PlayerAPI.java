package de.lars.apimanager.playersAPI;

public class PlayerAPI {

    private static IPlayerAPI api;

    public static IPlayerAPI getApi() {
        return api;
    }

    public static void setApi(IPlayerAPI api) {
        PlayerAPI.api = api;
    }
}
