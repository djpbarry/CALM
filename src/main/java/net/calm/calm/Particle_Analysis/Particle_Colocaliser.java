/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.calm.calm.Particle_Analysis;

import ij.plugin.PlugIn;
import net.calm.virustracker.ParticleAnalysis.ParticleColocaliser;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Particle_Colocaliser implements PlugIn {

    public Particle_Colocaliser() {

    }

    public void run(String arg) {
        (new ParticleColocaliser()).run(arg);
    }
}
