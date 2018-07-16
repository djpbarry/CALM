/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.ROIFitter;

import ij.plugin.PlugIn;
import ui.PlateFitterUI;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Plate_Analyser implements PlugIn {

    public static void main(String args[]) {
        (new Plate_Analyser()).run(null);
    }

    public void run(String args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PlateFitterUI().setVisible(true);
            }
        });

//        ImagePlus imp = IJ.openImage();
//        PlateFitter fitter = new PlateFitter(imp.getProcessor(), 2, 3, 86, 30, 20, 10);
//        fitter.doFit();
//        double[] p = fitter.getParams();
//        imp.setOverlay(fitter.getPlateTemplate().drawOverlay(p[0], p[1], p[2]));
//        IJ.saveAs(imp, "TIF", "D:\\OneDrive - The Francis Crick Institute\\Working Data\\Sahai\\Karin\\overlay");
//        System.out.println(String.format("X: %f, Y: %f, Theta: %f, Corr: %f", p[0], p[1], p[2], p[3]));
    }
}
