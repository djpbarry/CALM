/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.ROIFitter;

import Image.ImageNormaliser;
import Optimisation.RoiFitter;
import UtilClasses.GenUtils;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import java.awt.Color;

/**
 *
 * @author Dave Barry <david.barry at crick.ac.uk>
 */
public class ROI_Fitter implements PlugIn {

    private final String title = "ROI Fitter";
//    private static boolean ring = false;

    public static void main(String[] args) {
        (new ROI_Fitter()).run(null);
        System.exit(0);
    }

    public ROI_Fitter() {

    }

    public void run(String arg) {
        ImagePlus imp;
        if (IJ.getInstance() == null) {
            imp = IJ.openImage();
        } else {
            imp = WindowManager.getCurrentImage();
        }
        if (imp == null) {
            GenUtils.error("No Image Open.");
            return;
        }
//        if (!showDialog()) {
//            return;
//        }
        FloatProcessor ip = ImageNormaliser.normaliseImage(imp.getProcessor());
        int width = ip.getWidth();
        int height = ip.getHeight();
        float[] pix = (float[]) ip.getPixels();
        double[] xVals = new double[width];
        double[] yVals = new double[height];
        for (int x = 0; x < width; x++) {
            xVals[x] = x;
        }
        for (int y = 0; y < height; y++) {
            yVals[y] = y;
        }
        Roi roi = imp.getRoi();
        if (roi == null || !(roi instanceof OvalRoi)) {
            roi = new OvalRoi(0.0, 0.0, width, height);
        }
        imp.show();
        RoiFitter instance = new RoiFitter(xVals, yVals, pix, roi);
        instance.doFit();
        double[] p = instance.getParams();
        double rw = p[2] * 2.0;
        double x0 = p[0] - p[2];
        double y0 = p[1] - p[2];
        roi = new OvalRoi(x0, y0, rw, rw);
        imp.setProcessor(ip);
        imp.resetDisplayRange();
        imp.setRoi(roi);
        ByteProcessor mask = new ByteProcessor(imp.getWidth(), imp.getHeight());
        mask.setColor(Color.white);
        mask.fill();
        mask.setColor(Color.black);
        mask.fill(roi);
        (new ImagePlus("Mask Image", mask)).show();
        RoiManager roim = RoiManager.getRoiManager();
        roim.add(imp, roi, -1);
        IJ.showStatus(String.format("%s: Done", title));
    }

//    boolean showDialog() {
//        ROIFitterInterface ui = new ROIFitterInterface(IJ.getInstance(), true, ring);
//        ui.setVisible(true);
//        ring = ui.isRing();
//        return ui.isWasOked();
//    }
}
