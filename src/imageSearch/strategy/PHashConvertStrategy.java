package imageSearch.strategy;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * 2014年12月4日
 * @author decaywood
 *
 */
public class PHashConvertStrategy extends AbstractHashTransStrategy{
    
    
    
    public PHashConvertStrategy() {
        this(32, 32);
    }
    
     
    public PHashConvertStrategy(int imageWidth, int imageHeight) {
        super(imageWidth, imageHeight);
    }
    
    
    /**
     * 2014年12月4日
     * @author decaywood
     *
     */ 
    @Override
    public long convertToHash(BufferedImage sourceImage) {
        
        fillGrays(sourceImage);
        int[] dtcResult = DCT(imageHeight);
        int average = average(dtcResult, 8, 8);
        return generateHash(average, dtcResult, 8, 8);
    }
    
    private long generateHash(int average, int[] dtcResult, int width, int height){
        long hash = 0;
        for(int indexI = 0; indexI < height; indexI++)
            for(int indexJ = 0; indexJ < width; indexJ++){
                hash <<= 1;
                hash |= dtcResult[indexI * width + indexJ] > average ? 1 : 0;
            }
        hash = hash < 0 ? hash ^ 0x8000000000000000l : hash;
        return hash;
    }
    
    
    private int average(int[] dtcResult, int width, int height){
        int sum = 0;
        for(int indexI = 0; indexI < height; indexI++)
            for(int indexJ = 0; indexJ < width; indexJ++){
                sum += dtcResult[indexI * width + indexJ];
            }
        return sum / (width * height);
    }
    
    
    
    /**
     * 
     * 2014年12月4日
     * @author decaywood
     *  离散余弦变换，尤其是它的第二种类型，经常被信号处理和图像处理使用，
     *  用于对信号和图像（包括静止图像和运动图像）进行有损数据压缩。
     *  这是由于离散余弦变换具有很强的"能量集中"特性：大多数的自然信号（包括声音和图像）
     *  的能量都集中在离散余弦变换后的低频部分，而且当信号具有接近马尔可夫过程的统计特性时，
     *  离散余弦变换的去相关性接近于K-L变换（Karhunen-Loève变换——它具有最优的去相关性）的性能。
     */
    public int[] DCT(int n) {          
        double[][] iMatrix = new double[n][n];   
        for(int i=0; i<n; i++) {  
            for(int j=0; j<n; j++) {  
                iMatrix[i][j] = (double)(grays[i*n + j]);  
            }  
        }  
        double[][] quotient = coefficient(n);   //求系数矩阵  
        double[][] quotientT = transposingMatrix(quotient, n);  //转置系数矩阵  
          
        double[][] temp;  
        temp = matrixMultiply(quotient, iMatrix, n);  
        iMatrix =  matrixMultiply(temp, quotientT, n);  
          
        int newpix[] = new int[n*n];  
        for(int i=0; i<n; i++) {  
            for(int j=0; j<n; j++) {  
                newpix[i*n + j] = (int) Math.abs(iMatrix[i][j]);  
            }  
        }  
        return newpix;  
    }  
    /** 
     * 矩阵转置 
     * @param matrix 原矩阵 
     * @param n 矩阵(n*n)的高或宽 
     * @return 转置后的矩阵 
     */  
    private double[][]  transposingMatrix(double[][] matrix, int n) {  
        double nMatrix[][] = new double[n][n];  
        for(int i=0; i<n; i++) {  
            for(int j=0; j<n; j++) {  
                nMatrix[i][j] = matrix[j][i];  
            }  
        }  
        return nMatrix;  
    }  
    /** 
     * 求离散余弦变换的系数矩阵 
     * @param n n*n矩阵的大小 
     * @return 系数矩阵 
     */  
    private double[][] coefficient(int n) {  
        double[][] coeff = new double[n][n];  
        double sqrt = 1.0/Math.sqrt(n);  
        for(int i=0; i<n; i++) {  
            coeff[0][i] = sqrt;  
        }  
        for(int i=1; i<n; i++) {  
            for(int j=0; j<n; j++) {  
                coeff[i][j] = Math.sqrt(2.0/n) * Math.cos(i*Math.PI*(j+0.5)/(double)n);  
            }  
        }  
        return coeff;  
    }  
    /** 
     * 矩阵相乘 
     * @param A 矩阵A 
     * @param B 矩阵B 
     * @param n 矩阵的大小n*n 
     * @return 结果矩阵 
     */  
    private double[][] matrixMultiply(double[][] A, double[][] B, int n) {  
        double nMatrix[][] = new double[n][n];  
        double t = 0.0;  
        for(int i=0; i<n; i++) {  
            for(int j=0; j<n; j++) {  
                t = 0;  
                for(int k=0; k<n; k++) {  
                    t += A[i][k]*B[k][j];  
                }  
                nMatrix[i][j] = t;          }  
        }  
        return nMatrix;  
    }  

}
