package com.cbcode.dealertasks.Cars.model.Enums;

public enum CarStockSold {
    STOCK("Stock"),
    SOLD("Sold");

    private final String value;

    CarStockSold(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Get the enum value from a string.
     *
     * @param value The string value.
     * @return The enum value.
     */
    public static CarStockSold fromValue(String value) {
        for (CarStockSold carStockSold : CarStockSold.values()) {
            if (carStockSold.value.equals(value)) {
                return carStockSold;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
