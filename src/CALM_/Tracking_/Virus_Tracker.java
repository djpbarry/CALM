/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.Tracking_;

import ij.plugin.PlugIn;
import ui.VirusTrackerUI;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Virus_Tracker implements PlugIn {
    
    

    public void run(String arg) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VirusTrackerUI().setVisible(true);
            }
        });
    }
}
