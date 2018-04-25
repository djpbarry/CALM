/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.DataProcessing;

import ij.IJ;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Interpolator {

    public static double[][][] interpolateLinearly(double[][][] data, int ref, boolean[] keys) {
        double[][][] interpolatedData = new double[data.length][][];
        for (int dataIndex = 0; dataIndex < data.length; dataIndex++) {
            int length = data[dataIndex].length;
            double firstRef = data[dataIndex][0][ref];
            double lastRef = data[dataIndex][length - 1][ref];
            int outputSize = (int) Math.round(lastRef - firstRef + 1.0);
            interpolatedData[dataIndex] = new double[outputSize][data[dataIndex][0].length];
            for (int rowIndex = 0, outRow = 0; rowIndex < data[dataIndex].length; rowIndex++) {
                int interSpace = 1;
                if (rowIndex < data[dataIndex].length - 1) {
                    interSpace = (int) Math.round(data[dataIndex][rowIndex + 1][ref] - data[dataIndex][rowIndex][ref]);
                }
                for (int l = 0; l < interSpace; l++) {
                    for (int colIndex = 0; colIndex < data[dataIndex][rowIndex].length; colIndex++) {
                        double interVal = data[dataIndex][rowIndex][colIndex];
                        if (interSpace > 1 && keys[colIndex]) {
                            interVal = data[dataIndex][rowIndex][colIndex] + l * (data[dataIndex][rowIndex + 1][colIndex] - data[dataIndex][rowIndex][colIndex]) / interSpace;
                        }
                        interpolatedData[dataIndex][outRow][colIndex] = interVal;
                    }
                    outRow++;
                }
            }
        }
        return interpolatedData;
    }
}
