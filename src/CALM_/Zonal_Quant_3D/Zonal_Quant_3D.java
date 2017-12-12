/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.Zonal_Quant_3D;

import VoronoiQuant3D.Analyser;
import ij.IJ;
import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;

/**
 *
 * @author Dave Barry <david.barry at crick.ac.uk>
 */
public class Zonal_Quant_3D implements PlugIn {

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//        (new Voronoi_Quant3D()).run(null);
//        System.exit(0);
//    }

    public Zonal_Quant_3D() {

    }

    public void run(String arg) {
        ImagePlus voronoi = IJ.openImage((new OpenDialog("Specify Voronoi Image", null)).getPath());
        ImagePlus sig = IJ.openImage((new OpenDialog("Specify Signal Image", null)).getPath());
        (new Analyser()).analyse(voronoi, sig);
    }

}
