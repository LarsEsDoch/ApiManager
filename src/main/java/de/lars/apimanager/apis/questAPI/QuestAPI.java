package de.lars.apimanager.apis.questAPI;

public class QuestAPI {
    private static IQuestAPI api;

    public static IQuestAPI getApi() {
        return api;
    }

    public static void setApi(IQuestAPI api) {
        QuestAPI.api = api;
    }
}