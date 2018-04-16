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
import java.util.LinkedHashMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Trajectory_Analyser {

    private File inputFile = new File("D:\\OneDrive - The Francis Crick Institute\\Working Data\\Ultanir\\TrkB\\Manual tracking");
    private double minVel = 0.01;
    private double minDist = 1.0;
    private double timeRes = 1.0;
    private int X_INDEX, Y_INDEX, ID_INDEX;
    private final String TITLE = "Trajectory Analysis";
    private final String MIC_PER_SEC = String.format("%cm/s", IJ.micronSymbol);
    private LinkedHashMap<Integer, Integer> idIndexMap;

    public Trajectory_Analyser() {

    }
//D:\OneDrive - The Francis Crick Institute\Working Data\Ultanir\TrkB\Manual tracking

    public void run() {
        double[][][] data;
        double[][] inputData;
        ArrayList<String> headings = new ArrayList();
        ArrayList<String> labels = new ArrayList();
        try {
            inputFile = Utilities.getFile(inputFile, "Select input file", true);
            inputData = DataReader.readCSVFile(inputFile, CSVFormat.DEFAULT, headings, labels);
        } catch (Exception e) {
            GenUtils.error("Cannot read input file.");
            return;
        }
        String[] headingsArray = new String[headings.size()];
        headingsArray = headings.toArray(headingsArray);
        if (!showDialog(headingsArray)) {
            return;
        }
        double[][][] vels = calcInstVels(processData(inputData), 0, 1);
        double[][] meanVels = calcMeanVels(vels, minVel);
        double[][][] runLengths = calcRunLengths(vels, minVel);
        try {
            saveVelData(vels);
            saveMeanVels(meanVels);
            saveRunLengths(runLengths);
        } catch (IOException e) {
        }
    }

    double[][][] processData(double[][] inputData) {
        int count = 1;
        int id = (int) Math.round(inputData[0][ID_INDEX]);
        ArrayList<Integer> lengths = new ArrayList();
        int l = 0;
        idIndexMap = new LinkedHashMap();
        for (int i = 0; i < inputData.length; i++) {
            if (inputData[i][ID_INDEX] > id) {
                id = (int) Math.round(inputData[i][ID_INDEX]);
                count++;
                lengths.add(l);
                l = 0;
            }
            l++;
        }
        lengths.add(l);
        double[][][] output = new double[count][][];
        output[0] = new double[lengths.get(0)][3];
        id = (int) Math.round(inputData[0][ID_INDEX]);
        idIndexMap.put(0, id);
        for (int j = 0, k = 0, index = 0; j < inputData.length; j++) {
            if (inputData[j][ID_INDEX] > id) {
                id = (int) Math.round(inputData[j][ID_INDEX]);
                k++;
                index = 0;
                output[k] = new double[lengths.get(k)][3];
                idIndexMap.put(k, id);
            }
            output[k][index][0] = inputData[j][X_INDEX];
            output[k][index][1] = inputData[j][Y_INDEX];
            output[k][index][2] = inputData[j][ID_INDEX];
            index++;
        }
        return output;
    }

    double[][][] calcInstVels(double[][][] data, int X_INDEX, int Y_INDEX) {
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
        int N = (int) xVel.getN();
        double dist = Utils.calcDistance(xVel.getElement(0), yVel.getElement(0), xVel.getElement(N - 1), yVel.getElement(N - 1));
        if (dist > minDist) {
            current.add(new double[]{mag, theta, dist, xVel.getN()});
        }
    }

    void saveVelData(double[][][] vels) throws IOException {
        File dir = inputFile.getParentFile();
        File velFile = new File(String.format("%s%s%s", dir, File.separator, "Instantaneous_Velocities.csv"));
        if (velFile.exists()) {
            velFile.delete();
        }
        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(velFile), GenVariables.ISO), CSVFormat.EXCEL);
        printer.printRecord(((Object[]) new String[]{"Track ID",
            String.format("X Vel (%s)", MIC_PER_SEC),
            String.format("Y Vel (%s)", MIC_PER_SEC), String.format("Mag (%s)", MIC_PER_SEC),
            String.format("Theta (%c)", IJ.degreeSymbol)}));
        printer.close();
        for (int i = 0; i < vels.length; i++) {
            double[][] v = vels[i];
            String[] rowLabels = new String[v.length];
            for (int j = 0; j < v.length; j++) {
                rowLabels[j] = String.valueOf(idIndexMap.get(i));
            }
            DataWriter.saveValues(v, velFile, null, rowLabels, true);
        }
    }

    void saveMeanVels(double[][] meanVels) throws IOException {
        File dir = inputFile.getParentFile();
        File velData = new File(String.format("%s%s%s", dir, File.separator, "Mean_Velocities.csv"));
        if (velData.exists()) {
            velData.delete();
        }
        String[] rowLabels = new String[meanVels.length];
        for (int i = 0; i < meanVels.length; i++) {
            rowLabels[i] = String.valueOf(idIndexMap.get(i));
        }
        DataWriter.saveValues(meanVels, velData, new String[]{"Track ID", String.format("Mag (%s)", MIC_PER_SEC), String.format("Theta (%c)", IJ.degreeSymbol)}, rowLabels, false);
    }

    void saveRunLengths(double[][][] runs) throws IOException {
        File dir = inputFile.getParentFile();
        File velData = new File(String.format("%s%s%s", dir, File.separator, "Run_Lengths.csv"));
        if (velData.exists()) {
            velData.delete();
        }
        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(velData), GenVariables.ISO), CSVFormat.EXCEL);
        printer.printRecord(((Object[]) new String[]{"Track ID", String.format("Mag (%s)", MIC_PER_SEC), String.format("Theta (%c)", IJ.degreeSymbol), "Net Distance", "No. of Frames"}));
        printer.close();
        for (int i = 0; i < runs.length; i++) {
            double[][] v = runs[i];
            String[] rowLabels = new String[v.length];
            for (int j = 0; j < v.length; j++) {
                rowLabels[j] = String.valueOf(idIndexMap.get(i));
            }
            DataWriter.saveValues(v, velData, null, rowLabels, true);
        }
    }

    boolean showDialog(String[] headings) {
        GenericDialog gd = new GenericDialog(TITLE);
        gd.addNumericField("Minimum Velocity", minVel, 3, 5, MIC_PER_SEC);
        gd.addNumericField("Minimum Distance", minDist, 3, 5, "");
        gd.addNumericField("Temporal Resolution", timeRes, 3, 5, "Hz");
        gd.addChoice("Specify Column for X coordinates:", headings, headings[0]);
        gd.addChoice("Specify Column for Y coordinates:", headings, headings[1]);
        gd.addChoice("Specify Column for Track ID:", headings, headings[2]);
        gd.showDialog();
        if (!gd.wasOKed()) {
            return false;
        }
        minVel = gd.getNextNumber();
        minDist = gd.getNextNumber();
        timeRes = gd.getNextNumber();
        X_INDEX = gd.getNextChoiceIndex();
        Y_INDEX = gd.getNextChoiceIndex();
        ID_INDEX = gd.getNextChoiceIndex();
        return true;
    }
}
