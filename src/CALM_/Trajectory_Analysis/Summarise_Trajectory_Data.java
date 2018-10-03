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
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Summarise_Trajectory_Data implements PlugIn {

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
        ArrayList<DescriptiveStatistics> rollingAverager = new ArrayList();
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

    void processData(ArrayList<DescriptiveStatistics> rollingAverager, ArrayList<Double> timesteps, double[][] data) {
        for (int row = 0; row < data.length; row++) {
            if (timesteps.size() <= row) {
                timesteps.add(data[row][0]);
                rollingAverager.add(new DescriptiveStatistics());
            }
            for (int col = 1; col < data[row].length; col += 3) {
                if (!Double.isNaN(data[row][col])) {
                    rollingAverager.get(row).addValue(data[row][col]);
                }
            }
        }
    }

    void outputData(File outputFile, ArrayList<DescriptiveStatistics> rollingAverager, ArrayList<Double> timesteps) throws IOException {
        double[][] output = new double[timesteps.size()][4];
        for (int i = 0; i < output.length; i++) {
            output[i][0] = timesteps.get(i);
            output[i][1] = rollingAverager.get(i).getMean();
            output[i][2] = rollingAverager.get(i).getStandardDeviation();
            output[i][3] = rollingAverager.get(i).getN();
        }
        (new Trajectory_Analyser()).saveData(new double[][][]{output}, outputFile.getName(),
                new String[]{"Time Step (s)",
                    String.format("Mean Square Displacement (%s^2)", Trajectory_Analyser.MIC),
                    "Standard Deviation", "N"},
                outputFile.getParentFile());
    }
}
