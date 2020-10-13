package net.calm.calm.FRAP;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.TypeConverter;
import java.awt.Rectangle;
import java.io.File;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFileChooser;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author David Barry <david.barry at cancer.org.uk>
 */
public class FRAP_Analyser implements PlugIn {

    private static File directory;
    private final int HEADER_SIZE = 9;
//    private final int FRAMES = 20;

//    public static void main(String args[]) {
//        (new FRAP_Analyser()).run(null);
//        System.exit(0);
//    }

    public FRAP_Analyser() {

    }

    public void run(String arg) {
        ImageStack stack;
        ImagePlus imp = WindowManager.getCurrentImage();
        if (imp != null) {
            stack = imp.getImageStack();
        } else {
            stack = IJ.openImage().getImageStack();
        }
        ImageStack output = new ImageStack(stack.getWidth(), stack.getHeight());
        for (int i = 1; i <= stack.getSize(); i++) {
            ByteProcessor outslice = (ByteProcessor) (new TypeConverter(stack.getProcessor(i), true)).convertToByte().duplicate();
            output.addSlice(outslice);
        }
        JFileChooser chooser = new JFileChooser(directory);
        chooser.setDialogTitle("Select coordinate file...");
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        directory = file.getAbsoluteFile();
        String charset = null;
        List<String> lines;
        try {
            lines = FileUtils.readLines(file, charset);
        } catch (Exception e) {
            IJ.error("Cannot open file.");
            return;
        }
        int n = lines.size();
        double data[][] = new double[n - HEADER_SIZE][stack.getSize()];
        for (int i = HEADER_SIZE; i < n; i++) {
            int params[] = new int[15];
            int pcount = 0;
            Scanner scanner = new Scanner(lines.get(i)).useDelimiter("[\\s(),]");
            while (scanner.hasNext()) {
                if (scanner.hasNextInt()) {
                    params[pcount] = scanner.nextInt();
                    pcount++;
                } else {
                    scanner.next();
                }
            }
            Rectangle rect = new Rectangle(params[6], params[7], params[8], params[9]);
            for (int s = params[0]; s < stack.getSize(); s++) {
                ImageProcessor ip = stack.getProcessor(s + 1);
                ip.setRoi(rect);
                ImageProcessor ip2 = ip.crop();
                data[i - HEADER_SIZE][s - params[0]] = ip2.getStatistics().mean;
                ByteProcessor outslice = (ByteProcessor) output.getProcessor(s + 1);
                outslice.setValue(255.0);
                outslice.draw(new Roi(rect));
                outslice.drawString(String.valueOf(i - HEADER_SIZE),
                        rect.x + rect.width, rect.y + rect.height);
            }
        }
        ResultsTable rt = new ResultsTable();

        for (int y = 0; y < stack.getSize(); y++) {
            rt.incrementCounter();
            for (int x = 0; x < n - HEADER_SIZE; x++) {
                rt.addValue(x, data[x][y]);
            }
        }
        rt.show("FRAP Analysis");
        (new ImagePlus("FRAP Analysis Output", output)).show();
    }
}
