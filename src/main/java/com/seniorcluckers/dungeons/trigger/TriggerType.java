package com.seniorcluckers.dungeons.trigger;

public enum TriggerType {

    INDIVIDUAL, ALL, SINGLE;

    public static TriggerType equalsIgnoreCase(String value) {
        for (TriggerType triggerType : TriggerType.values()) {
            if (triggerType.name().equalsIgnoreCase(value)) {
                return TriggerType.valueOf(triggerType.name());
            }
        }
        return null;
    }

}
