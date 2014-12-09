package imageSearch.util;

import imageSearch.strategy.ShrinkStrategy;

import java.awt.image.BufferedImage;

/**
 * 2014年12月4日
 * @author decaywood
 *
 */
public class ImageShrinkTool {
    
     

    public static BufferedImage shrinkImage(BufferedImage bufferedImage, ShrinkStrategy strategy){
        return strategy.shrink(bufferedImage);
    }
    
}
