package imageSearch.strategy;

import java.awt.image.BufferedImage;

/**
 * 2014年12月4日
 * @author decaywood
 *
 */
public abstract class AbstractHashTransStrategy implements HashTransStrategy{
    
    protected int[] grays;
    protected int imageWidth;
    protected int imageHeight;
    
    protected StringBuffer stringBuffer;
    
    public AbstractHashTransStrategy(int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        grays = new int[imageHeight * imageWidth];
        stringBuffer = new StringBuffer(64);
    }
    
    
    protected void fillGrays(BufferedImage sourceImage){
        
        for(int indexX = 0; indexX < imageWidth; indexX++){
            for(int indexY = 0; indexY < imageHeight; indexY++){
                int RGB = sourceImage.getRGB(indexX, indexY);
                grays[indexX * imageHeight + indexY] = transfer(RGB);
            }
        }
    }
    
    
    
    private int transfer(int RGB){
        int R = (RGB & 0xFF0000)>>16;
        int G = (RGB & 0xFF00)>>8;
        int B = RGB & 0xFF;
        int gray = (R * 38 + G * 75 + B * 15) >> 7;
        return (gray << 16) | (gray << 8) | gray;
    }
    

}
