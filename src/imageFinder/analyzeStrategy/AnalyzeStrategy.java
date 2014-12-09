package imageFinder.analyzeStrategy;

import java.awt.image.BufferedImage;


/**
 * 2014年12月5日
 * @author decaywood
 *
 */
public interface AnalyzeStrategy{
    
    public StrategyType getStrategyType();
    
    public void analyzeImage(BufferedImage image);
    
    public double CalculateSimilarity(AnalyzeStrategy compareData);
    
    public double[] getImageKeyInfo();
    
    public void setImageKeyInfo(double[] imageKeyInfo);
    
}
