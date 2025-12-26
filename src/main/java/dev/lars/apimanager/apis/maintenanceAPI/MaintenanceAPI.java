package dev.lars.apimanager.apis.maintenanceAPI;

public class MaintenanceAPI {
    private static IMaintenanceAPI api;

    public static IMaintenanceAPI getApi() {
        return api;
    }

    public static void setApi(IMaintenanceAPI api) {
        MaintenanceAPI.api = api;
    }
}