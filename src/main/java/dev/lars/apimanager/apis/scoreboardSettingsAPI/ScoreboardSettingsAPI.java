package dev.lars.apimanager.apis.scoreboardSettingsAPI;

public class ScoreboardSettingsAPI {
    private static IScoreboardSettingsAPI api;

    public static IScoreboardSettingsAPI getApi() {
        return api;
    }

    public static void setApi(IScoreboardSettingsAPI api) {
        ScoreboardSettingsAPI.api = api;
    }
}