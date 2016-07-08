package com.jaredauty.scrible.shapes;

import android.graphics.Matrix;

/**
 * Abstract class for moving the scene.
 * Created by Jared on 08/07/2016.
 */
public abstract class SceneManipulator {
    Matrix mMatrix;

    public SceneManipulator(){
        mMatrix = new Matrix();
    }
    public Matrix getMatrix() {
        mMatrix = constructMatrix();
        return mMatrix;
    }

    /**
     * This method must be implemented on the base class to create the matrix that should be
     * applied to the scene.
     * @return Matrix the matrix to move the scene with.
     */
    protected abstract Matrix constructMatrix();

}
