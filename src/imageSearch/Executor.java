package imageSearch;

import imageSearch.strategy.GrayHashConvertStrategy;
import imageSearch.strategy.HashTransStrategy;
import imageSearch.strategy.NearestNeighbor;
import imageSearch.strategy.PHashConvertStrategy;
import imageSearch.strategy.ShrinkStrategy;
import imageSearch.util.HashConverter;
import imageSearch.util.ImageReader;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.sun.image.codec.jpeg.ImageFormatException;

/**
 * 2014年12月4日
 * @author decaywood
 *
 */
public class Executor {
    
    private static String toBinary(long n, int target) {
        StringBuffer stringBuffer = new StringBuffer();
        while (n != 0) {
            stringBuffer.append(n % target);
            n = n / target;
        }
        return stringBuffer.reverse().toString();
    }
    
    private static int hamingDistance(long hashcode1, long hashcode2){
        int count = 0;
        int bit = 0;
        while(hashcode1 != 0 && hashcode2 != 0){
            if(hashcode1 % 2 != hashcode2 % 2)
                count++;
            hashcode1 /= 2;
            hashcode2 /= 2;
            bit++;
        }
        return count + 63 - bit;
    }

    public static void executePerceptualHash() throws ImageFormatException, IOException{
        
        BufferedImage srcImage = ImageReader.readJPEGImage("F:\\照片\\mm\\1.jpg");
        BufferedImage srcImage2 = ImageReader.readJPEGImage("F:\\照片\\mm\\2.jpg");
        ShrinkStrategy shrinkStrategy = new NearestNeighbor(8, 8);
        HashTransStrategy hashTransStrategy = new GrayHashConvertStrategy(8, 8);
        long start = System.currentTimeMillis();
        long hashCode = HashConverter.Hashing(srcImage, shrinkStrategy, hashTransStrategy);
        long hashCode2 = HashConverter.Hashing(srcImage2, shrinkStrategy, hashTransStrategy);
        long end = System.currentTimeMillis();
        System.out.println((end - start) +" "+ toBinary(hashCode,2));
        System.out.println((end - start) +" "+ toBinary(hashCode2,2));
        System.out.println("hamingDistance: "+hamingDistance(hashCode, hashCode2));
    }
    
    public static void executepHash() throws ImageFormatException, IOException{
        
        BufferedImage srcImage = ImageReader.readJPEGImage("F:\\照片\\mm\\1.jpg");
        BufferedImage srcImage2 = ImageReader.readJPEGImage("F:\\照片\\mm\\2.jpg");
        ShrinkStrategy shrinkStrategy = new NearestNeighbor(32, 32);
        HashTransStrategy hashTransStrategy = new PHashConvertStrategy(32, 32);
        long start = System.currentTimeMillis();
        long hashCode = HashConverter.Hashing(srcImage, shrinkStrategy, hashTransStrategy);
        long hashCode2 = HashConverter.Hashing(srcImage2, shrinkStrategy, hashTransStrategy);
        long end = System.currentTimeMillis();
        System.out.println((end - start) +" "+ toBinary(hashCode,2));
        System.out.println((end - start) +" "+ toBinary(hashCode2,2));
        System.out.println("hamingDistance: "+hamingDistance(hashCode, hashCode2));
        
    }
    
    public static void main(String[] args) throws ImageFormatException, IOException {
        executePerceptualHash();
        System.out.println("=====================================================================================");
         executepHash();
    }
}
