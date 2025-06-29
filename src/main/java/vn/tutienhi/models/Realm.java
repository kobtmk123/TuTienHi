package vn.tutienhi.models;

import vn.tutienhi.utils.ChatUtil;
import java.util.List;

public class Realm {
    private final String id;
    private final String displayName;
    private final double maxLinhKhi;
    private final double linhKhiPerTick;
    private final double lightningDamage;
    private final List<String> permanentEffects;
    private final double bonusHealth;
    private final double bonusDamage;

    public Realm(String id, String displayName, double maxLinhKhi, double linhKhiPerTick, double lightningDamage, List<String> permanentEffects, double bonusHealth, double bonusDamage) {
        this.id = id;
        this.displayName = ChatUtil.colorize(displayName);
        this.maxLinhKhi = maxLinhKhi;
        this.linhKhiPerTick = linhKhiPerTick;
        this.lightningDamage = lightningDamage;
        this.permanentEffects = permanentEffects;
        this.bonusHealth = bonusHealth;
        this.bonusDamage = bonusDamage;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMaxLinhKhi() {
        return maxLinhKhi;
    }

    public double getLinhKhiPerTick() {
        return linhKhiPerTick;
    }

    public double getLightningDamage() {
        return lightningDamage;
    }

    public List<String> getPermanentEffects() {
        return permanentEffects;
    }

    public double getBonusHealth() {
        return bonusHealth;
    }

    public double getBonusDamage() {
        return bonusDamage;
    }
}