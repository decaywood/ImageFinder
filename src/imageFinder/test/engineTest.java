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
        
//        SearchEngine engine = new SearchEngine(new File("F:\\BigData\\contest_data\\clothes\\clothes_image"));
//        engine.generateIndex("F:\\forTest");
//        BufferedImage targetImage = ImageIO.read(new File("F:\\BigData\\contest_data\\clothes\\clothes_source\\clothes_250003.jpg"));
//        engine.searchImage(50, targetImage);
        
        SearchEngine engine = new SearchEngine(new File("F:\\BigData\\contest_data\\shoes\\shoes_image"));
        engine.generateIndex("F:\\forTest");
//        BufferedImage targetImage = ImageIO.read(new File("F:\\BigData\\contest_data\\shoes\\shoes_source\\shoes_250003.jpg"));
        BufferedImage targetImage = ImageIO.read(new File("F:\\BigData\\contest_data\\shoes\\shoes_source\\shoes_250001.jpg"));
        engine.searchImage(1000, targetImage);
    }

}
