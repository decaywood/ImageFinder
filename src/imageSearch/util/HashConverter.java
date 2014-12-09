package imageSearch.util;

import imageSearch.strategy.HashTransStrategy;
import imageSearch.strategy.ShrinkStrategy;

import java.awt.image.BufferedImage;

/**
 * 2014年12月4日
 * @author decaywood
 *
 */
public class HashConverter {

    public static long Hashing(BufferedImage srcImage,ShrinkStrategy shrinkStrategy, HashTransStrategy hashStrategy){
        BufferedImage bufferedImage = ImageShrinkTool.shrinkImage(srcImage, shrinkStrategy);
        return hashStrategy.convertToHash(bufferedImage);
    }
}
