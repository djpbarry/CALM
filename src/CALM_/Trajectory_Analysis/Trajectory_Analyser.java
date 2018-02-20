/*
 * Copyright (C) 2018 David Barry <david.barry at crick dot ac dot uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package CALM_.Trajectory_Analysis;

import IAClasses.Utils;
import IO.DataReader;
import IO.DataWriter;
import UtilClasses.GenUtils;
import UtilClasses.GenVariables;
import UtilClasses.Utilities;
import ij.IJ;
import ij.gui.GenericDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Trajectory_Analyser {

    private File inputFile;
    private double minVel = 0.01;
    private double timeRes = 1.0;
    private final int X_INDEX = 0, Y_INDEX = 1;
    private final String TITLE = "Trajectory Analysis";
    private final String MIC_PER_SEC = String.format("%cm/s", IJ.micronSymbol);

    public Trajectory_Analyser() {

    }

    public void run() {
        double[][][] data;
        try {
            inputFile = Utilities.getFile(inputFile, "Select input file", true);
            data = DataReader.readTabbedFile(inputFile);
        } catch (Exception e) {
            GenUtils.error("Cannot read input file.");
            return;
        }
        if (!showDialog()) {
            return;
        }
        double[][][] vels = calcInstVels(data);
        double[][] meanVels = calcMeanVels(vels, minVel);
        double[][][] runLengths = calcRunLengths(vels, minVel);
        try {
            saveVelData(vels);
            saveMeanVels(meanVels);
            saveRunLengths(runLengths);
        } catch (IOException e) {
        }
    }

    double[][][] calcInstVels(double[][][] data) {
        int a = data.length;
        double[][][] vels = new double[a][][];
        for (int i = 0; i < a; i++) {
            int b = data[i].length;
            vels[i] = new double[b - 1][4];
            for (int j = 1; j < b; j++) {
                double x2 = data[i][j][X_INDEX];
                double x1 = data[i][j - 1][X_INDEX];
                double y2 = data[i][j][Y_INDEX];
                double y1 = data[i][j - 1][Y_INDEX];
                vels[i][j - 1][0] = (x2 - x1) / timeRes;
                vels[i][j - 1][1] = (y2 - y1) / timeRes;
                vels[i][j - 1][2] = Utils.calcDistance(x1, y1, x2, y2) / timeRes;
                vels[i][j - 1][3] = Utils.arcTan(x2 - x1, y2 - y1);
            }
        }
        return vels;
    }

    double[][] calcMeanVels(double[][][] vels, double minVel) {
        int a = vels.length;
        double[][] meanVels = new double[a][2];
        for (int i = 0; i < a; i++) {
            int b = vels[i].length;
            DescriptiveStatistics xVel = new DescriptiveStatistics();
            DescriptiveStatistics yVel = new DescriptiveStatistics();
            for (int j = 0; j < b; j++) {
                double mag = Math.sqrt(Math.pow(vels[i][j][0], 2.0) + Math.pow(vels[i][j][1], 2.0));
                if (mag > minVel) {
                    xVel.addValue(vels[i][j][0]);
                    yVel.addValue(vels[i][j][1]);
                }
            }
            double meanX = xVel.getMean();
            double meanY = yVel.getMean();
            double mag = Math.sqrt(Math.pow(meanX, 2.0) + Math.pow(meanY, 2.0));
            double theta = Utils.arcTan(meanX, meanY);
            meanVels[i][0] = mag;
            meanVels[i][1] = theta;
        }
        return meanVels;
    }

    double[][][] calcRunLengths(double[][][] vels, double minVel) {
        int a = vels.length;
        ArrayList<ArrayList<double[]>> runs = new ArrayList();
        for (int i = 0; i < a; i++) {
            ArrayList<double[]> current = new ArrayList();
            int b = vels[i].length;
            DescriptiveStatistics xVel = null;
            DescriptiveStatistics yVel = null;
            for (int j = 0; j < b; j++) {
                double mag = Math.sqrt(Math.pow(vels[i][j][0], 2.0) + Math.pow(vels[i][j][1], 2.0));
                if (mag >= minVel) {
                    if (xVel == null) {
                        xVel = new DescriptiveStatistics();
                        yVel = new DescriptiveStatistics();
                    }
                    xVel.addValue(vels[i][j][0]);
                    yVel.addValue(vels[i][j][1]);
                } else if (xVel != null) {
                    addRun(xVel, yVel, current);
                    xVel = null;
                    yVel = null;
                }
            }
            if (xVel != null) {
                addRun(xVel, yVel, current);
            }
            runs.add(current);
        }
        int m = runs.size();
        double[][][] output = new double[m][][];
        for (int k = 0; k < m; k++) {
            ArrayList<double[]> record = runs.get(k);
            int size1 = record.size();
            output[k] = new double[size1][2];
            for (int j = 0; j < size1; j++) {
                double[] current = record.get(j);
                output[k][j] = new double[current.length];
                for (int i = 0; i < current.length; i++) {
                    output[k][j][i] = current[i];
                }
            }
        }
        return output;
    }

    private void addRun(DescriptiveStatistics xVel, DescriptiveStatistics yVel, ArrayList<double[]> current) {
        double meanX = xVel.getMean();
        double meanY = yVel.getMean();
        double mag = Math.sqrt(Math.pow(meanX, 2.0) + Math.pow(meanY, 2.0));
        double theta = Utils.arcTan(meanX, meanY);
        current.add(new double[]{mag, theta, xVel.getN()});
    }

    void saveVelData(double[][][] vels) throws IOException {
        File dir = inputFile.getParentFile();
        File velData = new File(String.format("%s%s%s", dir, File.separator, "Instantaneous_Velocities.csv"));
        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(velData), GenVariables.ISO), CSVFormat.EXCEL);
        printer.printRecord(((Object[]) new String[]{String.format("X Vel (%s)", MIC_PER_SEC),
            String.format("Y Vel (%s)", MIC_PER_SEC), String.format("Mag (%s)", MIC_PER_SEC),
            String.format("Theta (%c)", IJ.degreeSymbol)}));
        printer.close();
        for (int i = 0; i < vels.length; i++) {
            double[][] v = vels[i];
            DataWriter.saveValues(v, velData, new String[]{String.format("Particle %d", i)}, null, true);
        }
    }

    void saveMeanVels(double[][] meanVels) throws IOException {
        File dir = inputFile.getParentFile();
        File velData = new File(String.format("%s%s%s", dir, File.separator, "Mean_Velocities.csv"));
        String[] rowLabels = new String[meanVels.length];
        for (int i = 0; i < meanVels.length; i++) {
            rowLabels[i] = String.format("Particle %d", i);
        }
        DataWriter.saveValues(meanVels, velData, new String[]{String.format("Mag (%s)", MIC_PER_SEC), String.format("Theta (%c)", IJ.degreeSymbol)}, rowLabels, false);
    }

    void saveRunLengths(double[][][] runs) throws IOException {
        File dir = inputFile.getParentFile();
        File velData = new File(String.format("%s%s%s", dir, File.separator, "Run_Lengths.csv"));
        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(velData), GenVariables.ISO), CSVFormat.EXCEL);
        printer.printRecord(((Object[]) new String[]{String.format("Mag (%s)", MIC_PER_SEC), String.format("Theta (%c)", IJ.degreeSymbol), "No. of Frames"}));
        printer.close();
        for (int i = 0; i < runs.length; i++) {
            double[][] v = runs[i];
            DataWriter.saveValues(v, velData, new String[]{String.format("Particle %d", i)}, null, true);
        }
    }

    boolean showDialog() {
        GenericDialog gd = new GenericDialog(TITLE);
        gd.addNumericField("Minimum Velocity", minVel, 3, 5, MIC_PER_SEC);
        gd.addNumericField("Temporal Resolution", timeRes, 3, 5, "Hz");
        gd.showDialog();
        if (!gd.wasOKed()) {
            return false;
        }
        minVel = gd.getNextNumber();
        timeRes = gd.getNextNumber();
        return true;
    }
}
