/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.Particle_Analysis;

import ParticleAnalysis.ParticleMapper;
import ij.plugin.PlugIn;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Particle_Mapper implements PlugIn {

    public Particle_Mapper() {

    }

    public void run(String arg) {
        (new ParticleMapper()).run(arg);
    }
}
