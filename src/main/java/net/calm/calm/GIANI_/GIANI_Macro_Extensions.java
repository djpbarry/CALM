package net.calm.calm.GIANI_;

import ij.IJ;
import ij.macro.Functions;
import ij.plugin.PlugIn;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.giani.macro.GIANIMacroExecutor;

public class GIANI_Macro_Extensions implements PlugIn {
    public void run(String args) {
        if (!IJ.macroRunning()) {
            IJ.error("Macro extensions are designed to be run from within a macro."
                    + "\n Instructions on how to do so will follow.");
            IJ.log(toString());
            return;
        } else {
            Functions.registerExtensions(new GIANIMacroExecutor());
        }
    }

    public String toString() {
        GianiDefaultParams props = new GianiDefaultParams();

        String propList = "";

        for (String p : props.stringPropertyNames()) {
            propList = propList + p + "\n";
        }

        return "To gain access to GIANI Macro extensions from within a macro, put\n"
                + " the following line at the beginning of your macro:\n"
                + "\n"
                + "run(\"GIANI Macro Extensions\");\n"
                + "\n"
                + "This will enable the following macro functions:\n"
                + "\n"
                + "Ext.loadPropertiesFile(fileLocation);\n"
                + "-- Loads the GIANI property file specified by fileLocation.\n"
                + "Ext.getPropertyList();\n"
                + "-- Returns a comma-separated list of all properties.\n"
                + "Ext.setProperty(property, newValue);\n"
                + "-- Updates the specified property with the specified newValue. See below for a list of all properties.\n"
                + "Ext.run();\n"
                + "-- Runs GIANI.\n"
                + "\n"
                + "The following are a list of properties that can be modified using the above commands:\n"
                + "\n"
                + propList;
    }
}
