/*
 * Copyright (C) 2018 David Barry <david.barry at crick dot ac dot uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.calm.calm.GIANI_;

import ij.plugin.PlugIn;
import net.calm.giani.ui.GIANIUI;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class GIANI_ implements PlugIn {

    public GIANI_() {

    }

    public void run(String arg) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GIANIUI().setVisible(true);
            }
        });
    }

}
