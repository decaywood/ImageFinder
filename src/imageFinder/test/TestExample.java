package imageFinder.test;

import imageFinder.analyzeStrategy.AnalyzeStrategy;
import imageFinder.analyzeStrategy.CEDDStrategy.CEDDAnalizeStrategy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 2014年12月7日
 * @author decaywood
 *
 */
public class TestExample {
    
    public static void main(String[] args) {
        
        try {
            File file1 = new java.io.File("F:\\照片\\mm\\6.JPG");
            BufferedImage image1;
            image1 = ImageIO.read(file1);
            AnalyzeStrategy strategy1 = new CEDDAnalizeStrategy();
            strategy1.analyzeImage(image1);
            
            File file2 = new java.io.File("F:\\照片\\mm\\7.JPG");
            BufferedImage image2;
            image2 = ImageIO.read(file2);
            AnalyzeStrategy strategy2 = new CEDDAnalizeStrategy();
            strategy2.analyzeImage(image2);
            
            File file3 = new java.io.File("F:\\照片\\mm\\8.JPG");
            BufferedImage image3;
            image3 = ImageIO.read(file3);
            AnalyzeStrategy strategy3 = new CEDDAnalizeStrategy();
            strategy3.analyzeImage(image3);
            
            File file4 = new java.io.File("F:\\照片\\mm\\77.JPG");
            BufferedImage image4;
            image4 = ImageIO.read(file4);
            AnalyzeStrategy strategy4 = new CEDDAnalizeStrategy();
            strategy4.analyzeImage(image4);
            
            System.out.println(strategy1.CalculateSimilarity(strategy2));
            System.out.println(strategy1.CalculateSimilarity(strategy3));
            System.out.println(strategy1.CalculateSimilarity(strategy4));
             
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
    }

}
