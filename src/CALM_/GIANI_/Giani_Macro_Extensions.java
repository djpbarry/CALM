/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.GIANI_;

import GIANI.PipelineExecutor;
import GIANI.PipelineBuilder;
import IO.PropertyWriter;
import Process.ProcessPipeline;
import UtilClasses.GenUtils;
import gianiparams.GianiDefaultParams;
import ij.IJ;
import ij.macro.Functions;
import java.io.File;
import java.util.Properties;
import loci.plugins.macro.MacroFunctions;
import mcib3d.geom.Objects3DPopulation;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Giani_Macro_Extensions extends MacroFunctions {

    Properties props = new GianiDefaultParams();
    private PipelineExecutor exec;

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

    public void initialise(String propertyFileLocation, String inputDirectory) {
        Objects3DPopulation cells = new Objects3DPopulation();
        ProcessPipeline pipeline = PipelineBuilder.buildFullPipeline(props, cells);
        props = new GianiDefaultParams();
        try {
            PropertyWriter.loadProperties(props, null, new File(propertyFileLocation));
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to load properties file.");
        }
        props.setProperty(GianiDefaultParams.INPUT_DIR_LABEL, inputDirectory);
        if (!GianiDefaultParams.setOutputDirectory(props, null)) {
            return;
        }
        exec = new PipelineExecutor(pipeline, props);
    }

    public void runGiani() {
        exec.run();
    }

    public void setProperty(String property, String value) {
        props.setProperty(property, value);
    }
}
