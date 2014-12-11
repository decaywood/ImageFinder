package imageFinder;

import imageFinder.analyzeStrategy.JDCStrategy.JCDStrategy;
import imageFinder.analyzeStrategy.JDCStrategy.CEDD.CEDDAnalizeStrategy;
import imageFinder.analyzeStrategy.JDCStrategy.FCTH.FCTHAnalizeStrategy;
import imageFinder.util.ImageFinder;
import imageFinder.util.IndexGenerator;
import imageFinder.util.MinHeap.HeapEntry;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2014年12月6日
 * @author decaywood
 *
 */
public class SearchEngine {
    
    private List<ForkJoinIndexGenerator> generators;
    
    private List<ImageFinder> finders;
    
    private File[] imageFiles;
    
    private Map<String, Map<String, double[]>> groupIndexData;
    private Map<String, HeapEntry[]> groupHeapEntries;
    
    /**
     * @param file 图片库存储路径
     */
    public SearchEngine(File file) {
        
        imageFiles = file.listFiles();
        this.generators = new ArrayList<ForkJoinIndexGenerator>();
        this.finders = new ArrayList<ImageFinder>();
        

        this.generators.add(new ForkJoinIndexGenerator(new CEDDAnalizeStrategy(), true));
        this.generators.add(new ForkJoinIndexGenerator(new FCTHAnalizeStrategy(), true));
        this.generators.add(new ForkJoinIndexGenerator(new JCDStrategy(), true));
        
        this.finders.add(new ImageFinder(new CEDDAnalizeStrategy(), new CEDDAnalizeStrategy()));
        this.finders.add(new ImageFinder(new FCTHAnalizeStrategy(), new FCTHAnalizeStrategy()));
        this.finders.add(new ImageFinder(new JCDStrategy(), new JCDStrategy()));
        
        this.groupIndexData = new HashMap<String, Map<String, double[]>>(generators.size());
        this.groupHeapEntries = new HashMap<String, HeapEntry[]>(finders.size());
    }
    
    
   
    public void setIndexData(String strategyName, Map<String, double[]> indexData){
        groupIndexData.put(strategyName, indexData);
    }
    
   
    public void setHeapEntries(String strategyName, HeapEntry[] entries) {
        this.groupHeapEntries.put(strategyName, entries);
    }
    
    public void generateIndex(String outputPath){
       
       for(ForkJoinIndexGenerator generator : generators){
           generator.doSearch(this, outputPath, imageFiles); // 用于回调操作，将索引写入searchEngine
       }
       
    }
    
    public void searchImage(int topN, BufferedImage targetImage) throws Exception{
        
        
        for(ImageFinder finder : finders){
            
            String strategyName = finder.getStrategyName();
            finder.setIndexData(groupIndexData.get(strategyName));
            finder.doFind(this, targetImage, topN);
            
        }
        
    }
    
    public void check(){
        
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
        }
        
    }
    

}
