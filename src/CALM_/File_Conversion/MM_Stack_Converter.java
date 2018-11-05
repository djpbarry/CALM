/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.File_Conversion;

import IO.BioFormats.BioFormatsImg;
import UtilClasses.GenUtils;
import UtilClasses.Utilities;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class MM_Stack_Converter implements PlugIn {

    public void run(String args) {
        try {
            File inputDir = Utilities.getFolder(null, "Specify input directory", true);
            IJ.log(String.format("Input: %s", inputDir.getAbsolutePath()));
            File outputDir = Utilities.getFolder(inputDir, "Specify output directory", true);
            IJ.log(String.format("Output: %s", outputDir.getAbsolutePath()));
            File[] fileList = inputDir.listFiles();
            int i = -1;
            boolean valid = false;
            BioFormatsImg img = null;
            while (!valid) {
                i++;
                img = new BioFormatsImg(fileList[i].getAbsolutePath());
                valid = img.isValidID();
            }
            if (img == null) {
                return;
            }
            int Ns = img.getSeriesCount();
            for (int s = 1; s <= Ns; s++) {
                IJ.log(String.format("Converting %s series %d", fileList[i].getName(), s));
                img.setImg(s);
                ImagePlus imp = img.getImg();
                IJ.saveAs(imp, "TIF", String.format("%s%s%s_S%d", outputDir, File.separator, fileList[i].getName(), s));
            }
        } catch (InterruptedException | InvocationTargetException e) {
            GenUtils.logError(e, "Error opening directories.");
        }
    }
}
