package imageFinder.util;

import imageFinder.SearchEngine;
import imageFinder.analyzeStrategy.AnalyzeStrategy;
import imageFinder.util.MinHeap.HeapEntry;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 2014年12月6日
 * @author decaywood
 *
 */
public class ImageFinder{
    
    /**
     * 仅作为判断文件夹是否存在,减少新建File对象开支
     *  
     */
    private static Map<String, String> folderPath = new HashMap<String, String>();
    
  
    public static void addTofolderPath(String key, String value){
        
        folderPath.put(key, value);
        
    }
    
    private Map<String, double[]> indexData;
    
    private AnalyzeStrategy toolStrategy;
    
    private AnalyzeStrategy targetStrategy;
    
    
    
    public ImageFinder(AnalyzeStrategy toolStrategy, AnalyzeStrategy targetStrategy) {
        
        this.toolStrategy = toolStrategy;
        
        this.targetStrategy = targetStrategy;
        
    }
    
    
    
    public void setIndexData(Map<String, double[]> indexData) {
        this.indexData = indexData;
    }
    
    
    public String getStrategyName() {
        return toolStrategy.getStrategyType().strategyName();
    }
    
 
    
    private MinHeap.HeapEntry[] findSimilarImage(AnalyzeStrategy strategy, int topN){
        
        MinHeap minHeap = new MinHeap(topN);
        
        for(Entry<String, double[]> entry : indexData.entrySet()){
           
            String fileName = entry.getKey();
            double[] compareData = entry.getValue();
            if(compareData.length < 144){
                System.out.println(fileName);
                continue;
            }
            this.toolStrategy.setImageKeyInfo(compareData);
            double similarity = this.toolStrategy.CalculateSimilarity(strategy);
            minHeap.addToHeap(fileName, similarity);
        }
        
        return minHeap.returnResult();
    }
    
    
    private BufferedImage shrinkImage(BufferedImage sourceImage){
        int srcWidth = sourceImage.getWidth();
        int srcHeight = sourceImage.getHeight();
        
        int shrinkWidth = 150;
        double ratio = srcHeight * 1D / srcWidth;
        int shrinkHeight = (int) (shrinkWidth * ratio);
        
        int[] scalePointsX = new int[shrinkWidth];
        int[] scalePointsY = new int[shrinkHeight];
        
        if(shrinkWidth > srcWidth || shrinkHeight > srcHeight){ return sourceImage; }
        
        float scaleWidth = (float)srcWidth / shrinkWidth;
        float scaleHeight = (float)srcHeight / shrinkHeight;
        
        
        for(int index = 0; index < scalePointsX.length; index++) 
            scalePointsX[index] = (int) (index * scaleWidth);
         
        for(int index = 0; index < scalePointsY.length; index++)
            scalePointsY[index] = (int) (index * scaleHeight);
        
        BufferedImage destinationImage = new BufferedImage(shrinkWidth, shrinkHeight, sourceImage.getType());
        
        for(int i = 0; i < scalePointsX.length; i++){
            int indexX = scalePointsX[i];
            for(int j = 0; j < scalePointsY.length; j++){
                int indexY = scalePointsY[j];
                int RGB = sourceImage.getRGB(indexX, indexY);
                destinationImage.setRGB(i, j, RGB);
            }
        }
        return destinationImage;
    }
    

   /**
    * 
    * 2014年12月11日
    * @author decaywood
    *
    */
    public void doFind(SearchEngine engine, BufferedImage targetImage, int topN) {
        
        this.targetStrategy.analyzeImage(targetImage);
        HeapEntry[] results = findSimilarImage(targetStrategy, topN);
        engine.setHeapEntries(toolStrategy.getStrategyType().strategyName(), results);
        
    }

}
