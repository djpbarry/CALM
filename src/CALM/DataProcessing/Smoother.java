/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM.DataProcessing;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Smoother {

    public static double[][][] smoothData(double[][][] inputData, int window, boolean[] keys) {
        DescriptiveStatistics[] stats = new DescriptiveStatistics[keys.length];
        int statSize = 2 * window + 1;
        int iDL = inputData.length;
        double[][][] outputData = new double[iDL][][];
        for (int dataIndex = 0; dataIndex < iDL; dataIndex++) {
            for (int i = 0; i < keys.length; i++) {
                stats[i] = new DescriptiveStatistics(statSize);
            }
            double[][] currentData = inputData[dataIndex];
            int cDL = currentData.length;
            outputData[dataIndex] = new double[cDL][];
            for (int rowIndex = 0; rowIndex < cDL; rowIndex++) {
                double[] currentRow = currentData[rowIndex];
                int cRL = currentRow.length;
                outputData[dataIndex][rowIndex] = new double[cRL];
                for (int colIndex = 0; colIndex < cRL; colIndex++) {
                    if (keys[colIndex]) {
                        stats[colIndex].addValue(currentRow[colIndex]);
                    }
                    if (rowIndex >= window) {
                        if (keys[colIndex]) {
                            outputData[dataIndex][rowIndex - window][colIndex] = stats[colIndex].getMean();
                        } else {
                            outputData[dataIndex][rowIndex - window][colIndex] = currentData[rowIndex - window][colIndex];
                        }
                    }
                }
            }
            for (int rowIndex = cDL - window; rowIndex < cDL; rowIndex++) {
                double[] currentRow = currentData[rowIndex];
                int cRL = currentRow.length;
                for (int colIndex = 0; colIndex < cRL; colIndex++) {
                    if (keys[colIndex]) {
                        DescriptiveStatistics stats1 = new DescriptiveStatistics();
                        for (int i = statSize - (cDL - rowIndex); i < stats[colIndex].getN(); i++) {
                            stats1.addValue(stats[colIndex].getElement(i));
                        }
                        outputData[dataIndex][rowIndex][colIndex] = stats1.getMean();
                    } else {
                        outputData[dataIndex][rowIndex][colIndex] = currentData[rowIndex][colIndex];
                    }
                }
            }
        }
        return outputData;
    }
}
