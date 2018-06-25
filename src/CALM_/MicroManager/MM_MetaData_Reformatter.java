/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.MicroManager;

import UtilClasses.Utilities;
import ij.IJ;
import ij.plugin.PlugIn;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class MM_MetaData_Reformatter implements PlugIn {

    private File inputDir;
    private String ext = "txt";
    private String tif = "tif";
    private String tiff = "tiff";
    private String metadata = "metadata";
    private String cleaned = "Cleaned_";
    private String img = "img";
    private String MMStack = "MMStack";
    private final String USER_DATA = "UserData",
            PROP_VAL = "PropVal",
            PROP_TYPE = "PropType",
            COORDS = "Coords";

    public MM_MetaData_Reformatter() {

    }

    public void run(String args) {
        try {
            inputDir = Utilities.getFolder(null, "Specify input directory", true);
            Iterator<File> metaIter = FileUtils.iterateFiles(inputDir, new MetaFileNameFilter(), TrueFileFilter.INSTANCE);
            while (metaIter.hasNext()) {
                File file = metaIter.next();
                IJ.log(String.format("Processing %s", file.getName()));
                copyFile(file, new File(String.format("%s.backup", file.getAbsolutePath())));
                processFile(file);
                Files.delete(file.toPath());
            }
            Iterator<File> imageIter = FileUtils.iterateFiles(inputDir, new ImageFileNameFilter(), TrueFileFilter.INSTANCE);
            while (imageIter.hasNext()) {
                File file = imageIter.next();
                File newFileName = new File(String.format("%s%s%s", file.getParent(), File.separator, renameFile(file.getName())));
                IJ.log(String.format("Renaming %s", file.getName()));
                file.renameTo(newFileName);
                IJ.wait(0);
            }
        } catch (Exception e) {
            IJ.log("Sorry, we've encountered a problem - aborting.");
            e.printStackTrace();
        }
        IJ.log("Done");
    }

    String renameFile(String filename) {
        if (filename.contains(img) && !filename.contains(MMStack)) {
            String ext = FilenameUtils.getExtension(filename);
            String[] keys = FilenameUtils.getBaseName(filename).split("_");
            String[] itczp = new String[5];
            for (String k : keys) {
                if (k.contains("img")) {
                    itczp[0] = k;
                } else if (k.contains("time")) {
                    itczp[1] = k;
                } else if (k.contains("channel")) {
                    itczp[2] = k;
                } else if (k.contains("z")) {
                    itczp[3] = k;
                } else if (k.contains("position")) {
                    itczp[4] = k;
                }
            }
            filename = String.format("%s_%s_%s_%s_%s.%s", itczp[0], itczp[1], itczp[2], itczp[3], itczp[4], ext);
        }
        return filename;
    }

    private ArrayList<String> reformatJSONData(String jsonData)
            throws IOException {
        StringTokenizer st = new StringTokenizer(jsonData, "\n");
        boolean userData = false;
        boolean doNotAddLine = false;
        String lastKey = "";
        String lastToken = "";
        ArrayList<String> output = new ArrayList();
        while (st.hasMoreTokens()) {
            String line = st.nextToken();
            String token = line.trim();
            boolean open = token.indexOf('[') != -1;
            boolean closed = token.indexOf(']') != -1;
            if (userData && lastToken.startsWith("}") && token.startsWith("},")) {
                userData = false;
                doNotAddLine = true;
            } else if (userData && token.startsWith("}")) {
                doNotAddLine = true;
            }
            if (open || (!open && !closed && !token.equals("{")
                    && !token.startsWith("}"))) {
                int quote = token.indexOf("\"") + 1;
                String key = token.substring(quote, token.indexOf("\"", quote));
                if (key.startsWith(USER_DATA)) {
                    userData = true;
                }
                String value = null;

                if (open == closed) {
                    value = token.substring(token.indexOf(':') + 1);
                } else if (!closed) {
                    final StringBuilder valueBuffer = new StringBuilder();
                    output.add(line);
                    doNotAddLine = true;
                    while (!closed) {
                        token = st.nextToken();
                        output.add(token);
                        closed = token.indexOf(']') != -1;
                        valueBuffer.append(token);
                    }
                    value = valueBuffer.toString();
                    value = value.replaceAll("\n", "");
                }
                if (value == null) {
                    continue;
                }

                int startIndex = value.indexOf('[');
                int endIndex = value.indexOf(']');
                if (endIndex == -1) {
                    endIndex = value.length();
                }

                value = value.substring(startIndex + 1, endIndex).trim();
                if (value.length() == 0) {
                    continue;
                }
                value = value.substring(0, value.length() - 1);
                value = value.replaceAll("\"", "");
                if (value.endsWith(",")) {
                    value = value.substring(0, value.length() - 1);
                }
                if (userData) {
                    doNotAddLine = true;
                    if (key.startsWith(PROP_VAL)) {
                        key = lastKey;
                    } else {
                        value = null;
                    }
                    if (!key.startsWith(PROP_TYPE) && value != null) {
                        doNotAddLine = false;
                        line = String.format("\"%s\": %s,", key, value);
                    }
                }
                if (key.startsWith(COORDS)) {
                    String ext = FilenameUtils.getExtension(key);
                    String[] keys = FilenameUtils.getBaseName(key).split("_");
                    key = String.format("%s_%s_%s_%s_%s.%s", keys[0], keys[3], keys[1], keys[4], keys[2], ext);
                    line = String.format("\"%s\": {", key);
                }
                lastKey = key;
            }
            if (!doNotAddLine) {
                output.add(line);
            }
            doNotAddLine = false;
            lastToken = token;
        }
        return output;
    }

    void copyFile(File source, File dest) throws IOException {
        if (dest.exists()) {
            IJ.log("Backup already exists - skipping backup");
            return;
        }
        Files.copy(source.toPath(), dest.toPath());
    }

    void processFile(File file) throws IOException {
        File output = new File(String.format("%s%s%s%s", FilenameUtils.getFullPath(file.getAbsolutePath()), File.separator, cleaned, FilenameUtils.getName(file.getAbsolutePath())));
        if (output.exists()) {
            return;
        }
        String data = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        List formattedData = reformatJSONData(data);
        Files.write(output.toPath(), formattedData, StandardOpenOption.CREATE);
    }

    private class MetaFileNameFilter implements IOFileFilter {

        public boolean accept(File file) {
            String filename = file.getName();
            return FilenameUtils.getExtension(filename).equalsIgnoreCase(ext)
                    && FilenameUtils.getBaseName(filename).endsWith(metadata)
                    && !FilenameUtils.getBaseName(filename).startsWith(cleaned);
        }

        public boolean accept(File file, String name) {
            return accept(new File(String.format("%s%s%s", file.getAbsolutePath(), File.separator, name)));
        }
    }

    private class ImageFileNameFilter implements IOFileFilter {

        public boolean accept(File file) {
            String filename = file.getName();
            return FilenameUtils.getExtension(filename).equalsIgnoreCase(tif) || FilenameUtils.getExtension(filename).equalsIgnoreCase(tiff);
        }

        public boolean accept(File file, String name) {
            return accept(new File(String.format("%s%s%s", file.getAbsolutePath(), File.separator, name)));
        }
    }
}
