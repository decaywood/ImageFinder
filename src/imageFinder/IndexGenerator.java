package imageFinder;

import imageFinder.analyzeStrategy.AnalyzeStrategy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * 2014年12月5日
 * @author decaywood
 *
 */
public class IndexGenerator implements Runnable{
    
    private String indexDataSavePath;
    
    /**
     * 仅作为判断文件夹是否存在的记录减少判断以及创建File实例的性能消耗
     * add操作必须保证线程安全！but,无需对整个list方法进行线程安全封装
     */
    private static List<String> folderExist = new ArrayList<String>();
    
    /**
     * 
     *  写操作！
     *  冲突仅发生在每种策略对应文件夹还未生成时，synchronize关键字对性能影响不大！
     *
     */
    private static synchronized void addToFolderExist(String item){ folderExist.add(item); }
    
    public void setIndexDataPath(String path){ indexDataSavePath = path; }
    
    private SearchEngine engine;
    
    private int shrinkWidth;
    private int shrinkHeight;
    
    int[] scalePointsX;
    int[] scalePointsY;
    
    private boolean shrink;
    
    private Map<String, double[]> indexData;
    
    private boolean persistenceIndex;
    
    private File[] imageFiles;
    
    protected AnalyzeStrategy strategy;
    
    public IndexGenerator(AnalyzeStrategy strategy) {
        
        this(strategy, false);
        
    }
    
    public IndexGenerator(AnalyzeStrategy strategy, boolean persistenceIndex) {
        
        this(strategy, persistenceIndex, 8);
        
    }
    
    public IndexGenerator(AnalyzeStrategy strategy, boolean persistenceIndex,  int bits) {
        
        this(strategy,persistenceIndex, false, bits, 0, 0);
        
    }
    
    public IndexGenerator(AnalyzeStrategy strategy, boolean persistenceIndex, boolean shrink, int bits,  int shrinkWidth, int shrinkHeight) {
        
        this.strategy = strategy;
        
        this.shrinkWidth = shrinkWidth;
        this.shrinkHeight = shrinkHeight;
        
        this.shrink = shrink;
        
        if(shrink){
            this.scalePointsX = new int[shrinkWidth];
            this.scalePointsY = new int[shrinkHeight];
        }
        
        this.indexData = new HashMap<String, double[]>(1 << bits);
        
        this.persistenceIndex = persistenceIndex;
        
    }
    
    
    public void setEngine(SearchEngine engine) {
        this.engine = engine;
    }
    
    public void setImageFiles(File[] imageFiles) {
        this.imageFiles = imageFiles;
    }
    
    
    protected void generateIndexData(File file) throws Exception{
        
        String fileName = file.getName();
        String strategyName = this.strategy.getStrategyType().strategyName();
        File indexFile = new File(indexDataSavePath + "\\" + strategyName + "\\" + fileName);
        
        if(indexFile.exists()){ 
            System.out.println("loading file :" + fileName);
            loadIndexData(indexFile); 
            return;
        }
        
        BufferedImage sourceImage =  ImageIO.read(file);
       
        System.out.println(Thread.currentThread()+" "+strategyName+" processing file :" + fileName);
        generateIndexData(sourceImage, fileName);
        
    }
    
    private void generateIndexData(BufferedImage sourceImage, String fileName){
        
        double[] imageKeyInfo;
        
        if(this.shrink){
            BufferedImage shrinkedImage = shrinkImage(sourceImage);
            this.strategy.analyzeImage(shrinkedImage);
        }
        this.strategy.analyzeImage(sourceImage);
        imageKeyInfo = this.strategy.getImageKeyInfo();
        if(persistenceIndex)
            saveIndexData(imageKeyInfo, fileName);
        indexData.put(fileName, imageKeyInfo);
        
    }
    
   
   
    
    private BufferedImage shrinkImage(BufferedImage sourceImage){
        int srcWidth = sourceImage.getWidth();
        int srcHeight = sourceImage.getHeight();
        
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
    
  
    private void saveIndexData(double[] data, String imageName){
        
        String strategyName = this.strategy.getStrategyType().strategyName();
        
        if(!folderExist.contains(strategyName)){
            File file = new File(indexDataSavePath + "\\" + strategyName);
            if(!file.exists() || !file.isDirectory()){  
                file.mkdir();   
                /**
                 * 线程安全写操作
                 */
                addToFolderExist(strategyName);
            }
        }
     
        File file = new File(indexDataSavePath + "\\" + strategyName + "\\" + imageName);
        
        byte[] target = doubleArrToByteArr(data);
        int byteArrLength = target.length;
        
        try {
            
            FileOutputStream fos = new FileOutputStream(file);
            byte[] arrLength = new byte[4];
            
            arrLength[0] = (byte)(byteArrLength >>> 0);  
            arrLength[1] = (byte)(byteArrLength >>> 8);  
            arrLength[2] = (byte)(byteArrLength >>> 16);  
            arrLength[3] = (byte)(byteArrLength >>> 24);  
            
            fos.write(arrLength); // 数组长度
            fos.write(target); //数据
            
            fos.flush();
            fos.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private byte[] doubleArrToByteArr(double[] target){
        
        byte[] result = new byte[target.length << 3];
        
        for(int index = 0; index < target.length; index++){
            
            double item = target[index];
            long key = Double.doubleToRawLongBits(item);  
            int offset = index << 3;
            
            result[offset + 0] = (byte)(key >>> 0);  
            result[offset + 1] = (byte)(key >>> 8);  
            result[offset + 2] = (byte)(key >>> 16);  
            result[offset + 3] = (byte)(key >>> 24);  
            result[offset + 4] = (byte)(key >>> 32);  
            result[offset + 5] = (byte)(key >>> 40);  
            result[offset + 6] = (byte)(key >>> 48);  
            result[offset + 7] = (byte)(key >>> 56);  
            
        }
        
        return result;
        
    }
    
    
    
    /**
     * 可以保证程序崩溃后仍然能够读取持久化在磁盘的索引数据,
     * 无需重新生成索引。
     */
    public void loadIndexData(File indexFile) throws Exception{
        
        FileInputStream fis = new FileInputStream(indexFile);
        
        byte[] intByte = new byte[4];
        fis.read(intByte);
        
        int byteArrLength = 0;
        
        byteArrLength |= ((int)intByte[0] & 0xFF) << 0;
        byteArrLength |= ((int)intByte[1] & 0xFF) << 8;
        byteArrLength |= ((int)intByte[2] & 0xFF) << 16;
        byteArrLength |= ((int)intByte[3] & 0xFF) << 24;
        
        byte[] buffer = new byte[byteArrLength]; 
            
        fis.read(buffer);
        
        double[] imageKeyInfo = ByteArrToDoubleArr(buffer);

        this.indexData.put(indexFile.getName(), imageKeyInfo);
        fis.close();
        
    }
    
    
    private double[] ByteArrToDoubleArr(byte[] target){
        
        double[] result = new double[target.length >> 3];
        
        for(int index = 0; index < result.length; index++){
            
            int offset = index << 3;
            long key = 0;
            
            key |= ((long)target[offset + 0] & 0xFFL) << 0;
            key |= ((long)target[offset + 1] & 0xFFL) << 8;
            key |= ((long)target[offset + 2] & 0xFFL) << 16;
            key |= ((long)target[offset + 3] & 0xFFL) << 24;
            key |= ((long)target[offset + 4] & 0xFFL) << 32;
            key |= ((long)target[offset + 5] & 0xFFL) << 40;
            key |= ((long)target[offset + 6] & 0xFFL) << 48;
            key |= ((long)target[offset + 7] & 0xFFL) << 56;
            
            result[index] = Double.longBitsToDouble(key);;
        }
        
        return result;
        
    }

    /**
     * 2014年12月7日
     * @author decaywood
     *
     */ 
    @Override
    public void run() {
        for(File targetFile : imageFiles){
            try {
                
                generateIndexData(targetFile);
                engine.setIndexData(strategy.getStrategyType().strategyName(), indexData);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
}
