package dev.lars.apimanager.apis.serverStateAPI;

public class ServerStateAPI {
    private static IServerStateAPI api;

    public static IServerStateAPI getApi() {
        return api;
    }

    public static void setApi(IServerStateAPI api) {
        ServerStateAPI.api = api;
    }
}