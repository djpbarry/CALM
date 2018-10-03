/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.Trajectory_Analysis;

import IO.DataReader;
import UtilClasses.GenUtils;
import UtilClasses.Utilities;
import ij.IJ;
import ij.plugin.PlugIn;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Summarise_Trajectory_MSD_Data implements PlugIn {

    public void run(String args) {
        File inputDir = null;
        try {
            inputDir = Utilities.getFolder(null, "Select input folder", false);
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to read input directory.");
        }
        if (inputDir == null) {
            return;
        }
        Collection<File> fileList = FileUtils.listFiles(inputDir, new String[]{"csv"}, true);
        ArrayList<ArrayList<ArrayList<Double>>> rollingAverager = new ArrayList();
        ArrayList<Double> timesteps = new ArrayList();
        File outputFile = new File(String.format("%s%s%s", inputDir.getAbsolutePath(), File.separator, "Trajectory_Summary.csv"));
        for (File f : fileList) {
            if (!f.getName().contentEquals(Trajectory_Analyser.MSD)) {
                continue;
            }
            IJ.log(String.format("Processing %s\n", f.getName()));
            try {
                processData(rollingAverager, timesteps, DataReader.readCSVFile(f, CSVFormat.DEFAULT, new ArrayList(), null));
            } catch (Exception e) {
                GenUtils.error("Cannot read file.");
            }
        }
        try {
            outputData(outputFile, rollingAverager, timesteps);
        } catch (IOException e) {
            GenUtils.error("Failed to save output");
        }
    }

    void processData(ArrayList<ArrayList<ArrayList<Double>>> rollingAverager, ArrayList<Double> timesteps, double[][] data) {
        for (int row = 0; row < data.length; row++) {
            if (timesteps.size() <= row) {
                timesteps.add(data[row][0]);
                rollingAverager.add(new ArrayList());
                rollingAverager.get(row).add(new ArrayList());
                rollingAverager.get(row).add(new ArrayList());
            }
            for (int col = 1; col < data[row].length; col += 3) {
                if (!Double.isNaN(data[row][col])) {
                    rollingAverager.get(row).get(0).add(data[row][col]);
                    rollingAverager.get(row).get(1).add(data[row][col + 1] / Math.sqrt(data[row][col + 2]));
                }
            }
        }
    }

    void outputData(File outputFile, ArrayList<ArrayList<ArrayList<Double>>> rollingAverager, ArrayList<Double> timesteps) throws IOException {
        double[][] output = new double[timesteps.size()][4];
        Mean mean = new Mean();
        StandardDeviation sd = new StandardDeviation();
        for (int i = 0; i < output.length; i++) {
            output[i][0] = timesteps.get(i);
            double[] means = new double[rollingAverager.get(i).get(0).size()];
            double[] weights = new double[rollingAverager.get(i).get(1).size()];

            for (int j = 0; j < means.length; j++) {
                means[j] = rollingAverager.get(i).get(0).get(j);
                weights[j] = rollingAverager.get(i).get(1).get(j);
            }
            try {
                output[i][1] = mean.evaluate(means, weights);
                output[i][2] = sd.evaluate(means);
            } catch (MathIllegalArgumentException e) {
                output[i][1] = 0.0;
                output[i][2] = 0.0;
            }
            output[i][3] = means.length;
        }
        (new Trajectory_Analyser()).saveData(new double[][][]{output}, outputFile.getName(),
                new String[]{"Time Step (s)",
                    String.format("Mean Square Displacement (%s^2)", Trajectory_Analyser.MIC),
                    "Standard Deviation", "N"},
                outputFile.getParentFile());
    }
}
