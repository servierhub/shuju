package com.github.romualdrousseau.shuju.math;

import java.util.Random;

public class Scalar {
    public static final float EPSILON = 1e-8f;
    public static final float LAM = 1e-3f;

    private static Random randomGenerator;

    static {
        Scalar.randomGenerator = new Random(System.currentTimeMillis());
    }

    public static float sign(float x) {
        return x >= 0 ? 1.0f : -1.0f;
    }

    public static float abs(float x) {
        return (float) Math.abs(x);
    }

    public static float tanh(float x) {
        return (float) Math.tanh(x);
    }

    public static float sqrt(float x) {
        return (float) Math.sqrt(x);
    }

    public static float pow(float x, float n) {
        return (float) Math.pow(x, n);
    }

    public static float exp(float x) {
        return (float) Math.exp(x);
    }

    public static float log(float x) {
        return (float) Math.log(x);
    }

    public static float min(float a, float b) {
        return (float) Math.min(a, b);
    }

    public static float max(float a, float b) {
        return (float) Math.max(a, b);
    }

    public static float random(float a) {
        return Scalar.random(0, a);
    }

    public static float random(float a, float b) {
        return randomGenerator.nextFloat() * (b - a) + a;
    }

    public static float randomGaussian() {
        return (float) randomGenerator.nextGaussian();
    }

    public static float sgn(float x) {
        return (x > 0.0f) ? 1.0f : ((x < 0.0f) ? -1.0f : 0.0f);
    }

    public static float map(float t, float start1, float stop1, float start2, float stop2) {
        final float m = (stop2 - start2) / (stop1 - start1);
        return m * (t - start1) + start2;
    }

    public static float unlerp(float a, float b, float v) {
        return (v - a) / (b - a);
    }

    public static float constrain(float x, float a, float b) {
        return (x < a) ? a : ((x > b) ? b : x);
    }

    public static float if_lt_then(float x, float p, float a, float b) {
        return (x < p) ? a : b;
    }
}
