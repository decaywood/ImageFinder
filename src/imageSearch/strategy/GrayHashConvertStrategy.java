package imageSearch.strategy;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * 2014年12月4日
 * @author decaywood
 *
 */
public class GrayHashConvertStrategy extends AbstractHashTransStrategy{
    
   
    
    public GrayHashConvertStrategy() {
        this(8, 8);
    }
    
     
    
    public GrayHashConvertStrategy(int imageWidth, int imageHeight) {
       super(imageWidth, imageHeight);
    }

    /**
     * 2014年12月4日
     * @author decaywood
     *
     */ 
    @Override
    public long convertToHash(BufferedImage sourceImage) {
        
        fillGrays(sourceImage);
        int average = averageArray(grays);
        return generateHash(average);
        
    }
    
    private long generateHash(int average){
        long hash = 0;
        for(int item : grays){
            hash <<= 1;
            hash |= item > average ? 1 : 0;
        }
        hash = hash < 0 ? hash ^ 0x8000000000000000l : hash;
        return hash;
    }
    
    private int averageArray(int[] array){
        int sum = 0;
        for(int item : array){
            sum += item;
        }
        return sum / array.length; 
    }
    

}
