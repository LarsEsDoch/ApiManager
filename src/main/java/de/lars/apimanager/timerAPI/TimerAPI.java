package de.lars.apimanager.timerAPI;

public class TimerAPI {

    private static ITimerAPI api;

    public static ITimerAPI getApi() {
        return api;
    }

    public static void setApi(ITimerAPI api) {
        TimerAPI.api = api;
    }
}
