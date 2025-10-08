package de.lars.apiManager.dataAPI;

public class DataAPI {

    private static IDataAPI api;

    public static IDataAPI getApi() {
        return api;
    }

    public static void setApi(IDataAPI api) {
        DataAPI.api = api;
    }
}
