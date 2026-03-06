package dev.lars.apimanager.apis.scoreboardSettingsAPI;

public enum ScoreboardElement {
    SCOREBOARD_TOGGLE("scoreboard_toggle"),
    COINS("show_coins"),
    PLAYTIME("show_playtime"),
    DEATHS("show_deaths"),
    COORDINATES("show_coordinates"),
    QUESTS("show_quests"),
    ONLINE_PLAYERS("show_online_players"),
    PING("show_ping"),
    BIOM("show_biom"),
    WEATHER("show_weather"),
    CONDITION("show_condition"),
    EVENT_COUNTDOWN("show_event_countdown"),
    KILLS("show_kills"),
    PROGRESS("show_progress"),
    SESSION_TIME("show_session_time");

    private final String column;

    ScoreboardElement(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }
}