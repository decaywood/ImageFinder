package imageFinder.analyzeStrategy.JDCStrategy.FCTH;

import imageFinder.analyzeStrategy.AnalyzeStrategy;
import imageFinder.analyzeStrategy.StrategyType;
import imageFinder.analyzeStrategy.JDCStrategy.Fuzzy10Bin;
import imageFinder.analyzeStrategy.JDCStrategy.Fuzzy24Bin;
import imageFinder.analyzeStrategy.JDCStrategy.RGBToHSVCoverter;
import imageFinder.analyzeStrategy.JDCStrategy.RGBToHSVCoverter.HSV;
import imageFinder.analyzeStrategy.JDCStrategy.CEDD.CEDDQuant;
import imageFinder.analyzeStrategy.JDCStrategy.FCTH.WaveletTransformer.WaveletMatrix;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * 2014年12月9日
 * @author decaywood
 * 
 * 
 * FCTH 特征可从 3 个模糊单元的组合求得结果。先将图像划分成若干分块，
 * 在第一个模糊单元中以 HSV 颜色空间的三个信道为输入，经模糊系统最终产生 10-bin 的直方图。
 * 在第二个模糊单元修改每个颜色的色调后，经模糊系统最终产生 24-bin 的直方图。
 * 以上两个模糊单元在颜色描述子的章节中已详细作了解释，
 * 且模糊颜色描述子与 CEDD中所提的颜色描述子是同一个概念，
 * 在此不再累述。在第三个模糊单元中，将图像分块经 Harr 小波变换成一组纹理元素，
 * 模糊系统以这些纹理元素集为输入将 24-bin 直方图转换成 192-bin 的直方图。
 * 最后是描述 FCTH 特征提取的实现。
 *
 */
public class FCTHAnalizeStrategy implements AnalyzeStrategy{
    
    
    private double[] histogram;
    
    private Fuzzy10Bin Fuzzy10bin;
    private Fuzzy24Bin Fuzzy24bin;
    private FuzzyFCTH FuccyFCTH;
   
    private double[][] imageGrid;
    private int[][] imageGridRed;
    private int[][] imageGridGreen;
    private int[][] imageGridBlue;
    
    
    private double[][] Block;
    private int[][] BlockR;
    private int[][] BlockG;
    private int[][] BlockB;
    private int[][] BlockCount;
    
    
    private int[] CororRed;
    private int[] CororGreen;
    private int[] CororBlue;

    private int[] CororRedTemp;
    private int[] CororGreenTemp;
    private int[] CororBlueTemp;
    
    private RGBToHSVCoverter HSVConverter;
    private WaveletTransformer transformer;
    
    private FCTHQuant Quant;
    
    public FCTHAnalizeStrategy() {
        this.histogram = new double[192];
        
        this.Block = new double[4][4];
        this.BlockR = new int[4][4];
        this.BlockG = new int[4][4];
        this.BlockB = new int[4][4];
        this.BlockCount = new int[4][4];
        
        this.Fuzzy10bin = new Fuzzy10Bin();
        this.Fuzzy24bin = new Fuzzy24Bin();
        this.FuccyFCTH = new FuzzyFCTH();
        
        this.HSVConverter = new RGBToHSVCoverter();
        this.transformer = new WaveletTransformer();
        
        this.Quant = new FCTHQuant();
    }
    

   
    @Override
    public StrategyType getStrategyType() {
        return StrategyType.FCTH;
    }

   
    @Override
    public void analyzeImage(BufferedImage image) {
        
     
        int width = image.getWidth();
        int height = image.getHeight();


        Arrays.fill(histogram, 0);

        image = convertTo8BitRGBImage(image);

        /**
         * 按RGB颜色分解图像
         */

        if(imageGrid == null || width > imageGrid.length){
            imageGrid = new double[width][height];
            imageGridRed = new int[width][height];
            imageGridGreen = new int[width][height];
            imageGridBlue = new int[width][height];
        }
     

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);
                imageGridRed[x][y] = (pixel >> 16) & 0xff;
                imageGridGreen[x][y] = (pixel >> 8) & 0xff;
                imageGridBlue[x][y] = (pixel) & 0xff;

                int mean = (int) (0.114 * imageGridBlue[x][y] + 0.587 * imageGridGreen[x][y] + 0.299 * imageGridRed[x][y]);
                imageGrid[x][y] = mean;
            }
        }

        /**
         * 先将图像分割成 1600 个分块。 
         */
        int NumberOfBlocks = 1600;
        int Step_X = (int) Math.floor(width / Math.sqrt(NumberOfBlocks));
        int Step_Y = (int) Math.floor(height / Math.sqrt(NumberOfBlocks));

        if ((Step_X % 2) != 0) {
            Step_X = Step_X - 1;
        }
        if ((Step_Y % 2) != 0) {
            Step_Y = Step_Y - 1;
        }


        if (Step_Y < 4) Step_Y = 4;
        if (Step_X < 4) Step_X = 4;
        ///
        // Filter

        if(CororRed == null || Step_Y * Step_X > CororRed.length){
            CororRed = new int[Step_Y * Step_X];
            CororGreen = new int[Step_Y * Step_X];
            CororBlue = new int[Step_Y * Step_X];

            CororRedTemp = new int[Step_Y * Step_X];
            CororGreenTemp = new int[Step_Y * Step_X];
            CororBlueTemp = new int[Step_Y * Step_X];
        }
       
        
        
        for (int y = 0; y < height - Step_Y; y += Step_Y) {
            for (int x = 0; x < width - Step_X; x += Step_X) {
                 
                int MeanRed = 0;
                int MeanGreen = 0;
                int MeanBlue = 0;
                int CurrentPixelX = 0;
                int CurrentPixelY = 0;
               

                int TempSum = 0;
                for (int i = 0; i < Step_X; i++) {
                    for (int j = 0; j < Step_Y; j++) {
                        CurrentPixelX = 0;
                        CurrentPixelY = 0;

                        if (i >= (Step_X / 4)) CurrentPixelX = 1;
                        if (i >= (Step_X / 2)) CurrentPixelX = 2;
                        if (i >= (3 * Step_X / 4)) CurrentPixelX = 3;

                        if (j >= (Step_Y / 4)) CurrentPixelY = 1;
                        if (j >= (Step_Y / 2)) CurrentPixelY = 2;
                        if (j >= (3 * Step_Y / 4)) CurrentPixelY = 3;

                        Block[CurrentPixelX][CurrentPixelY] += imageGrid[x + i][y + j];
                        BlockCount[CurrentPixelX][CurrentPixelY]++;

                        BlockR[CurrentPixelX][CurrentPixelY] = imageGridRed[x + i][y + j];
                        BlockG[CurrentPixelX][CurrentPixelY] = imageGridGreen[x + i][y + j];
                        BlockB[CurrentPixelX][CurrentPixelY] = imageGridBlue[x + i][y + j];

                        CororRed[TempSum] = BlockR[CurrentPixelX][CurrentPixelY];
                        CororGreen[TempSum] = BlockG[CurrentPixelX][CurrentPixelY];
                        CororBlue[TempSum] = BlockB[CurrentPixelX][CurrentPixelY];

                        CororRedTemp[TempSum] = BlockR[CurrentPixelX][CurrentPixelY];
                        CororGreenTemp[TempSum] = BlockG[CurrentPixelX][CurrentPixelY];
                        CororBlueTemp[TempSum] = BlockB[CurrentPixelX][CurrentPixelY];


                        TempSum++;
                    }
                }


                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        Block[i][j] = Block[i][j] / BlockCount[i][j];
                    }
                }

                /**
                 * harr小波变换
                 */
                WaveletMatrix matrix = transformer.waveletTransform(Block, 1);


                for (int i = 0; i < (Step_Y * Step_X); i++) {
                    MeanRed += CororRed[i];
                    MeanGreen += CororGreen[i];
                    MeanBlue += CororBlue[i];
                }

                MeanRed = (int) (MeanRed / (Step_Y * Step_X));
                MeanGreen = (int) (MeanGreen / (Step_Y * Step_X));
                MeanBlue = (int) (MeanBlue / (Step_Y * Step_X));

                HSV HSV;
                
                HSV = HSVConverter.convert(MeanRed, MeanGreen, MeanBlue);
                
                double[] Fuzzy10BinResult;
                double[] Fuzzy24BinResult;

                Fuzzy10BinResult = Fuzzy10bin.ApplyFilter(HSV.H, HSV.S, HSV.V);
                Fuzzy24BinResult = Fuzzy24bin.ApplyFilter(HSV.H, HSV.S, HSV.V, Fuzzy10BinResult);
                histogram = FuccyFCTH.ApplyFilter(matrix.F3, matrix.F2, matrix.F1, Fuzzy24BinResult, 24);


            }


        }

        // end of the filter
        double TotalSum = 0;

        for (int i = 0; i < 192; i++) {
            TotalSum += histogram[i];
        }

        for (int i = 0; i < 192; i++) {
            histogram[i] = histogram[i] / TotalSum;
        }

        Quant.calculate(histogram);

    }
    
    
    /**
     * 2014年12月9日
     * @author decaywood
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
    
    /**
     * 转换成HSV之前,先保证图像为 TYPE_INT_RGB
     */
    private BufferedImage convertTo8BitRGBImage(BufferedImage bufferedImage) {
         
        if (bufferedImage.getType() != ColorSpace.TYPE_RGB || bufferedImage.getSampleModel().getSampleSize(0) != 8) {
            BufferedImage img = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            img.getGraphics().drawImage(bufferedImage, 0, 0, null);
            bufferedImage = img;
        }
        return bufferedImage;
    }

}
