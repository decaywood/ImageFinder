package imageFinder;

import imageFinder.MinHeap.HeapEntry;
import imageFinder.analyzeStrategy.AnalyzeStrategy;
import imageFinder.analyzeStrategy.JDCStrategy.JCDStrategy;
import imageFinder.analyzeStrategy.JDCStrategy.CEDD.CEDDAnalizeStrategy;
import imageFinder.analyzeStrategy.JDCStrategy.FCTH.FCTHAnalizeStrategy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 2014年12月6日
 * @author decaywood
 *
 */
public class SearchEngine {
    
    private List<IndexGenerator> generators;
    
    private List<ImageFinder> finders;
    
    private ExecutorService threadPool;
    
    private File[] imageFiles;
    
    private Map<String, Map<String, double[]>> groupIndexData;
    private Map<String, HeapEntry[]> groupHeapEntries;
    
    /**
     * @param file 图片库存储路径
     */
    public SearchEngine(File file) {
        
        imageFiles = file.listFiles();
        this.generators = new ArrayList<IndexGenerator>();
        this.finders = new ArrayList<ImageFinder>();
        
//        this.generators.add(new IndexGenerator(new CEDDAnalizeStrategy(), true, 6));
//        this.generators.add(new IndexGenerator(new FCTHAnalizeStrategy(), true, 6));
        this.generators.add(new IndexGenerator(new JCDStrategy(), true, 6));
        
//        this.finders.add(new ImageFinder(new CEDDAnalizeStrategy(), new CEDDAnalizeStrategy(), 6));
//        this.finders.add(new ImageFinder(new FCTHAnalizeStrategy(), new FCTHAnalizeStrategy(), 6));
        this.finders.add(new ImageFinder(new JCDStrategy(), new JCDStrategy(), 6));
        
        this.threadPool = Executors.newFixedThreadPool(generators.size());
        
        this.groupIndexData = new HashMap<String, Map<String, double[]>>(generators.size());
        this.groupHeapEntries = new HashMap<String, HeapEntry[]>(finders.size());
    }
    
    
    /**
     * 写操作 需要同步
     */
    public synchronized void setIndexData(String strategyName, Map<String, double[]> indexData){
        groupIndexData.put(strategyName, indexData);
    }
    
    /**
     * 写操作 需要同步
     */
    public synchronized void setHeapEntries(String strategyName, HeapEntry[] entries) {
        this.groupHeapEntries.put(strategyName, entries);
    }
    
    public void generateIndex(String outputPath){
       
       for(IndexGenerator generator : generators){
           generator.setEngine(this); // 用于回调操作，将索引写入searchEngine
           generator.setIndexDataPath(outputPath);
           generator.setImageFiles(imageFiles);
       }
       
       for(IndexGenerator generator : generators){
           threadPool.execute(generator);
       }
       
    }
    
    public void searchImage(int topN, BufferedImage targetImage) throws Exception{
        
        
        for(ImageFinder finder : finders){
            
            String strategyName = finder.getStrategyName();
            finder.setIndexData(groupIndexData.get(strategyName));
            finder.setEngine(this);
            finder.setTopN(topN);
            finder.initTargetStrategy(targetImage);
            
        }
        
        for(ImageFinder finder : finders){
            threadPool.execute(finder);
        }
        
    }
    
    public synchronized void check(){
        
        boolean done = groupHeapEntries.size() == finders.size();
        
        /**
         * for test
         */
        if(done){
            for(ImageFinder finder : finders){
                String strategyName = finder.getStrategyName();
                HeapEntry[] resultsEntries = groupHeapEntries.get(strategyName);
                for(HeapEntry entry : resultsEntries)
                    System.out.println(strategyName+"  "+entry.fileName+"  "+entry.similarity);
            }
            threadPool.shutdown();
        }
        
    }
    

}
