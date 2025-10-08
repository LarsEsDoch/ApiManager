package de.lars.apiManager.rankAPI;

public class RankAPI {

    private static IRankAPI api;

    public static IRankAPI getApi() {
        return api;
    }

    public static void setApi(IRankAPI api) {
        RankAPI.api = api;
    }
}
