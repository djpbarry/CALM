/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CALM_.Filters;

import UtilClasses.GenUtils;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Skeleton_Branch_Finder implements PlugIn {

    private final int foreground = 0;
    private final int background = 255;

    public void run(String args) {
        ImagePlus imp = IJ.getImage();
        if (imp == null) {
            GenUtils.error("No images open.");
        }
        ImageStack stack = imp.getImageStack();
        int nSlices = stack.getSize();
        int width = stack.getWidth();
        int height = stack.getHeight();
        ImageStack output = new ImageStack(width, height);
        for (int n = 1; n <= nSlices; n++) {
            ImageProcessor ip = stack.getProcessor(n);
            ByteProcessor outSlice = new ByteProcessor(width, height);
            outSlice.setValue(background);
            outSlice.fill();
            outSlice.setValue(foreground);
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    if (ip.getPixelValue(x, y) == foreground
                            && countNeighbours(x, y, ip, foreground) > 2) {
                        outSlice.drawPixel(x, y);
                    }
                }
            }
            output.addSlice(outSlice);
        }
        (new ImagePlus("Branch-Points", output)).show();
    }

    private int countNeighbours(int x, int y, ImageProcessor processor, int foreground) {
        int count = 0;
        for (int j = y - 1; j <= y+ 1; j++) {
            for (int i = x - 1; i <= x+1; i++) {
                if ((processor.getPixelValue(i, j) == foreground)
                        && !(i == x && j == y)) {
                    count++;
                }
            }
        }
        return count;
    }

}
