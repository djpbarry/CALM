/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.calm.calm.MicroManager;

import ij.IJ;
import ij.plugin.PlugIn;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import net.calm.iaclasslibrary.UtilClasses.Utilities;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class MM_MetaData_Reformatter implements PlugIn {

    private static File inputDir;
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
            COORDS = "Coords",
            CHAN_INDEX = "ChannelIndex",
            FRAME_KEY = "\"FrameKey";

    public MM_MetaData_Reformatter() {

    }

    public void run(String args) {
        try {
            inputDir = Utilities.getFolder(inputDir, "Specify input directory", true);
            IJ.log(String.format("Root Directory: %s\n", inputDir.getAbsolutePath()));
            Iterator<File> metaIter = FileUtils.iterateFiles(inputDir, new MetaFileNameFilter(), TrueFileFilter.INSTANCE);
            while (metaIter.hasNext()) {
                File file = metaIter.next();
                IJ.log(String.format("Processing %s", file.getAbsolutePath()));
                copyFile(file, new File(String.format("%s.backup", file.getAbsolutePath())));
                processFile(file);
                Files.delete(file.toPath());
            }
            Iterator<File> imageIter = FileUtils.iterateFiles(inputDir, new ImageFileNameFilter(), TrueFileFilter.INSTANCE);
            while (imageIter.hasNext()) {
                File file = imageIter.next();
                String newFileBaseName = renameFile(file.getName());
                if (newFileBaseName != null) {
                    File newFileName = new File(String.format("%s%s%s", file.getParent(), File.separator, renameFile(file.getName())));
                    IJ.log(String.format("Renaming %s as %s", file.getName(), newFileName.getName()));
                    file.renameTo(newFileName);
                }
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
            return filename;
        } else {
            return null;
        }
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

    private String reorderChannels(String jsonData)
            throws IOException {
        StringTokenizer st = new StringTokenizer(jsonData, "\n");
        ArrayList<ArrayList<Integer>> sliceCountsPerChannel = new ArrayList();
        int[] slice = new int[3];
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if (token.startsWith("\"FrameKey")) {
                int dash = token.indexOf('-') + 1;
                int nextDash = token.indexOf("-", dash);
                slice[2] = Integer.parseInt(token.substring(dash, nextDash));
                dash = nextDash + 1;
                nextDash = token.indexOf("-", dash);
                slice[1] = Integer.parseInt(token.substring(dash, nextDash));
                dash = nextDash + 1;
                slice[0] = Integer.parseInt(token.substring(dash,
                        token.indexOf("\"", dash)));
                while (sliceCountsPerChannel.size() <= slice[1]) {
                    sliceCountsPerChannel.add(new ArrayList());
                }
                if (!(sliceCountsPerChannel.get(slice[1])).contains(slice[0])) {
                    sliceCountsPerChannel.get(slice[1]).add(slice[0]);
                }
            }
        }
        int maxCount = -1;
        int maxSliceIndex = -1;
        for (int c = 0; c < sliceCountsPerChannel.size(); c++) {
            ArrayList<Integer> sliceCount = sliceCountsPerChannel.get(c);
            if (sliceCount.size() > maxCount) {
                maxCount = sliceCount.size();
                maxSliceIndex = c;
            }
        }
        LinkedHashMap<Integer, Integer> channelMap = new LinkedHashMap();
        for (int c = 0; c < sliceCountsPerChannel.size(); c++) {
            if (c == 0) {
                channelMap.put(0, maxSliceIndex);
            } else if (c == maxSliceIndex) {
                channelMap.put(maxSliceIndex, 0);
            } else {
                channelMap.put(c, c);
            }
        }
        st = new StringTokenizer(jsonData, "\n");
        StringBuilder output = new StringBuilder();
        while (st.hasMoreTokens()) {
            String line = st.nextToken();
            String token = line.trim();
            if (token.startsWith(FRAME_KEY)) {
                int dash = token.indexOf('-') + 1;
                int nextDash = token.indexOf("-", dash);
                slice[2] = Integer.parseInt(token.substring(dash, nextDash));
                dash = nextDash + 1;
                nextDash = token.indexOf("-", dash);
                slice[1] = Integer.parseInt(token.substring(dash, nextDash));
                dash = nextDash + 1;
                slice[0] = Integer.parseInt(token.substring(dash,
                        token.indexOf("\"", dash)));
                String frameLine = String.format("%s-%d-%d-%d\": {", FRAME_KEY, slice[2], channelMap.get(slice[1]), slice[0]);
                appendLineToOutput(output, String.format("%s\n", frameLine));
                line = st.nextToken();
                token = line.trim();
                String key = "";
                StringBuilder valueBuffer = new StringBuilder();
                boolean valueArray = false;
                int nestedCount = 0;
                while (!token.startsWith("}") || nestedCount > 0) {
                    if (token.trim().endsWith("{")) {
                        nestedCount++;
                        appendLineToOutput(output, line);
                        line = st.nextToken();
                        token = line.trim();
                        continue;
                    } else if (token.trim().startsWith("}")) {
                        nestedCount--;
                        appendLineToOutput(output, line);
                        line = st.nextToken();
                        token = line.trim();
                        continue;
                    }
                    if (valueArray || token.trim().equals("],")) {
                        if (token.trim().equals("],")) {
                            valueArray = false;
                        } else {
                            valueBuffer.append(token.trim().replaceAll("\"", ""));
                            appendLineToOutput(output, line);
                            line = st.nextToken();
                            token = line.trim();
                            continue;
                        }
                    } else {
                        int colon = token.indexOf(':');
                        key = token.substring(1, colon).trim();
                        valueBuffer.setLength(0);
                        valueBuffer.append(token.substring(colon + 1, token.length() - 1).trim().replaceAll("\"", ""));
                        key = key.replaceAll("\"", "");
                        if (token.trim().endsWith("[")) {
                            valueArray = true;
                            appendLineToOutput(output, line);
                            line = st.nextToken();
                            token = line.trim();
                            continue;
                        }
                    }
                    String value = valueBuffer.toString();
                    if (key.equals(CHAN_INDEX)) {
                        line = String.format("\"%s\": %d", CHAN_INDEX, channelMap.get(Integer.parseInt(value)));
                    } else {
                        line = token;
                    }
                    appendLineToOutput(output, line);
                    line = st.nextToken();
                    token = line.trim();
                }
            }
            output.append(String.format("%s\n", line));
        }
        return output.toString();
    }

    void appendLineToOutput(StringBuilder output, String line) {
        output.append(String.format("%s\n", line));
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
        List formattedData = reformatJSONData(reorderChannels(data));
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
