package com.ruse.model;

/**
 * Item bonus.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class BonusValue {

    private final Bonus bonus;

    private final double value;

    public BonusValue(Bonus bonus, double value) {
        this.bonus = bonus;
        this.value = value;
    }

    @Override
    public String toString() {
        return "BonusValue{" +
                "bonus=" + bonus +
                ", value=" + value +
                '}';
    }

    public Bonus getBonus() {
        return bonus;
    }

    public double getValue() {
        return value;
    }
}
