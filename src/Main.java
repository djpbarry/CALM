
import CALM_.Filters.Skeleton_Branch_Finder;
import UtilClasses.GenUtils;

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
        (new Skeleton_Branch_Finder()).run(null);
        try{
//            ImagePlus[] imp = BF.openImagePlus("D:\\debugging\\mm\\Untitled_2\\Cleaned_Untitled_2_MMStack_Pos0_metadata.txt");
        } catch (Exception e){
            GenUtils.logError(e, "");
        }
        System.exit(0);
    }
}
