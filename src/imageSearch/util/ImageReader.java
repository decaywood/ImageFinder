package imageSearch.util;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.ImageFormatException;


/**
 * 2014年12月4日
 * @author decaywood
 *
 */
public class ImageReader {

    public static BufferedImage readJPEGImage(String filename) throws ImageFormatException, IOException
    {
        BufferedImage sourceImage = ImageIO.read(new File(filename));
        return sourceImage;
    }
    
    public static void main(String[] args) throws ImageFormatException, IOException {
        
//        BufferedImage srcImage = readJPEGImage("F:\\照片\\风景成品\\IMG_4788.jpg");
//        long start = System.currentTimeMillis();
//        BufferedImage image = ImageShrinkTool.shrinkImage(srcImage,  new NearestNeighbor());
//        long end = System.currentTimeMillis();
//        System.out.println((end - start));
//        FileOutputStream stream = new FileOutputStream(new File("C:\\Users\\decaywood\\Desktop\\test.jpg"));
//        ImageIO.write(image, "jpg", stream);
       
    }
}

