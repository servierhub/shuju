package com.github.romualdrousseau.shuju.op.linalg;

import java.util.function.BiFunction;

import com.github.romualdrousseau.shuju.core.MArray;
import com.github.romualdrousseau.shuju.core.UFunc;
import com.github.romualdrousseau.shuju.types.Tensor;

public class MatMul extends UFunc<Float> {

    public static BiFunction<Tensor, Tensor, Tensor> Op = (a, b) -> Tensor.of(new MatMul((x, y) -> x * y).outer(a, b, null));

    public MatMul(BiFunction<Float, Float, Float> func) {
        super(func, 2);
    }

    @Override
    protected int[] outerShape(final MArray a, final MArray b) {
        int[] newShape = new int[a.shape.length];
        for (int i = 0; i < a.shape.length; i++) {
            final int n = a.shape.length - i;
            if (n == 1) {
                newShape[i] = b.shape[i];
            } else if (n == 2) {
                newShape[i] = a.shape[i];
            } else {
                newShape[i] = Math.max(a.shape[i], b.shape[i]);
            }
        }
        return newShape;
    }

    @Override
    protected void outerArray(final int n, final MArray a, int aoff, final MArray b, int boff, final MArray c,
            int coff) {
        if (a.shape.length - n > 2) {
            super.outerArray(n, a, aoff, b, boff, c, coff);
        } else {
            final int adim_i = a.shape[n];
            final int astr_i = a.stride[n];
            final int adim_ij = a.shape[n + 1];
            final int astr_ij = a.stride[n + 1];

            final int bstr_ij = b.stride[n];
            final int bdim_ijk = b.shape[n + 1];
            final int bstr_ijk = b.stride[n + 1];

            final int cstr_i = c.stride[n];
            final int cstr_ijk = c.stride[n + 1];

            for (int i = 0; i < adim_i; i++) {
                int aoff_ij = aoff;
                int boff_ij = boff;
                int coff_ij = coff;
                for (int j = 0; j < adim_ij; j++) {
                    final float aa = a.data[aoff_ij];
                    int boff_ijk = boff_ij;
                    int coff_ijk = coff_ij;
                    for (int k = 0; k < bdim_ijk; k++) {
                        c.data[coff_ijk] = aa * b.data[boff_ijk] + c.data[coff_ijk];
                        boff_ijk += bstr_ijk;
                        coff_ijk += cstr_ijk;
                    }
                    aoff_ij += astr_ij;
                    boff_ij += bstr_ij;
                }
                aoff += astr_i;
                coff += cstr_i;
            }
        }
    }

    @Override
    protected void applyAccFunc(int dim, MArray a, int aoff, int astr, MArray b, int boff, int bstr, float initital) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected void applyScalarFunc(int dim, MArray a, int aoff, int astr, float b, MArray c, int coff, int cstr) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected void applyArrayFunc(int dim, MArray a, int aoff, int astr, MArray b, int boff, int bstr, MArray c, int coff,
            int cstr) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
