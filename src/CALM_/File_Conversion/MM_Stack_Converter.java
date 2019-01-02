/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.File_Conversion;

import DateAndTime.Time;
import IO.BioFormats.BioFormatsImg;
import UtilClasses.GenUtils;
import UtilClasses.Utilities;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.HyperStackConverter;
import ij.plugin.PlugIn;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class MM_Stack_Converter implements PlugIn {

    public void run(String args) {
        LocalDateTime startTime = LocalDateTime.now();
        try {
            File inputDir = Utilities.getFolder(null, "Specify input directory", true);
            IJ.log(String.format("Input: %s", inputDir.getAbsolutePath()));
            File outputDir = Utilities.getFolder(inputDir, "Specify output directory", true);
            IJ.log(String.format("Output: %s", outputDir.getAbsolutePath()));
            File[] fileList = inputDir.listFiles();
            int i = -1;
            boolean valid = false;
            BioFormatsImg img = null;
            IJ.log("Parsing metadata. This may take some time for large datasets.");
            while (!valid) {
                i++;
                img = new BioFormatsImg(fileList[i].getAbsolutePath());
                valid = img.isValidID();
            }
            if (img == null) {
                return;
            }
            int Ns = img.getSeriesCount();
            for (int s = 0; s < Ns; s++) {
                IJ.log(String.format("Reading %s series %d", fileList[i].getName(), s));
                img.loadPixelData(s);
                ImagePlus imp = HyperStackConverter.toHyperStack(img.getLoadedImage(), img.getChannelCount(), img.getSizeZ(), 1, "xyzct", "composite");
                IJ.log(String.format("Writing %s series %d", fileList[i].getName(), s));
                IJ.run(imp, "Bio-Formats Exporter", "save=" + String.format("%s%s%s_S%d.ome.tif", outputDir, File.separator, fileList[i].getName(), s) + " compression=Uncompressed");
            }
        } catch (InterruptedException | InvocationTargetException e) {
            GenUtils.logError(e, "Error opening directories.");
        }
        IJ.log("Done");
        IJ.log(Time.getDurationAsString(startTime));
    }
}
