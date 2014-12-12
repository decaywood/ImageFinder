package imageFinder.test;

import imageFinder.SearchEngine;

import java.io.File;
import java.util.Scanner;

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
        
        StringBuffer buffer = new StringBuffer("F:\\BigData\\contest_data\\shoes\\shoes_source\\shoes_25000");
        
        Scanner scanner = new Scanner(System.in);
        int i = 0;
        while (i != -1) {
            i = scanner.nextInt();
            if(i == -1)
                continue;
            buffer.append(i + ".jpg");
            File file = new File(buffer.toString());
            engine.searchImage(50, file, null);
            buffer = new StringBuffer("F:\\BigData\\contest_data\\shoes\\shoes_source\\shoes_25000");
            
        }
        
       
    }

}
