package imageSearch.strategy;

import java.awt.image.BufferedImage;

/**
 * 2014年12月4日
 * @author decaywood
 *
 */
public interface HashTransStrategy {

    public long convertToHash(BufferedImage sourceImage);
    
}