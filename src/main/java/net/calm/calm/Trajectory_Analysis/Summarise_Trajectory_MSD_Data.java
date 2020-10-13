/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.calm.calm.Trajectory_Analysis;

import ij.IJ;
import ij.plugin.PlugIn;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.calm.iaclasslibrary.IO.DataReader;
import net.calm.iaclasslibrary.UtilClasses.GenUtils;
import net.calm.iaclasslibrary.UtilClasses.Utilities;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Summarise_Trajectory_MSD_Data implements PlugIn {

    private static File inputDir;

    public void run(String args) {
        try {
            inputDir = Utilities.getFolder(null, "Select input folder", false);
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to read input directory.");
        }
        if (inputDir == null) {
            return;
        }
        Collection<File> fileList = FileUtils.listFiles(inputDir, new String[]{"csv"}, true);
        ArrayList<ArrayList<DescriptiveStatistics>> rollingAverager = new ArrayList();
        ArrayList<Double> timesteps = new ArrayList();
        File outputFile = new File(String.format("%s%s%s%s", inputDir.getAbsolutePath(), File.separator, inputDir.getName(), "_MSD_Data_Summary.csv"));
        for (File f : fileList) {
            if (!f.getName().contentEquals(Trajectory_Analyser.MSD)) {
                continue;
            }
            IJ.log(String.format("Processing %s", f.getAbsolutePath()));
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
        IJ.log("Done");
    }

    void processData(ArrayList<ArrayList<DescriptiveStatistics>> rollingAverager, ArrayList<Double> timesteps, double[][] data) {
        for (int row = 0; row < data.length; row++) {
            if (timesteps.size() <= row) {
                timesteps.add(data[row][0]);
                rollingAverager.add(new ArrayList());
                rollingAverager.get(row).add(new DescriptiveStatistics());
                rollingAverager.get(row).add(new DescriptiveStatistics());
            }
            for (int col = 1; col < data[row].length; col += 3) {
                if (!Double.isNaN(data[row][col])) {
                    rollingAverager.get(row).get(0).addValue(data[row][col]);
                    rollingAverager.get(row).get(1).addValue(Math.pow(data[row][col + 1], 2.0));
                }
            }
        }
    }

    void outputData(File outputFile, ArrayList<ArrayList<DescriptiveStatistics>> rollingAverager, ArrayList<Double> timesteps) throws IOException {
        double[][] output = new double[timesteps.size()][4];
        for (int i = 0; i < output.length; i++) {
            output[i][0] = timesteps.get(i);
            output[i][1] = rollingAverager.get(i).get(0).getMean();
            output[i][2] = Math.sqrt(rollingAverager.get(i).get(1).getMean());
            output[i][3] = rollingAverager.get(i).get(0).getN();
        }
        IJ.log(String.format("Saving %s", outputFile.getAbsolutePath()));
        (new Trajectory_Analyser()).saveData(new double[][][]{output}, outputFile.getName(),
                new String[]{"Time Step (s)",
                    String.format("Mean Square Displacement (%s^2)", Trajectory_Analyser.MIC),
                    "Standard Deviation", "N"},
                outputFile.getParentFile());
    }
}
