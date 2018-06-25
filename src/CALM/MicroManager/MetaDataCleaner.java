/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM.MicroManager;

import UtilClasses.GenVariables;
import UtilClasses.Utilities;
import ij.IJ;
import ij.plugin.PlugIn;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class MetaDataCleaner implements PlugIn {

    private Charset charSet = GenVariables.UTF8;
    private String[] keys = {"IJType", "GridRow", "Comment", "UUID", "Height",
        "GridColumn", "CameraTimeout", "Depth", "BitDepth", "KeepShutterOpenChannels",
        "PixelType", "Source", "TimeFirst", "ChColors", "SlicesFirst",
        "Width", "Positions", "ROI"};
    private String[] newKeys = {"IJ-Type", "Grid-Row", "Comment-", "UUID-", "Height-",
        "Grid-Column", "Camera-Timeout", "Depth-", "Bit-Depth", "KeepShutter-OpenChannels",
        "Pixel-Type", "Source-", "Time-First", "Ch-Colors", "Slices-First",
        "Width-", "Positions-", "ROI-"};
    private File inputDir;
    private String ext = "txt";
    private String metadata = "metadata";

    public void run(String args) {
        try {
//            ImagePlus[] imp = BF.openImagePlus("C:\\Users\\barryd\\AcquisitionData\\Untitled_2\\Untitled_2_MMStack_Pos0_metadata.txt");
            inputDir = Utilities.getFolder(null, "Specify input directory", true);
            Iterator<File> iter = FileUtils.iterateFiles(inputDir, new FileNameFilter(), TrueFileFilter.INSTANCE);
            while (iter.hasNext()) {
                File file = iter.next();
                IJ.log(String.format("Processing %s", file.getName()));
                copyFile(file, new File(String.format("%s.backup", file.getAbsolutePath())));
                processFile(file);
                Files.delete(file.toPath());
            }
        } catch (Exception e) {
            IJ.log("Sorry, we've encountered a problem - aborting.");
            e.printStackTrace();
        }
        IJ.log("Done");
    }

    String checkLine(String line) {
        for (int i = 0; i < keys.length; i++) {
            if (line.contains(keys[i])) {
                int index = line.indexOf(keys[i]);
                String newLine = line.substring(0, index);
                newLine = newLine.concat(newKeys[i]);
                newLine = newLine.concat(line.substring(index + keys[i].length()));
                line = newLine;
            }
        }
        return line;
    }

    void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }

    void processFile(File file) throws IOException {
        File output = new File(String.format("%s%sCleaned_%s", FilenameUtils.getFullPath(file.getAbsolutePath()), File.separator, FilenameUtils.getName(file.getAbsolutePath())));
        if (output.exists()) {
            return;
        }
        output.setWritable(true);
        output.createNewFile();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), charSet));
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSet));
        String line = br.readLine();
        int checkedLines = 0;
        while (line != null && checkedLines < keys.length) {
            line = checkLine(line);
            bw.append(line);
            bw.newLine();
            line = br.readLine();
        }
        br.close();
        bw.close();
    }

    private class FileNameFilter implements IOFileFilter {

        public boolean accept(File file) {
            String filename = file.getName();
            return FilenameUtils.getExtension(filename).equalsIgnoreCase(ext) && FilenameUtils.getBaseName(filename).endsWith(metadata);
        }

        public boolean accept(File file, String name) {
            return accept(new File(String.format("%s%s%s", file.getAbsolutePath(), File.separator, name)));
        }
    }
}
