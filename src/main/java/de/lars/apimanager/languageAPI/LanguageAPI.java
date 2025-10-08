package de.lars.apiManager.languageAPI;

public class LanguageAPI {

    private static ILanguageAPI api;

    public static ILanguageAPI getApi() {
        return api;
    }

    public static void setApi(ILanguageAPI api) {
        LanguageAPI.api = api;
    }
}
