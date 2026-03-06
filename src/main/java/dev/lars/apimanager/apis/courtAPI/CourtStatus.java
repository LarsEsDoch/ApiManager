package dev.lars.apimanager.apis.courtAPI;

public enum CourtStatus {
    FREE(0),
    REPORTED(1),
    WAITING(2),
    COURT(3),
    LOCKED(4),
    JAILED(5);

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
        return FREE;
    }
}