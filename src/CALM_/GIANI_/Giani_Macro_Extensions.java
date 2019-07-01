/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.GIANI_;

import GIANI.LocalMapperExecutor;
import IO.PropertyWriter;
import Process.ProcessPipeline;
import UtilClasses.GenUtils;
import gianiparams.GianiDefaultParams;
import ij.IJ;
import ij.macro.Functions;
import java.io.File;
import java.util.Properties;
import loci.plugins.macro.MacroFunctions;
import ui.GIANIUI;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Giani_Macro_Extensions extends MacroFunctions {

    Properties props = new GianiDefaultParams();
    private LocalMapperExecutor exec;

    public void run(String args) {
        if (!IJ.macroRunning()) {
            IJ.error("Macro extensions are designed to be run from within a macro."
                    + "\n Instructions on how to do so will follow.");
            IJ.log(toString());
        } else {
            Functions.registerExtensions(this);
        }
    }

    public String toString() {
        return null;
    }

    public void initialise(String propertyFileLocation) {
        GIANIUI gui = new GIANIUI();
        ProcessPipeline pipeline = gui.buildPipeline();
        props = new GianiDefaultParams();
        try {
            PropertyWriter.loadProperties(props, null, new File(propertyFileLocation));
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to load AnaMorf properties file.");
        }
        gui.setOutputDirectory(props);
        exec = new LocalMapperExecutor(pipeline, props);
    }

    public void run() {
        exec.run();
    }

    public void setProperty(String property, String value) {
        props.setProperty(property, value);
    }
}
