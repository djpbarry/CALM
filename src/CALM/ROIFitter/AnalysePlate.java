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
import ij.plugin.filter.Analyzer;
import ij.process.ImageProcessor;
import java.awt.Rectangle;
import java.io.File;
import java.util.LinkedList;
import java.util.Properties;
import ui.PlateFitterUI;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class AnalysePlate {

    int rows;
    int cols;
    int wellRad;
    double xBuff;
    double yBuff;
    double interWellSpacing;
    double shrinkFactor;
    double spatRes;
    File inputDirectory, outputDirectory;

    public AnalysePlate(Properties props, File inputDirectory, File outputDirectory) {
        this.spatRes = Double.parseDouble(props.getProperty(PlateFitterUI.SPAT_RES));
        this.rows = Integer.parseInt(props.getProperty(PlateFitterUI.N_ROWS));
        this.cols = Integer.parseInt(props.getProperty(PlateFitterUI.N_COLS));
        this.wellRad = Integer.parseInt(props.getProperty(PlateFitterUI.WELL_RAD));
        this.xBuff = Double.parseDouble(props.getProperty(PlateFitterUI.X_BUFF));
        this.yBuff = Double.parseDouble(props.getProperty(PlateFitterUI.Y_BUFF));
        this.shrinkFactor = Double.parseDouble(props.getProperty(PlateFitterUI.WELL_FRACTION));
        this.interWellSpacing = Double.parseDouble(props.getProperty(PlateFitterUI.WELL_SPACING));
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
    }

    public void analyse() {
        String fileList[] = inputDirectory.list(new ImageFilter(new String[]{"tif", "tiff", "png"}));
        IJ.log(inputDirectory.getAbsolutePath());
        ResultsTable rt = new ResultsTable();
        for (String fileName : fileList) {
            IJ.log(String.format("Analysing %s...", fileName));
            ImagePlus imp = IJ.openImage(String.format("%s%s%s", inputDirectory.getAbsolutePath(), File.separator, fileName));
            ImageProcessor output = imp.duplicate().getProcessor();
            output.setValue(0);
            output.setLineWidth(3);
            PlateFitter fitter = new PlateFitter(imp.getProcessor(), rows, cols, wellRad, xBuff, yBuff, interWellSpacing, shrinkFactor);
            fitter.doFit();
            double[] p = fitter.getParams();
            System.out.println(String.format("X: %f, Y: %f, Theta: %f, Corr: %f", p[0], p[1], p[2], p[3]));
            LinkedList<Roi> rois = fitter.getPlateTemplate().drawRoi(p[0], p[1], p[2]);
            int nRois = rois.size();
            int count = 1;
            for (int i = nRois - 1; i >= 0; i--) {
                if (rois.get(i).getProperty(Plate.PLATE_COMPONENT).contentEquals(Plate.SHRUNK_WELL)) {
                    Roi well = rois.get(i);
                    imp.setRoi(well);
                    output.draw(well);
                    Rectangle wellBounds = well.getBounds();
                    int x0 = wellBounds.x + wellBounds.height / 2;
                    int y0 = wellBounds.y + wellBounds.width / 2;
                    output.drawString(String.valueOf(count++), x0, y0);
                    (new Analyzer(imp, Measurements.MEAN + Measurements.LABELS, rt)).measure();
                }
            }
            IJ.saveAs(new ImagePlus("", output), "PNG", String.format("%s%s%s%s%s", outputDirectory.getAbsolutePath(), File.separator, "Result_", imp.getTitle(), ".png"));
        }
        try {
            DataWriter.saveResultsTable(rt, new File(String.format("%s%s%s", outputDirectory.getAbsolutePath(), File.separator, "Results.csv")));
        } catch (Exception e) {
            IJ.log("Could not save results file.");
            IJ.log(e.toString());
            e.printStackTrace();
        }
    }
}
