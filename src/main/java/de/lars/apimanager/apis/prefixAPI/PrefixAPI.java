package de.lars.apimanager.apis.prefixAPI;

public class PrefixAPI {
    private static IPrefixAPI api;

    public static IPrefixAPI getApi() {
        return api;
    }

    public static void setApi(IPrefixAPI api) {
        PrefixAPI.api = api;
    }
}