package Foundation;

public class StatusEffect {
    private String statusEffectName;
    private int statusEffectDuration;

    public StatusEffect(String statusEffectName, int statusEffectDuration) {
        this.statusEffectName = statusEffectName;
        this.statusEffectDuration = statusEffectDuration;
    }

    public String getStatusEffectName() {
        return this.statusEffectName;
    }

    public int getStatusEffectDuration() {
        return this.statusEffectDuration;
    }

    public void reduceStatusEffectDuration() {
        this.statusEffectDuration--;
    }

    public boolean isStatusEffectExpired() {
        return this.statusEffectDuration <= 0;
    }
}
