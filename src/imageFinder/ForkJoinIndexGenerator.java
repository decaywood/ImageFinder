package imageFinder;

import imageFinder.analyzeStrategy.AnalyzeStrategy;
import imageFinder.util.IndexGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * 2014年12月11日
 * @author decaywood
 *
 */
public class ForkJoinIndexGenerator{
    
    private static final int THRESHOLD = 30;
    
    private static ForkJoinPool forkJoinPool = new ForkJoinPool();
    
    
    private static class ForkJoinExecutor extends RecursiveAction{
        
         
        private static final long serialVersionUID = -7158832343152023906L;
        
        private IndexGenerator generator;
        private File[] files;
        private int start;
        private int end;
        private ForkJoinIndexGenerator mother;
        private String indexDataSavePath;
        
        public ForkJoinExecutor(IndexGenerator generator, 
                                ForkJoinIndexGenerator mother, 
                                File[] files, 
                                String indexDataSavePath,
                                int start,
                                int end) {
            
            try {
                /**
                 * 深度复制
                 */
                this.generator = generator.clone();
                this.mother = mother;
                this.files = files;
                this.indexDataSavePath = indexDataSavePath;
                this.start = start;
                this.end = end;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            
        }

      
        @Override
        protected void compute() {
            int length = end - start;
           
            if(length > THRESHOLD){
                int middle = (end + start) >> 1;
               
                invokeAll(new ForkJoinExecutor(generator, mother, files, indexDataSavePath,  start, middle), 
                          new ForkJoinExecutor(generator, mother, files, indexDataSavePath,  middle, end));
            }
            else {
                this.generator.doSearch(indexDataSavePath, files, mother, start, end);
                this.generator = null;
            }
            
        }
        
    }
    
    private IndexGenerator generator;
    
    private Map<String, double[]> indexData;
    
 
    /**
     * 
     * 2014年12月11日
     * @author decaywood
     *  
     *  存在线程竞争
     *
     */
    public synchronized void setIndexData(String fileName, double[] indexData) {
        this.indexData.put(fileName, indexData);
    }
    
    
    public ForkJoinIndexGenerator(AnalyzeStrategy strategy) {
        
        this(strategy, false);
        
    }
    
    
    public ForkJoinIndexGenerator(AnalyzeStrategy strategy, boolean persistenceIndex) {
        
        this(strategy,persistenceIndex, false, 0, 0);
        
    }
    
    public ForkJoinIndexGenerator(AnalyzeStrategy strategy, boolean persistenceIndex, boolean shrink, int shrinkWidth, int shrinkHeight) {
        
        this.generator = new IndexGenerator(strategy, persistenceIndex, shrink, shrinkWidth, shrinkHeight);
        
    }
    

     
    /**
     * 
     * 2014年12月11日
     * @author decaywood
     *
     */
    public void doSearch(SearchEngine engine, String indexDataSavePath, File[] imageFiles) {
        
        if(this.indexData == null)
            this.indexData = new HashMap<String, double[]>(imageFiles.length);
            
        forkJoinPool.invoke(new ForkJoinExecutor(generator, this, imageFiles, indexDataSavePath, 0, imageFiles.length));
        
        engine.setIndexData(this.generator.getStrategyType(), indexData);
        
    }
    

}
