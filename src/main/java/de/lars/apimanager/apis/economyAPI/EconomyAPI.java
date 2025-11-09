package de.lars.apimanager.apis.economyAPI;

public class EconomyAPI {
    private static IEconomyAPI api;

    public static IEconomyAPI getApi() {
        return api;
    }

    public static void setApi(IEconomyAPI api) {
        EconomyAPI.api = api;
    }
}