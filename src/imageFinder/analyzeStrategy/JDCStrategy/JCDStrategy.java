package imageFinder.analyzeStrategy.JDCStrategy;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import imageFinder.analyzeStrategy.AnalyzeStrategy;
import imageFinder.analyzeStrategy.StrategyType;
import imageFinder.analyzeStrategy.JDCStrategy.CEDD.CEDDAnalizeStrategy;
import imageFinder.analyzeStrategy.JDCStrategy.FCTH.FCTHAnalizeStrategy;

/**
 * 2014年12月9日
 * @author decaywood
 *
 */
public class JCDStrategy implements AnalyzeStrategy{

    
    private double[] histogram;
    
    private AnalyzeStrategy CEDDStrategy;
    private AnalyzeStrategy FCTHStrategy;
    
    private double[] TempTable1;
    private double[] TempTable2;
    private double[] TempTable3;
    private double[] TempTable4;
    
    public JCDStrategy() {
        this.histogram = new double[168];
        this.CEDDStrategy = new CEDDAnalizeStrategy();
        this.FCTHStrategy = new FCTHAnalizeStrategy();
        
        this.TempTable1 = new double[24];
        this.TempTable2 = new double[24];
        this.TempTable3 = new double[24];
        this.TempTable4 = new double[24];
    }
    
    @Override
    public StrategyType getStrategyType() {
        return StrategyType.JCD;
    }
 
    
    
    @Override
    public void analyzeImage(BufferedImage image) {
        
       
        CEDDStrategy.analyzeImage(image);
        FCTHStrategy.analyzeImage(image);
        mergeHistograms(CEDDStrategy.getImageKeyInfo(), FCTHStrategy.getImageKeyInfo());
        
    }

    /**
     * 
     * tanimoto
     *
     */
    @Override
    public double CalculateSimilarity(AnalyzeStrategy compareData) {
        
        double[] compareHistogram = compareData.getImageKeyInfo();
      
        if (compareHistogram.length != histogram.length){
            try {
                throw new Exception("Image Info Is Not Matched!!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        double Result = 0;
        double Temp1 = 0;
        double Temp2 = 0;
        double iTemp1 = 0;
        double iTemp2 = 0;
        double TempCount1 = 0;
        double TempCount2 = 0;
        double TempCount3 = 0;
        
        for (int i = 0; i < compareHistogram.length; i++) {
            Temp1 += compareHistogram[i];
            Temp2 += histogram[i];
        }

        if (Temp1 == 0 && Temp2 == 0) return 100f;
        if (Temp1 == 0 || Temp2 == 0) return 0f;

        for (int i = 0; i < compareHistogram.length; i++) {
            iTemp1 = compareHistogram[i] / Temp1;
            iTemp2 = histogram[i] / Temp2;
            TempCount1 += iTemp1 * iTemp2;
            TempCount2 += iTemp2 * iTemp2;
            TempCount3 += iTemp1 * iTemp1;

        }

        Result = (100 * (TempCount1 / (TempCount2 + TempCount3 - TempCount1)));
        return Result;
    }

    
    @Override
    public double[] getImageKeyInfo() {
        return Arrays.copyOf(histogram, histogram.length);
    }

   
    @Override
    public void setImageKeyInfo(double[] imageKeyInfo) {
        this.histogram = imageKeyInfo;
    }
    
    private void mergeHistograms(double[] CEDD, double[] FCTH) {

       

        for (int i = 0; i < 24; i++) {
            TempTable1[i] = FCTH[0 + i] + FCTH[96 + i];
            TempTable2[i] = FCTH[24 + i] + FCTH[120 + i];
            TempTable3[i] = FCTH[48 + i] + FCTH[144 + i];
            TempTable4[i] = FCTH[72 + i] + FCTH[168 + i];

        }


        for (int i = 0; i < 24; i++) {
            histogram[i] = (TempTable1[i] + CEDD[i]) / 2;
            histogram[24 + i] = (TempTable2[i] + CEDD[48 + i]) / 2;
            histogram[48 + i] = CEDD[96 + i];
            histogram[72 + i] = (TempTable3[i] + CEDD[72 + i]) / 2;
            histogram[96 + i] = CEDD[120 + i];
            histogram[120 + i] = TempTable4[i];
            histogram[144 + i] = CEDD[24 + i];

        }


    }
    
    @Override
    public AnalyzeStrategy clone() throws CloneNotSupportedException {
        return new JCDStrategy();
    }

}
