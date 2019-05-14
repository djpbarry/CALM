/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.GIANI_;

import ResultsBrowser.GianiResultsBrowser;
import ij.plugin.PlugIn;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class GIANI_Results_Browser implements PlugIn {

    public GIANI_Results_Browser() {

    }

    public void run(String arg) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GianiResultsBrowser().setVisible(true);
            }
        });
    }
}
