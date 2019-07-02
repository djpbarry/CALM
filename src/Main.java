
import CALM_.GIANI_.Giani_Macro_Extensions;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Main {

    public static void main(String[] args) {
//        (new GIANI_()).run(null);
        Giani_Macro_Extensions exec = new Giani_Macro_Extensions();
        exec.handleExtension("initialise", new String[]{
            "D:\\debugging\\giani_debug\\subset\\GIANI v2.042_Output\\properties.xml",
            "D:\\debugging\\giani_debug\\subset"
        });
        exec.handleExtension("runGiani", null);

//        SkeletonPruner sp = new SkeletonPruner(100, (ByteProcessor) (IJ.openImage()).getProcessor().convertToByteProcessor());
//
//        IJ.saveAs(new ImagePlus("Pruned", sp.getPrunedImage()), "PNG", "D:\\OneDrive - The Francis Crick Institute\\Working Data\\Tapon\\Maxine\\Tiffs\\Pruned");
//        System.exit(0);
//        (new Trajectory_Analyser()).run(null);
//        RiemannianDistanceTransform rdt = new RiemannianDistanceTransform();
//        ImageStack greyImage = IJ.openImage().getImageStack();
//        ImageStack maskImage = IJ.openImage().getImageStack();
//        IJ.saveAs((rdt.run(new ImageFloat(greyImage), new ImageShort(maskImage), 2.0f, 0.0962002f, 1.3832000f)).getImagePlus(), "TIF", "D:\\debugging\\giani_debug\\rdt.tif");
        System.exit(0);
    }

//    public static void main(String[] args) {
//        (new Skeleton_Branch_Finder()).run(null);
//        try{
////            ImagePlus[] imp = BF.openImagePlus("D:\\debugging\\mm\\Untitled_2\\Cleaned_Untitled_2_MMStack_Pos0_metadata.txt");
//        } catch (Exception e){
//            GenUtils.logError(e, "");
//        }
////        System.exit(0);
//    }
}
