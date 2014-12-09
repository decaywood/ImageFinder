package imageFinder.test;

import imageFinder.SearchEngine;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * 2014年12月8日
 * @author decaywood
 *
 */
public class engineTest {
    
    public static void main(String[] args) throws Exception {
        
        SearchEngine engine = new SearchEngine(new File("F:\\照片\\婚庆"));
        engine.generateIndex("F:\\forTest");
        BufferedImage targetImage = ImageIO.read(new File("F:\\照片\\婚庆\\IMGP0367.JPG"));
        engine.searchImage(10, targetImage);
    }

}
