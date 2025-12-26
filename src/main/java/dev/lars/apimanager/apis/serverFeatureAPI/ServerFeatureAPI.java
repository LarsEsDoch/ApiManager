package dev.lars.apimanager.apis.serverFeatureAPI;

public class ServerFeatureAPI {
    private static IServerFeatureAPI api;

    public static IServerFeatureAPI getApi() {
        return api;
    }

    public static void setApi(IServerFeatureAPI api) {
        ServerFeatureAPI.api = api;
    }
}