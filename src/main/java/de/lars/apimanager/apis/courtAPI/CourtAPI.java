package de.lars.apimanager.apis.courtAPI;

public class CourtAPI {
    private static ICourtAPI api;

    public static ICourtAPI getApi() {
        return api;
    }

    public static void setApi(ICourtAPI api) {
        CourtAPI.api = api;
    }
}