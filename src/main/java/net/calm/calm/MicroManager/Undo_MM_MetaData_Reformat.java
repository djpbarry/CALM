/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.calm.calm.MicroManager;

import ij.IJ;
import ij.plugin.PlugIn;
import java.io.File;
import java.nio.file.Files;
import java.util.Collection;

import net.calm.iaclasslibrary.UtilClasses.Utilities;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.PrefixFileFilter;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Undo_MM_MetaData_Reformat implements PlugIn {

    private static File inputDir;

    public void run(String args) {
        try {
            inputDir = Utilities.getFolder(inputDir, "Specify input directory", true);
            IJ.log(String.format("Root Directory: %s\n", inputDir.getAbsolutePath()));
            Collection<File> metaIter = FileUtils.listFiles(inputDir, new PrefixFileFilter("Cleaned", IOCase.INSENSITIVE), null);
            for (File f : metaIter) {
                IJ.log(String.format("Deleting %s", f.getAbsolutePath()));
                Files.delete(f.toPath());
            }
            Collection<File> backupIter = FileUtils.listFiles(inputDir, new String[]{"backup"}, false);
            for (File f : backupIter) {
                String filename = f.getAbsolutePath();
                int index = filename.lastIndexOf(".backup");
                File newFileName = new File(filename.substring(0, index));
                f.renameTo(newFileName);
            }
        } catch (Exception e) {
            IJ.log("Sorry, we've encountered a problem - aborting.");
            e.printStackTrace();
        }
        IJ.log("Done");
    }

}
