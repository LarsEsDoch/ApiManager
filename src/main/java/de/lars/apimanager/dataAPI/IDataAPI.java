package de.lars.apiManager.dataAPI;

public interface IDataAPI {
    boolean isRealTimeActivated();

    boolean isMaintenanceActive();

    String getMaintenanceReason();

    int getMaintenanceTime();

    void setMaintenanceTime(Integer maintenanceTime);

    void setMaintenanceReason(String maintenanceReason);

    void setRealTimeActivated(boolean activated);

    void activateMaintenance(String reason, Integer time);

    void deactivateMaintenance();
}
