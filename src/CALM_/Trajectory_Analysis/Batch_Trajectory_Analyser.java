/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.Trajectory_Analysis;

import IO.File.FileExtensionFilter;
import UtilClasses.GenUtils;
import UtilClasses.Utilities;
import ij.plugin.PlugIn;
import java.io.File;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Batch_Trajectory_Analyser implements PlugIn {

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
        File[] fileList = inputDir.listFiles(new FileExtensionFilter(new String[]{"csv"}));
        Trajectory_Analyser ta = new Trajectory_Analyser(true);
        ta.showDialog(ta.getFileHeadings(fileList[0]));
        for (File f : fileList) {
            ta.run(f.getAbsolutePath());
        }
    }
}
