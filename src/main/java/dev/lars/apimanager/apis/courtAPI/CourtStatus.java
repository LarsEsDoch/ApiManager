package dev.lars.apimanager.apis.courtAPI;

public enum CourtStatus {
    RELEASED(0),
    REPORTED(1),
    AWAITING_FOR_COURT(2),
    PROSECUTED(3),
    SENTENCED(4),
    IMPRISONED(5);

    private final int id;

    CourtStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CourtStatus fromId(int id) {
        for (CourtStatus status : values()) {
            if (status.id == id) return status;
        }
        return RELEASED;
    }
}