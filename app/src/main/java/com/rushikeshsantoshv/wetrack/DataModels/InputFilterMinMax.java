package com.rushikeshsantoshv.wetrack.DataModels;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private double minValue;
    private double maxValue;

    public InputFilterMinMax(double minValue, double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            // Convert the input CharSequence to a string
            String sourceStr = source.toString();
            String destStr = dest.toString();

            // Concatenate the existing text with the new input
            String newValue = destStr.substring(0, dstart) + sourceStr.substring(start, end) + destStr.substring(dend);

            // Parse the new value
            double input = Double.parseDouble(newValue);

            // Check if the input is within the valid range
            if (isInRange(input)) {
                return null; // Accept the input
            }
        } catch (NumberFormatException e) {
            // Invalid input format, ignore it
        }

        // Reject the input by returning an empty string
        return "";
    }

    private boolean isInRange(double value) {
        return value >= minValue && value <= maxValue;
    }
}
