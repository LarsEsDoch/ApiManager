package de.lars.apimanager.toggleAPI;

public class ToggleAPI {

    private static IToggleAPI api;

    public static IToggleAPI getApi() {
        return api;
    }

    public static void setApi(IToggleAPI api) {
        ToggleAPI.api = api;
    }
}
