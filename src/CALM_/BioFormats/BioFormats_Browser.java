/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.BioFormats;

import ij.plugin.PlugIn;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class BioFormats_Browser implements PlugIn {

    public BioFormats_Browser() {

    }

    public void run(String arg) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BioFormatsBrowserFrame().setVisible(true);
            }
        });
    }
}
