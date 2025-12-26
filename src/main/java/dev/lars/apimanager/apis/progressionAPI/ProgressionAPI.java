package dev.lars.apimanager.apis.progressionAPI;

public class ProgressionAPI {
    private static IProgressionAPI api;

    public static IProgressionAPI getApi() {
        return api;
    }

    public static void setApi(IProgressionAPI api) {
        ProgressionAPI.api = api;
    }
}