package vn.tutienhi.data;

public class PlayerData {
    private String realmId;
    private double linhKhi;
    private boolean isCultivating;
    private String cultivationPathId;

    public PlayerData(String realmId, double linhKhi, String cultivationPathId) {
        this.realmId = realmId;
        this.linhKhi = linhKhi;
        this.isCultivating = false;
        this.cultivationPathId = cultivationPathId;
    }

    public String getRealmId() { return realmId; }
    public void setRealmId(String realmId) { this.realmId = realmId; }
    public double getLinhKhi() { return linhKhi; }
    public void setLinhKhi(double linhKhi) { this.linhKhi = linhKhi; }
    public void addLinhKhi(double amount) { this.linhKhi += amount; }
    public boolean isCultivating() { return isCultivating; }
    public void setCultivating(boolean cultivating) { isCultivating = cultivating; }
    public String getCultivationPathId() { return cultivationPathId; }
    public void setCultivationPathId(String cultivationPathId) { this.cultivationPathId = cultivationPathId; }
}