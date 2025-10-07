package de.lars.apimanager.backpackAPI;

public class BackpackAPI {

    private static IBackpackAPI api;

    public static IBackpackAPI getApi() {
        return api;
    }

    public static void setApi(IBackpackAPI api) {
        BackpackAPI.api = api;
    }
}
