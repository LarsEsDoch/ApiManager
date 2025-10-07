package de.lars.apimanager.banAPI;

public class BanAPI {

    private static IBanAPI api;

    public static IBanAPI getApi() {
        return api;
    }

    public static void setApi(IBanAPI api) {
        BanAPI.api = api;
    }
}
