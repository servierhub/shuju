package com.github.romualdrousseau.shuju.transforms;

import com.github.romualdrousseau.shuju.ITransform;
import com.github.romualdrousseau.shuju.math.Vector;

public class SmoothScaler implements ITransform {
    public SmoothScaler(float coef) {
        this.firstRow = true;
        this.coef = coef;
    }

    public void apply(Vector feature, int rowIndex, int colIndex) {
        if (this.firstRow) {
            this.lastValue = feature;
            firstRow = false;
        } else {
            this.lastValue = feature.mul(this.coef).add(this.lastValue.mul(1.0f - this.coef));
        }
    }

    private boolean firstRow;
    private Vector lastValue;
    private float coef;
}
