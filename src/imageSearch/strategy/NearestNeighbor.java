package imageSearch.strategy;

import imageSearch.strategy.ShrinkStrategy;

import java.awt.image.BufferedImage;

/**
 * 2014年12月4日
 * @author decaywood
 *
 *  最近邻点插值法(NearestNeighbor)又称泰森多边形方法,
 *  泰森多边形(Thiesen,又叫Dirichlet或voronoi多边形)分析法是荷兰气象学家A.H.Thiessen提出的一种分析方法。
 *  最初用于从离散分布气象站的降雨量数据中计算平均降雨量,现在GIS和地理分析中经常采用泰森多边形进行快速的赋值。
 *  实际上,最近邻点插值的一个隐含的假设条件是任一网格点p(x,y)的属性值都使用距它最近的位置点的属性值,
 *  用每一个网格节点的最邻点值作为待的节点值。当数据已经是均匀间隔分布,要先将数据转换为surfer的网格文件,
 *  可以应用最近邻点插值法;或者在一个文件中,数据紧密完整,只有少数点没有取值,可用最近邻点插值法来填充无值的数据点。
 *  有时需要排除网格文件中的无值数据的区域,在搜索椭圆 (SearchEllipse)设置一个值,对无数据区域赋予该网格文件里的空白值。
 *  设置的搜索半径的大小要小于该网格文件数据值之间的距离,所有的无数据网格节点都被赋予空白值。在使用最近邻点插值网格化法,
 *  将一个规则间隔的XYZ数据转换为一个网格文件时,可设置网格间隔和XYZ数据的数据点之间的间距相等。最近邻点插值网格化法没有选项,
 *  它是均质且无变化的,对均匀间隔的数据进行插值很有用,同时,它对填充无值数据的区域很有效。
 *  
 */
public class NearestNeighbor implements ShrinkStrategy{
    
    
    
    private int destWidth;
    private int destHeight;
    
    
    /**
     * 由于一种策略的缩放长宽一旦定下来就不会改变，故为了减小垃圾回收
     * 记录采样点的数组对象不会因为方法调用而新建，新值直接覆盖数组中旧值即可
     */
    private int[] scalePointsX;
    private int[] scalePointsY;
    
    
    public NearestNeighbor() {
        this(8, 8);
    }
    
    
    public NearestNeighbor(int destWidth, int destHeight) {
        this.scalePointsX = new int[destWidth];
        this.scalePointsY = new int[destHeight];
        
        this.destWidth = destWidth;
        this.destHeight = destHeight;
    }
    

    /**
     * 2014年12月4日
     * @author decaywood
     *
     */ 
    @Override
    public BufferedImage shrink(BufferedImage sourceImage) {
        
        int srcWidth = sourceImage.getWidth();
        int srcHeight = sourceImage.getHeight();
        
        float scaleWidth = (float)srcWidth / destWidth;
        float scaleHeight = (float)srcHeight / destHeight;
        
        
        for(int index = 0; index < scalePointsX.length; index++) 
            scalePointsX[index] = (int) (index * scaleWidth);
         
        for(int index = 0; index < scalePointsY.length; index++)
            scalePointsY[index] = (int) (index * scaleHeight);
        
        BufferedImage destinationImage = new BufferedImage(destWidth, destHeight, sourceImage.getType());
        
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

//    private int transfer(int RGB){
//        int R = (RGB &0xFF0000 )>>16;
//        int G = (RGB &0xFF00)>>8;
//        int B = RGB &0xFF;
//        int gray = (R*38 + G*75 + B*15) >> 7;
//        return (gray<<16)|(gray<<8)|gray;
//    }

}
