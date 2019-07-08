/*
 * Copyright (C) 2019 David Barry <david.barry at crick dot ac dot uk>
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
package CALM_.GIANI_;

import IO.BioFormats.BioFormatsFileLister;
import IO.BioFormats.BioFormatsImg;
import UtilClasses.GenUtils;
import UtilClasses.GenVariables;
import UtilClasses.Utilities;
import ij.IJ;
import ij.plugin.PlugIn;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import loci.formats.FormatException;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Slurm_Script_Generator implements PlugIn {

    private static File input;
    private static String gianiJarLocation = "/home/camp/barryd/working/barryd/hpc/java/giani/GIANI_v2.042.jar";
    private static String propFileLocation = "/home/camp/barryd/working/barryd/hpc/giani_test/GIANI v2.042_Output/properties.xml";

    public Slurm_Script_Generator() {

    }

    public void run(String arg) {
        try {
            input = Utilities.getFolder(input, "Specify input directory", false);
            gianiJarLocation = Utilities.getFile(new File("gianiJarLocation"), "Specify location of GIANI Jar file", false).getAbsolutePath();
            propFileLocation = Utilities.getFile(new File("propFileLocation"), "Specify location of GIANI properties file", false).getAbsolutePath();
        } catch (InterruptedException | InvocationTargetException e) {
            return;
        }
        if (!input.isDirectory()) {
            IJ.log("Input is not a directory - aborting.");
            return;
        }
        File scriptFile = new File(String.format("%s%sGiani_Slurm_Script.sh", input, File.separator));
        Writer scriptWriter;
        try {
            if (scriptFile.exists()) {
                if (!scriptFile.delete()) {
                    IJ.log(String.format("Could not delete %s - aborting.", input.getAbsolutePath()));
                    return;
                }
            } else if (!scriptFile.createNewFile()) {
                IJ.log(String.format("Could not create %s - aborting.", input.getAbsolutePath()));
                return;
            }
            scriptWriter = new OutputStreamWriter(new FileOutputStream(scriptFile), GenVariables.ISO);

            scriptWriter.write("#!/bin/bash\n\n");
            scriptWriter.write("#SBATCH --job-name=fiji-giani\n");
            scriptWriter.write("#SBATCH --ntasks=1\n");
            scriptWriter.write("#SBATCH --time=15:00\n");
            scriptWriter.write("#SBATCH --mem-per-cpu=8G\n");
            scriptWriter.write("#SBATCH --cpus-per-task=96\n");
            scriptWriter.write("#SBATCH --partition=hmem\n");
            scriptWriter.write("#SBATCH --output=/home/camp/barryd/working/barryd/hpc/output/res.txt\n\n");
            scriptWriter.write("ml Java/1.8.0_202\n");

            ArrayList<String> files = BioFormatsFileLister.obtainValidFileList(input);
            for (String f : files) {
                BioFormatsImg img = new BioFormatsImg();
                try {
                    img.setId(String.format("%s%s%s", input, File.separator, f));
                } catch (IOException | FormatException e) {
                    GenUtils.logError(e, String.format("Failed to initialise %s", f));
                }
                int nSeries = img.getSeriesCount();
                for (int s = 0; s < nSeries; s++) {
                    scriptWriter.write(String.format("srun java -jar %s %s %s %d\n", gianiJarLocation, img.getId(), propFileLocation, s));
                }
            }
            scriptWriter.close();
        } catch (IOException e) {
            IJ.log(String.format("Encountered a problem generating %s - aborting.", input.getAbsolutePath()));
        }
        IJ.log("Done.");
    }
}
