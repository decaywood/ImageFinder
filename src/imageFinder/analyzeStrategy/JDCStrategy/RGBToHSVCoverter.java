package imageFinder.analyzeStrategy.JDCStrategy;

/**
 * 2014年12月6日
 * @author decaywood
 * 
 *  HSV模型中，H （Hue）代表色调，指通过物体传播或从物体射出的颜色，
 *  一般在使用中是由颜色名称来标识的。S （Saturation）代表饱和度，
 *  表示色调中灰色成分的比例，指颜色的纯度或强度。V （Value）代表亮度，
 *  指颜色相对的明暗程度。HSV模型能够较好地反应人对颜色的感知和鉴别能力，
 *  所以非常适合于比较基于颜色的图像相似性，在图像分类中也得到了广泛应用。
 *  综合上述两点，在提取颜色信息前就需要对图像像素进行RGB-HSV的模型转换。
 *  在此特征提取算法中RGB-HSV转换的方式稍有不同，且最后得出的S、V取值范围也有差别，
 *  都是（0，255），但基本原理不变，这是为了方便于后面在模糊过滤器中的运算，转换公式如下：
 * 
 *  V = max(R, G, B)
 *  S = 255 - 255 * ( max(R, G, B) ) / min(R, G, B)
 *  如果 R = max(R, G, B), 且 G >= B ====> H = 60 * ( G - B ) / ( max(R, G, B) - min(R, G, B) )
 *  如果 R = max(R, G, B), 且 G < B ====> H = ( 359 + 60 * ( G - B ) ) / ( max(R, G, B) - min(R, G, B) )
 *  如果 G = max(R, G, B) ====> H = ( 119 + 60 * ( B - R ) ) / ( max(R, G, B) - min(R, G, B) )
 *  如果 B = max(R, G, B)====> H = ( 239 + 60 * ( R - G ) ) / ( max(R, G, B) - min(R, G, B) )
 *  
 *  
 */
public class RGBToHSVCoverter {
    
    public static class HSV{
        public int H;
        public int S;
        public int V;
    }
    
    private HSV result;
    
    public RGBToHSVCoverter() {
        result = new HSV();
    }

    public HSV convert(int red, int green, int blue) {
        
        
        
        int HSV_H = 0;
        int HSV_S = 0;
        int HSV_V = 0;

        double MaxHSV = (Math.max(red, Math.max(green, blue)));
        double MinHSV = (Math.min(red, Math.min(green, blue)));

        HSV_V = (int) (MaxHSV);

        HSV_S = 0;
        if (MaxHSV != 0) HSV_S = (int) (255 - 255 * (MinHSV / MaxHSV));

        if (MaxHSV != MinHSV) {

            int IntegerMaxHSV = (int) (MaxHSV);

            if (IntegerMaxHSV == red && green >= blue) {
                HSV_H = (int) (60 * (green - blue) / (MaxHSV - MinHSV));
            } else if (IntegerMaxHSV == red && green < blue) {
                HSV_H = (int) (359 + 60 * (green - blue) / (MaxHSV - MinHSV));
            } else if (IntegerMaxHSV == green) {
                HSV_H = (int) (119 + 60 * (blue - red) / (MaxHSV - MinHSV));
            } else if (IntegerMaxHSV == blue) {
                HSV_H = (int) (239 + 60 * (red - green) / (MaxHSV - MinHSV));
            }


        } else HSV_H = 0;

        result.H = HSV_H;
        result.S = HSV_S;
        result.V = HSV_V;

        return result;
    }
    
}
