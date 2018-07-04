/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM.ROIFitter;

import IO.DataWriter;
import IO.File.ImageFilter;
import Math.Optimisation.Plate;
import Math.Optimisation.PlateFitter;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.RoiScaler;
import ij.plugin.filter.Analyzer;
import ij.process.ImageProcessor;
import java.awt.Rectangle;
import java.io.File;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import ui.PlateFitterUI;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class AnalysePlate {

    int rows;
    int cols;
    double wellRad;
    double xBuff;
    double yBuff;
    double interWellSpacing;
    double wellFraction;
    double spatRes;
    double shrinkFactor;
    File inputDirectory, outputDirectory;

    public AnalysePlate(Properties props, File inputDirectory, File outputDirectory) {
        this.spatRes = Double.parseDouble(props.getProperty(PlateFitterUI.SPAT_RES));
        this.rows = Integer.parseInt(props.getProperty(PlateFitterUI.N_ROWS));
        this.cols = Integer.parseInt(props.getProperty(PlateFitterUI.N_COLS));
        this.wellRad = Integer.parseInt(props.getProperty(PlateFitterUI.WELL_RAD)) / spatRes;
        this.xBuff = Double.parseDouble(props.getProperty(PlateFitterUI.X_BUFF)) / spatRes;
        this.yBuff = Double.parseDouble(props.getProperty(PlateFitterUI.Y_BUFF)) / spatRes;
        this.wellFraction = Double.parseDouble(props.getProperty(PlateFitterUI.WELL_FRACTION));
        this.interWellSpacing = Double.parseDouble(props.getProperty(PlateFitterUI.WELL_SPACING)) / spatRes;
        this.shrinkFactor = Double.parseDouble(props.getProperty(PlateFitterUI.SHRINK_FACTOR));
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
    }

    public void analyse() {
        String fileList[] = inputDirectory.list(new ImageFilter(new String[]{"tif", "tiff", "png"}));
        IJ.log(inputDirectory.getAbsolutePath());
        ResultsTable rt = new ResultsTable();
        int nProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println(String.format("%d processsors available", nProcessors));
        try {
            ExecutorService exec = Executors.newFixedThreadPool(nProcessors);
            for (String fileName : fileList) {
                exec.submit(new AnalyseOnePlate(fileName, rt));
            }
            exec.shutdown();
            exec.awaitTermination(12, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            System.out.println(String.format("An exception occured - aborting: %s", e.toString()));
            return;
        }

        try {
            DataWriter.saveResultsTable(rt, new File(String.format("%s%s%s", outputDirectory.getAbsolutePath(), File.separator, "Results.csv")));
        } catch (Exception e) {
            IJ.log("Could not save results file.");
            IJ.log(e.toString());
            e.printStackTrace();
        }
    }

    private class AnalyseOnePlate implements Runnable {

        private final String fileName;
        private final ResultsTable rt;

        public AnalyseOnePlate(String fileName, ResultsTable rt) {
            this.fileName = fileName;
            this.rt = rt;
        }

        public void run() {
            ImagePlus imp = IJ.openImage(String.format("%s%s%s", inputDirectory.getAbsolutePath(), File.separator, fileName));
            ImageProcessor output = imp.duplicate().getProcessor();
            output.setValue(0);
            output.setLineWidth(3);
            ImageProcessor ip = imp.getProcessor().resize((int) Math.round(imp.getWidth() * shrinkFactor));
            PlateFitter fitter = new PlateFitter(ip, rows, cols, wellRad * shrinkFactor, xBuff * shrinkFactor, yBuff * shrinkFactor, interWellSpacing * shrinkFactor, wellFraction);
            fitter.doFit();
            double[] p = fitter.getParams();
            System.out.println(String.format("%s - X: %f, Y: %f, Theta: %f, Corr: %f", fileName, p[0] * spatRes / shrinkFactor, p[1] * spatRes / shrinkFactor, p[2], p[3]));
            LinkedList<Roi> rois = fitter.getPlateTemplate().drawRoi(p[0], p[1], p[2]);
            int nRois = rois.size();
            int count = 1;
            for (int i = nRois - 1; i >= 0; i--) {
                if (rois.get(i).getProperty(Plate.PLATE_COMPONENT).contentEquals(Plate.SHRUNK_WELL)) {
                    Roi well = RoiScaler.scale(rois.get(i), 1.0 / shrinkFactor, 1.0 / shrinkFactor, false);
                    imp.setRoi(well);
                    output.draw(well);
                    Rectangle wellBounds = well.getBounds();
                    int x0 = wellBounds.x + wellBounds.height / 2;
                    int y0 = wellBounds.y + wellBounds.width / 2;
                    output.drawString(String.valueOf(count++), x0, y0);
                    (new Analyzer(imp, Measurements.MEAN + Measurements.LABELS, rt)).measure();
                    rt.addValue("Well Index", (count - 1));
                }
            }
            IJ.saveAs(new ImagePlus("", output), "PNG", String.format("%s%s%s%s%s", outputDirectory.getAbsolutePath(), File.separator, "Result_", imp.getTitle(), ".png"));
        }
    }
}
