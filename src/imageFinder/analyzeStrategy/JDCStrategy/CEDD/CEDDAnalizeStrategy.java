package imageFinder.analyzeStrategy.JDCStrategy.CEDD;

import imageFinder.analyzeStrategy.AnalyzeStrategy;
import imageFinder.analyzeStrategy.StrategyType;
import imageFinder.analyzeStrategy.JDCStrategy.Fuzzy10Bin;
import imageFinder.analyzeStrategy.JDCStrategy.Fuzzy24Bin;
import imageFinder.analyzeStrategy.JDCStrategy.RGBToHSVCoverter;
import imageFinder.analyzeStrategy.JDCStrategy.RGBToHSVCoverter.HSV;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.util.Arrays;


/**
 * 2014年12月6日
 * @author decaywood
 * 
 * CEDD的英文全称是Color and Edge Directivity Descriptor,即颜色和边缘方向特征描述符。
 * 它结合了图像的颜色和纹理信息,生成一个144位的直方图。
 * 这个特征提取方法可以分为两个子模块系统,提取颜色信息的是颜色模块,提取纹理信息的是纹理模块,
 * CEDD直方图信息由六个区域组成,也就是纹理模块,六个区域就是提取出的6维向量直方图,
 * 然后在这些纹理信息的每一维中再加入颜色模块提取出的24维颜色信息,
 * 这样就可以将颜色和纹理有效结合起来,最终得出6*24=144维的直方图信息。
 *  
 * 在实现过程中先将图片分成若干小区,小区的数量是根据图像具体情况和计算机能力综合决定的,
 * 每一个图像小区都会经过纹理模块和颜色模块的处理。小区在纹理模块特征提取过程中会先分为4个子小区。
 * 根据YIQ计算公式得出每个像素的灰度值,求出每个子小区的平均灰度值。再经过5个数字滤波器过滤后,
 * 根据图3-10的原理判断该子小区属于哪些纹理信息类别。在颜色模块中,每个图像小区都会转换为HSV色彩空间,
 * 系统会将小区内HSV各通道的平均值通过10-bins模糊过滤器输出的10维向量再通过24-bins模糊过滤器中。
 * 通过10-bins模糊过滤器后根据H值得出了 10个色彩类别,
 * 当通过24-bins模糊过滤器时会根据S和V的区域判定对H进行再分类输出24维的直方图。
 * 图像的每一个小区都会经过颜色模块的处理,处理后将24个数据分别加入到该小区所属的各纹理类别中,
 * 最后对直方图进行归一化处理。
 *
 */
public class CEDDAnalizeStrategy implements AnalyzeStrategy {
    
    /**
     * 检验是否含有边缘信息的阈值:
     * T0=14,检验该小区是否含有边缘信息；
     * T1=0.68,判断该小区是否含有无方向信息；
     * T2=T3=0.98,用来判断该小区是否含有其它四个方向的信息。
     */

    private double T0;
    private double T1;
    private double T2;
    private double T3;
    
    private Fuzzy10Bin fuzzy10;
    private Fuzzy24Bin fuzzy24;
    private int[] edges;
    private double[][] pixelCount;
    private CEDDQuant ceddQuant;
    
    private double[] histogram;
    
    private int[][] imageGridRed;
    private int[][] imageGridGreen;
    private int[][] imageGridBlue;

    private double[][] grayscale;

    private int[] CororRed;
    private int[] CororGreen;
    private int[] CororBlue;
    
    public CEDDAnalizeStrategy() {
        this(14d, 0.68d, 0.98d, 0.98d);
    }
    
    
    public CEDDAnalizeStrategy(double Th0, double Th1, double Th2, double Th3) {
        this.T0 = Th0;
        this.T1 = Th1;
        this.T2 = Th2;
        this.T3 = Th3;
      
        this.fuzzy10 = new Fuzzy10Bin();
        this.fuzzy24 = new Fuzzy24Bin();
        this.histogram = new double[144];
        this.pixelCount = new double[2][2];
        this.edges = new int[6];
        this.ceddQuant = new CEDDQuant();
      
    }
  
    @Override
    public StrategyType getStrategyType() {
        return StrategyType.CEDD;
    }

    
    @Override
    public void analyzeImage(BufferedImage image) {
        
        /**
         * 转换成HSV之前,先保证图像为 TYPE_INT_RGB
         */
        image= convertTo8BitRGBImage(image);
       

      
        RGBToHSVCoverter HSVConverter = new RGBToHSVCoverter();
        

        int width = image.getWidth();
        int height = image.getHeight();
        
        if(imageGridRed == null || width > imageGridRed.length || height > imageGridRed[0].length){
            imageGridRed = new int[width][height];
            imageGridGreen = new int[width][height];
            imageGridBlue = new int[width][height];
            grayscale = new double[width][height];
        }
        

 
        /**
         * 根据图片大小调整分区
         */
        int NumberOfBlocks = -1;

        if (Math.min(width, height) >= 80) NumberOfBlocks = 1600;
        if (Math.min(width, height) < 80 && Math.min(width, height) >= 40) NumberOfBlocks = 400;
        if (Math.min(width, height) < 40) NumberOfBlocks = -1;


        int Step_X = 2;
        int Step_Y = 2;

        /**
         * 调整步进
         */
        if (NumberOfBlocks > 0)
        {
            Step_X =  (int)Math.floor(width / Math.sqrt(NumberOfBlocks));
            Step_Y = (int)Math.floor(height / Math.sqrt(NumberOfBlocks));

            if ((Step_X % 2) != 0)
            {
                Step_X = Step_X - 1;
            }
            if ((Step_Y % 2) != 0)
            {
                Step_Y = Step_Y - 1;
            }


        }

     

        Arrays.fill(histogram, 0);
        
        /**  
        *  CEDD特征中纹理信息的提取
        *  YIQ彩色空间：
        *  
        *  YIQ色彩空间属于NTSC (国际电视标准委员会)系统。
        *  Y(Luminace)代表了颜色的明视度,直观点说就是图像的灰度值。
        *  I和Q (Chrominace)代表了色调信息,它们分别描述图像色彩以及饱和度的属性。
        *  在YIQ色彩空间模型中,Y分量表示图像的亮度信息,I和Q分量表示颜色信息,
        *  I分量是指从橙色到青色,Q分量则是指从紫色到黄绿色[24]。
        *  通过对彩色图像从RGB到YIQ空间的转换,可以分开彩色图像中的亮度信息与色度信息,
        *  并对其各自进行独立处理。RGB转换到YIQ空间模型的对应关系如下面方程所示：
        *  
        *  Y = 0.299 * R + 0.587 * G + 0.114 * B
        */
        
        int pixel;
        
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixel = image.getRGB(x, y);
                imageGridRed[x][y] = (pixel >> 16) & 0xff;
                imageGridGreen[x][y] = (pixel >> 8) & 0xff;
                imageGridBlue[x][y] = (pixel) & 0xff;
                grayscale[x][y] = (0.299f * ((pixel >> 16) & 0xff) +  0.587f * ((pixel >> 8) & 0xff) + 0.114f * ((pixel) & 0xff)  );

            }
        }

        if(CororRed == null || Step_Y * Step_X > CororRed.length){
            CororRed = new int[Step_Y * Step_X];
            CororGreen = new int[Step_Y * Step_X];
            CororBlue = new int[Step_Y * Step_X];
        }
        

        int meanRed, meanGreen, meanBlue;


        int tempSum = 0;
        double max = 0;

        int temoMAX_X = Step_X * (int)Math.floor(image.getWidth() >> 1);
        int temoMAX_Y = Step_Y * (int)Math.floor(image.getHeight() >> 1);

        if (NumberOfBlocks > 0)
        {
            temoMAX_X = Step_X * (int)Math.sqrt(NumberOfBlocks);
            temoMAX_Y = Step_Y * (int)Math.sqrt(NumberOfBlocks);
        }
        
        
      

       
        
     

        /**
         * 纹理信息的提取:
         * 
         * 边缘方向直方图
         * 在这里将提出一种计算速度较快捷的纹理信息提取方法,
         * EHD( Edge Histogram Descriptor),
         * 即边缘直方图描述符,将会用到5个数字滤波器.
         * 
         * 这五个数字滤波器是用来提取纹理边缘信息的,
         * 它们能够将其所作用的区域分为:
         * 垂直方向、
         * 水平方向、
         * 45度方向、
         * 135度方向和无方向五个类别。
         * 
         */
      

        for (int y = 0; y < temoMAX_Y; y += Step_Y) {
            for (int x = 0; x < temoMAX_X; x += Step_X) {

                /** 
                 *  在对图像进行纹理信息提取时会将图像分为若干小区。
                 *  然后每个小区再分为四个大小相等的子小区。                  
                 *   _________
                 *  | 1  | 2  |
                 *  |____|____|
                 *  | 3  | 4  |
                 *  |____|____|
                 *  
                 *  用Area1,Area2,Area3,Area4分别表示在第(x,y)个小区内四个子小区的平均灰度值。
                 *  
                 */
                double Area1 = 0;
                double Area2 = 0;
                double Area3 = 0;
                double Area4 = 0;
                
                double nVertical;
                double nHorizon;
                double nDirect45;
                double nDirect135;
                double nDirectNone;
                
                meanRed = 0;
                meanGreen = 0;
                meanBlue = 0;
                edges[0] = -1;
                edges[1] = -1;
                edges[2] = -1;
                edges[3] = -1;
                edges[4] = -1;
                edges[5] = -1;

                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        pixelCount[i][j] = 0;
                    }
                }

                tempSum = 0;

                for (int i = y; i < y + Step_Y; i++) {
                    for (int j = x; j < x + Step_X; j++) {

                        CororRed[tempSum] = imageGridRed[j][i];
                        CororGreen[tempSum] = imageGridGreen[j][i];
                        CororBlue[tempSum] = imageGridBlue[j][i];
 

                        tempSum++;

                        if (j < (x + Step_X / 2) && i < (y + Step_Y / 2)) Area1 += (grayscale[j][i]);
                        if (j >= (x + Step_X / 2) && i < (y + Step_Y / 2)) Area2 += (grayscale[j][i]);
                        if (j < (x + Step_X / 2) && i >= (y + Step_Y / 2)) Area3 += (grayscale[j][i]);
                        if (j >= (x + Step_X / 2) && i >= (y + Step_Y / 2)) Area4 += (grayscale[j][i]);

                    }
                }
                
                /**
                 * 求平均灰度
                 */

                Area1 = (int)(Area1 * (4.0 / (Step_X * Step_Y)));

                Area2 = (int)(Area2 * (4.0 / (Step_X * Step_Y)));

                Area3 = (int)(Area3 * (4.0 / (Step_X * Step_Y)));

                Area4 = (int)(Area4 * (4.0 / (Step_X * Step_Y)));

                /**
                 * 滤波：
                 * 
                 * nVertical:       nHorizon:       nDirect45:      nDirect135:     nDirectNone:
                 *   _________       _________       _________       _________       _________
                 *  | 1  |-1  |     | 1  | 1  |     |1.41| 0  |     | 0  |1.41|     | 2  |-2  | 
                 *  |____|____|     |____|____|     |____|____|     |____|____|     |____|____|  
                 *  | 1  |-1  |     |-1  |-1  |     | 0  |1.41|     |1.41| 0  |     |-2  | 2  | 
                 *  |____|____|     |____|____|     |____|____|     |____|____|     |____|____|
                 *  
                 */

                nDirectNone = Math.abs(Area1 * 2 + Area2 * -2 + Area3 * -2 + Area4 * 2);
                nHorizon = Math.abs(Area1 * 1 + Area2 * 1 + Area3 * -1 + Area4 * -1);
                nVertical = Math.abs(Area1 * 1 + Area2 * -1 + Area3 * 1 + Area4 * -1);
                nDirect45 = Math.abs(Area1 * Math.sqrt(2) + Area2 * 0 + Area3 * 0 + Area4 * -Math.sqrt(2));
                nDirect135 = Math.abs(Area1 * 0 + Area2 * Math.sqrt(2) + Area3 * -Math.sqrt(2) + Area4 * 0);

                max = Math.max(nDirectNone, Math.max(nHorizon, Math.max(nVertical, Math.max(nDirect45, nDirect135))));

                /**
                 * 规范化：
                 */

                nDirectNone = nDirectNone / max;
                nHorizon = nHorizon / max;
                nVertical = nVertical / max;
                nDirect45 = nDirect45 / max;
                nDirect135 = nDirect135 / max;
                
                /**
                 * 通过上面的计算公式,可以得出每个小区内图像边缘的信息。
                 * CEDD中纹理信息提取的是一个6维直方图,直方图中各维信息的含义分别是：
                 * (0)无边缘信息,
                 * (1)无方向的边缘信息,
                 * (2)水平方向的边缘信息,
                 * (3)垂直方向的边缘信息,
                 * (4) 45度方向的边缘信息,
                 * (5) 135度方向的边缘信息。
                 * 
                 * 如果max大于T0,则该小区含有纹理信息,如果不大于则是非含有纹理信息的小区,
                 * 那么6维直方图第一维的值会加1。如果该区域是有边缘信息的,即max大于等于T0,
                 * 便可以计算其它各方向信息的值,可以想象一个发散的五边形,
                 * 每个顶点代表一个边缘方向类别,每个小区内计算出的
                 * nDirectNone、nHorizon、nVertical、nDirect45、nDirect135值便分别落在五个点与中心原点的连线上。
                 * 中心点的值为1,五边形边界线上的值为0。如果n值大于它相应边缘方向类别上的阈值,
                 * 则可判定该小区属于这个边缘方向类别,可想而知,一个小区可以同时属于几个类别。
                 * 由此,便有如下划分方法：若nDirectNone大于T1,则直方图中含有无方向信息的区域值加1;
                 * 若nHorizon大于T2,则直方图中含有水平方向边缘信息的区域值加1;若nVertical大于T2,
                 * 则直方图中含有垂直方向边缘信息的区域值加1;若nDirect45大于T3,
                 * 则直方图中含有45度方向边缘信息的区域值加1;若nDirect135大于T3,
                 * 则直方图中含有135度方向边缘信息的区域值加1。
                 * 
                 */

                int T = -1;

                if (max < T0) {
                    edges[0] = 0;
                    T = 0;
                } 
                else {
                    T = -1;

                    if (nDirectNone > T1) {
                        T++;
                        edges[T] = 1;
                    }
                    if (nHorizon > T2) {
                        T++;
                        edges[T] = 2;
                    }
                    if (nVertical > T2) {
                        T++;
                        edges[T] = 3;
                    }
                    if (nDirect45 > T3) {
                        T++;
                        edges[T] = 4;
                    }
                    if (nDirect135 > T3) {
                        T++;
                        edges[T] = 5;
                    }

                }

                for (int i = 0; i < (Step_Y * Step_X); i++) {
                    meanRed += CororRed[i];
                    meanGreen += CororGreen[i];
                    meanBlue += CororBlue[i];
                }

                meanRed = (int) (meanRed / (Step_Y * Step_X));
                meanGreen = (int) (meanGreen / (Step_Y * Step_X));
                meanBlue = (int) (meanBlue / (Step_Y * Step_X));

                HSV HSV;
                HSV = HSVConverter.convert(meanRed, meanGreen, meanBlue);
                
                double[] Fuzzy10BinResult;
                double[] Fuzzy24BinResult;
               
                Fuzzy10BinResult = fuzzy10.ApplyFilter(HSV.H, HSV.S, HSV.V);
                Fuzzy24BinResult = fuzzy24.ApplyFilter(HSV.H, HSV.S, HSV.V, Fuzzy10BinResult);

                for (int i = 0; i <= T; i++) {
                    for (int j = 0; j < 24; j++) {
                        if (Fuzzy24BinResult[j] > 0) histogram[24 * edges[i] + j] += Fuzzy24BinResult[j];
                    }
                }
                  
            }
        }

        double Sum = 0;
        for (int i = 0; i < 144; i++) {
            Sum += histogram[i];
        }

        for (int i = 0; i < 144; i++) {
            histogram[i] = histogram[i] / Sum;
        }

        ceddQuant.calculate(histogram);

 
    }

     
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
    public AnalyzeStrategy clone() throws CloneNotSupportedException {
        return new CEDDAnalizeStrategy(T0, T1, T2, T3);
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
