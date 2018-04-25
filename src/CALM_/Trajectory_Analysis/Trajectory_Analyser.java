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
import ij.plugin.PlugIn;
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
public class Trajectory_Analyser implements PlugIn {

    private File inputFile = new File("D:\\OneDrive - The Francis Crick Institute\\Working Data\\Ultanir\\TrkB\\Manual tracking");
    private double minVel = 0.01;
    private double minDist = 1.0;
    private double framesPerSec = 1.0;
    private static int INPUT_X_INDEX = 4, INPUT_Y_INDEX = 5, INPUT_ID_INDEX = 2, INPUT_FRAME_INDEX = 8;
    private final int _X_ = 0, _Y_ = 1, _T_ = 2, _ID_ = 3;
    private final int V_X = 0, V_Y = 1, V_M = 2, V_Th = 3, V_F = 4, V_ID = 5, V_D = 6, V_T = 7;
    private final String TITLE = "Trajectory Analysis";
    private final String MIC_PER_SEC = String.format("%cm/s", IJ.micronSymbol);
    private LinkedHashMap<Integer, Integer> idIndexMap;

    public Trajectory_Analyser() {

    }
//D:\OneDrive - The Francis Crick Institute\Working Data\Ultanir\TrkB\Manual tracking

    public void run(String args) {
        IJ.log(String.format("Running %s\n", TITLE));
        double[][] inputData;
        ArrayList<String> headings = new ArrayList();
        ArrayList<String> labels = new ArrayList();
        try {
            inputFile = Utilities.getFile(inputFile, "Select input file", true);
            IJ.log(String.format("Reading %s...", inputFile.getAbsolutePath()));
            inputData = DataReader.readCSVFile(inputFile, CSVFormat.DEFAULT, headings, labels);
            IJ.log("Parsing data...");
        } catch (Exception e) {
            GenUtils.error("Cannot read input file.");
            return;
        }
        String[] headingsArray = new String[headings.size()];
        headingsArray = headings.toArray(headingsArray);
        if (!showDialog(headingsArray)) {
            return;
        }
        IJ.log("Calculating instananeous velocities...");
        double[][][] vels = calcInstVels(processData(inputData), _X_, _Y_, _T_);
        IJ.log("Calculating mean velocities...");
        double[][] meanVels = calcMeanVels(vels, minVel);
        IJ.log("Analysing runs...");
        double[][][] runLengths = calcRunLengths(vels, minVel);
        try {
            IJ.log("Saving outputs...");
            saveVelData(vels);
            saveMeanVels(meanVels);
            saveRunLengths(runLengths);
        } catch (IOException e) {
        }
        IJ.log("Done.");
    }

    double[][][] processData(double[][] inputData) {
        int count = 1;
        int id = (int) Math.round(inputData[0][INPUT_ID_INDEX]);
        ArrayList<Integer> lengths = new ArrayList();
        int l = 0;
        idIndexMap = new LinkedHashMap();
        for (int i = 0; i < inputData.length; i++) {
            if (inputData[i][INPUT_ID_INDEX] > id) {
                id = (int) Math.round(inputData[i][INPUT_ID_INDEX]);
                count++;
                lengths.add(l);
                l = 0;
            }
            l++;
        }
        lengths.add(l);
        double[][][] output = new double[count][][];
        output[0] = new double[lengths.get(0)][4];
        id = (int) Math.round(inputData[0][INPUT_ID_INDEX]);
        idIndexMap.put(0, id);
        for (int j = 0, k = 0, index = 0; j < inputData.length; j++) {
            if (inputData[j][INPUT_ID_INDEX] > id) {
                id = (int) Math.round(inputData[j][INPUT_ID_INDEX]);
                k++;
                index = 0;
                output[k] = new double[lengths.get(k)][4];
                idIndexMap.put(k, id);
            }
            output[k][index][_X_] = inputData[j][INPUT_X_INDEX];
            output[k][index][_Y_] = inputData[j][INPUT_Y_INDEX];
            output[k][index][_T_] = inputData[j][INPUT_FRAME_INDEX];
            output[k][index][_ID_] = inputData[j][INPUT_ID_INDEX];
            index++;
        }
        return output;
    }

    double[][][] calcInstVels(double[][][] data, int X_INDEX, int Y_INDEX, int FRAME_INDEX) {
        int a = data.length;
        double[][][] vels = new double[a][][];
        for (int i = 0; i < a; i++) {
            int b = data[i].length;
            vels[i] = new double[b - 1][8];
            for (int j = 1; j < b; j++) {
                double x2 = data[i][j][X_INDEX];
                double x1 = data[i][j - 1][X_INDEX];
                double y2 = data[i][j][Y_INDEX];
                double y1 = data[i][j - 1][Y_INDEX];
                double dt = data[i][j][FRAME_INDEX] - data[i][j - 1][FRAME_INDEX];
                vels[i][j - 1][V_X] = framesPerSec * (x2 - x1) / dt;
                vels[i][j - 1][V_Y] = framesPerSec * (y2 - y1) / dt;
                vels[i][j - 1][V_D] = Utils.calcDistance(x1, y1, x2, y2);
                vels[i][j - 1][V_M] = framesPerSec * vels[i][j - 1][V_D] / dt;
                vels[i][j - 1][V_Th] = Utils.arcTan(x2 - x1, y2 - y1);
                vels[i][j - 1][V_F] = dt;
                vels[i][j - 1][V_ID] = idIndexMap.get(i);
                vels[i][j - 1][V_T] = dt / framesPerSec;
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
                if (vels[i][j][V_M] > minVel) {
                    xVel.addValue(vels[i][j][_X_]);
                    yVel.addValue(vels[i][j][_Y_]);
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
            int id = idIndexMap.get(i);
            ArrayList<double[]> current = new ArrayList();
            int b = vels[i].length;
            DescriptiveStatistics mVel = null;
            DescriptiveStatistics theta = null;
            double time = 0.0, cumDist = 0.0, netDistX = 0.0, netDistY = 0.0;
            boolean dir = vels[i][0][V_Th] > 90 && vels[i][0][V_Th] < 270;
            boolean lastDir = dir;
            for (int j = 0; j < b; j++) {
                dir = vels[i][j][V_Th] > 90 && vels[i][j][V_Th] < 270;
                if (vels[i][j][V_M] >= minVel && dir == lastDir) {
                    if (mVel == null) {
                        mVel = new DescriptiveStatistics();
                        theta = new DescriptiveStatistics();
                        time = 0.0;
                    }
                    time += vels[i][j][V_F];
                    for (int t = 0; t < vels[i][j][V_F]; t++) {
                        mVel.addValue(Math.sqrt(vels[i][j][V_X] * vels[i][j][V_X] + vels[i][j][V_Y] * vels[i][j][V_Y]));
                        theta.addValue(vels[i][j][V_Th]);
                    }
                    theta.addValue(vels[i][j][V_Y]);
                    cumDist += vels[i][j][V_D];
                    netDistX += vels[i][j][V_X] * vels[i][j][V_F];
                    netDistY += vels[i][j][V_Y] * vels[i][j][V_F];
                    lastDir = dir;
                } else if (mVel != null) {
                    addRun(mVel, theta, time, current, id, cumDist, Utils.calcDistance(0.0, 0.0, netDistX, netDistY));
                    mVel = null;
                    theta = null;
                    time = 0.0;
                    cumDist = 0.0;
                    netDistX = 0.0;
                    netDistY = 0.0;
                    if (j < b - 1) {
                        lastDir = vels[i][j + 1][V_Th] > 90 && vels[i][j + 1][V_Th] < 270;
                    }
                }
            }
            if (mVel != null) {
                addRun(mVel, theta, time, current, id, cumDist, Utils.calcDistance(0.0, 0.0, netDistX, netDistY));
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

    private void addRun(DescriptiveStatistics mVel, DescriptiveStatistics tVel, double time, ArrayList<double[]> current, int id, double dist, double netDist) {
        double meanMag = mVel.getMean();
        double meanTheta = tVel.getMean();
        if (netDist > minDist) {
            current.add(new double[]{id, meanMag, meanTheta, netDist, dist, time / framesPerSec});
        }
    }

    void saveVelData(double[][][] vels) throws IOException {
        File dir = inputFile.getParentFile();
        File velFile = new File(String.format("%s%s%s", dir, File.separator, "Instantaneous_Velocities.csv"));
        if (velFile.exists()) {
            velFile.delete();
        }
        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(velFile), GenVariables.ISO), CSVFormat.EXCEL);
        printer.printRecord(((Object[]) new String[]{String.format("X Vel (%s)", MIC_PER_SEC),
            String.format("Y Vel (%s)", MIC_PER_SEC), String.format("Mag (%s)", MIC_PER_SEC),
            String.format("Theta (%c)", IJ.degreeSymbol),
            "Time (frames)", "Track ID", "Distance", "Time (s)"}));
        printer.close();
        for (int i = 0; i < vels.length; i++) {
            double[][] v = vels[i];
            DataWriter.saveValues(v, velFile, null, null, true);
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
        printer.printRecord(((Object[]) new String[]{"Track ID", String.format("Mag (%s)", MIC_PER_SEC), String.format("Theta (%c)", IJ.degreeSymbol), "Net Distance", "Cumulative Distance", "Duration (s)"}));
        printer.close();
        for (int i = 0; i < runs.length; i++) {
            double[][] v = runs[i];
            DataWriter.saveValues(v, velData, null, null, true);
        }
    }

    boolean showDialog(String[] headings) {
        GenericDialog gd = new GenericDialog(TITLE);
        gd.addNumericField("Minimum Velocity", minVel, 3, 5, MIC_PER_SEC);
        gd.addNumericField("Minimum Distance", minDist, 3, 5, "");
        gd.addNumericField("Temporal Resolution", framesPerSec, 3, 5, "Hz");
        gd.addChoice("Specify Column for X coordinates:", headings, headings[INPUT_X_INDEX]);
        gd.addChoice("Specify Column for Y coordinates:", headings, headings[INPUT_Y_INDEX]);
        gd.addChoice("Specify Column for Frame Number:", headings, headings[INPUT_FRAME_INDEX]);
        gd.addChoice("Specify Column for Track ID:", headings, headings[INPUT_ID_INDEX]);
        gd.showDialog();
        if (!gd.wasOKed()) {
            return false;
        }
        minVel = gd.getNextNumber();
        minDist = gd.getNextNumber();
        framesPerSec = gd.getNextNumber();
        INPUT_X_INDEX = gd.getNextChoiceIndex();
        INPUT_Y_INDEX = gd.getNextChoiceIndex();
        INPUT_FRAME_INDEX = gd.getNextChoiceIndex();
        INPUT_ID_INDEX = gd.getNextChoiceIndex();
        return true;
    }
}
