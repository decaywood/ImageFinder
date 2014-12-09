package imageFinder;

import imageFinder.MinHeap.HeapEntry;
import imageFinder.analyzeStrategy.AnalyzeStrategy;

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
public class ImageFinder implements Runnable{
    
    /**
     * 仅作为判断文件夹是否存在,减少新建File对象开支
     * add操作必须保证线程安全！but,无需对整个list方法进行线程安全封装
     */
    private static Map<String, String> folderPath = new HashMap<String, String>();
    
    /**
  
     *  写操作！
     *  冲突仅发生在每种策略对应文件夹还未生成时,synchronize关键字对性能影响不大！
     *
     */
    public static synchronized void addTofolderPath(String key, String value){
        
        folderPath.put(key, value);
        
    }
    
    
    /**
     * 不同策略共享参数
     */
    private int topN;
    
    private Map<String, double[]> indexData;
    
    public void setTopN(int topN){ this.topN = topN; }
    
    private AnalyzeStrategy toolStrategy;
    
    private SearchEngine engine;
    
    private AnalyzeStrategy targetStrategy;
    
    
    public ImageFinder(AnalyzeStrategy strategy, AnalyzeStrategy targetStrategy) {
        
        this(strategy, targetStrategy, 8);
        
    }
    
    
    public ImageFinder(AnalyzeStrategy toolStrategy, AnalyzeStrategy targetStrategy, int bits) {
        
        this.toolStrategy = toolStrategy;
        
        this.targetStrategy = targetStrategy;
        
        indexData = new HashMap<String, double[]>(1 << bits);
        
    }
    
    public void initTargetStrategy(BufferedImage targetImage) {
        this.targetStrategy.analyzeImage(targetImage);
    }
    
    public void setIndexData(Map<String, double[]> indexData) {
        this.indexData = indexData;
    }
    
    public void setEngine(SearchEngine engine) {
        this.engine = engine;
    }
    
    public String getStrategyName() {
        return toolStrategy.getStrategyType().strategyName();
    }
    
 
    
    private MinHeap.HeapEntry[] findSimilarImage(AnalyzeStrategy strategy){
        
        MinHeap minHeap = new MinHeap(topN);
        
        for(Entry<String, double[]> entry : indexData.entrySet()){
           
            String fileName = entry.getKey();
            double[] compareData = entry.getValue();
            
            this.toolStrategy.setImageKeyInfo(compareData);
            double similarity = this.toolStrategy.CalculateSimilarity(strategy);
            minHeap.addToHeap(fileName, similarity);
        }
        
        return minHeap.returnResult();
    }

    /**
     * 2014年12月7日
     * @author decaywood
     *
     */ 
    @Override
    public void run() {
        
        HeapEntry[] results = findSimilarImage(targetStrategy);
        engine.setHeapEntries(toolStrategy.getStrategyType().strategyName(), results);
        engine.check();
    }

}
