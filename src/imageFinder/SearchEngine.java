package imageFinder;

import imageFinder.analyzeStrategy.JDCStrategy.JCDStrategy;
import imageFinder.analyzeStrategy.JDCStrategy.CEDD.CEDDAnalizeStrategy;
import imageFinder.analyzeStrategy.JDCStrategy.FCTH.FCTHAnalizeStrategy;
import imageFinder.util.ImageFinder;
import imageFinder.util.IndexGenerator;
import imageFinder.util.MinHeap;
import imageFinder.util.MinHeap.HeapEntry;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.opencsv.CSVWriter;

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
    
    public void searchImage(int topN, File file, CSVWriter csvWriter) throws Exception{
        
        BufferedImage targetImage = ImageIO.read(file);
        
        for(ImageFinder finder : finders){
            
            String strategyName = finder.getStrategyName();
            finder.setIndexData(groupIndexData.get(strategyName));
            finder.doFind(this, targetImage, topN);
            
        }
        
        writeResult(csvWriter, file.getName());
//        check(csvWriter, file.getName());
        
    }
    
    public void check(CSVWriter csvWriter, String imageName) throws IOException{
        
        /**
         * for test
         */
        
        Map<String, File> fileMap = test_imageOutPut();
           
        HeapEntry[] resultsEntries = weightCalculate();
        
        int i = 0;
        for(HeapEntry entry : resultsEntries){
            File file = fileMap.get(entry.fileName);
            try {
                i++;
                BufferedImage image = ImageIO.read(file);
                ImageIO.write(image, "jpg", new File("F:\\BigData\\contest_data\\shoes\\result\\shoes_" + i +".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(entry.fileName+"  "+entry.similarity);
        }
               
        
    }
        
  
    
    private void writeResult(CSVWriter csvWriter, String imageName) throws IOException{
       
        String[] csvFormat = new String[2];
        
        HeapEntry[] resultsEntries = weightCalculate();
        
        
        for(HeapEntry entry : resultsEntries){
            csvFormat[0] = imageName;
            csvFormat[1] = entry.fileName;
            csvWriter.writeNext(csvFormat);
            csvWriter.flush();
        }
        
    }
    
    private HeapEntry[] weightCalculate(){
        
        int topN = 0;
        Map<String, Double> map = new HashMap<String, Double>();
        
        for(ImageFinder finder : finders){
            String strategyName = finder.getStrategyName();
            HeapEntry[] resultsEntries = groupHeapEntries.get(strategyName);
            topN = resultsEntries.length;
            for(HeapEntry entry : resultsEntries){
                if(map.containsKey(entry.fileName)){
                    double value = map.get(entry.fileName);
                    value += entry.similarity;
                    map.put(entry.fileName, value);
                    continue;
                }
                map.put(entry.fileName, entry.similarity);
            }
        }
        
        MinHeap heap = new MinHeap(topN);
        
        for(Entry<String, Double> entry : map.entrySet()){
            heap.addToHeap(entry.getKey(), entry.getValue());
        }
        
        return heap.returnResult();
        
    }
    
    private Map<String, File> test_imageOutPut(){
        
        Map<String, File> fileMap = new HashMap<>(imageFiles.length);
        
        for(File file : imageFiles){
            fileMap.put(file.getName(), file);
        }
        
        return fileMap;
    }



    

}
