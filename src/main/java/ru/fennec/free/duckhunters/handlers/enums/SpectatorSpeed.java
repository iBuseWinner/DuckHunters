package ru.fennec.free.duckhunters.handlers.enums;

public enum SpectatorSpeed {

    X1("1x", 1.0),
    X1_5("1.5x", 1.5),
    X2("2x", 2.0);

    private String name;
    private double coef;

    private SpectatorSpeed(String name, double coef) {
        this.name = name;
        this.coef = coef;
    }

    public String getName() {
        return name;
    }

    public double getCoef() {
        return coef;
    }

    public SpectatorSpeed next() {
        int ordinal = ordinal() + 1;
        if (ordinal >= values().length)
            ordinal = 0;
        return values()[ordinal];
    }

    public SpectatorSpeed prev() {
        int ordinal = ordinal() - 1;
        if (ordinal < 0)
            ordinal = values().length - 1;
        return values()[ordinal];
    }
}
