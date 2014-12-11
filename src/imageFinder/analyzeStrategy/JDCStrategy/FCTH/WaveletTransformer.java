package imageFinder.analyzeStrategy.JDCStrategy.FCTH;

/**
 * 2014年12月9日
 * @author decaywood
 *
 */
public class WaveletTransformer {

    public static class WaveletMatrix {

        public double F1;
        public double F2;
        public double F3;
        public double Entropy;
        
    }
    
    WaveletMatrix matrix;
    double[][] resultMatrix;
    
    public WaveletTransformer() {
        this.matrix = new WaveletMatrix();
        resultMatrix = new double[4][4];
    }
    
    public WaveletMatrix waveletTransform(double[][] inputMatrix, int level) {


        level = (int) Math.pow(2.0, level - 1);

 
        

        int xOffset = inputMatrix.length / 2 / level;

        int yOffset = inputMatrix[0].length / 2 / level;


        double multiplier = 0;


        for (int y = 0; y < inputMatrix[0].length; y++) {

            for (int x = 0; x < inputMatrix.length; x++) {

                if ((y < inputMatrix[0].length / 2 / level) && (x < inputMatrix.length / 2 / level)) {


                    resultMatrix[x][y] = (inputMatrix[2 * x][2 * y] + inputMatrix[2 * x + 1][2 * y] + inputMatrix[2 * x][2 * y + 1] + inputMatrix[2 * x + 1][2 * y + 1]) / 4;

                    double vertDiff = (-inputMatrix[2 * x][2 * y] - inputMatrix[2 * x + 1][2 * y] + inputMatrix[2 * x][2 * y + 1] + inputMatrix[2 * x + 1][2 * y + 1]);

                    double horzDiff = (inputMatrix[2 * x][2 * y] - inputMatrix[2 * x + 1][2 * y] + inputMatrix[2 * x][2 * y + 1] - inputMatrix[2 * x + 1][2 * y + 1]);

                    double diagDiff = (-inputMatrix[2 * x][2 * y] + inputMatrix[2 * x + 1][2 * y] + inputMatrix[2 * x][2 * y + 1] - inputMatrix[2 * x + 1][2 * y + 1]);


                    resultMatrix[x + xOffset][y] = (int) (byte) (multiplier + Math.abs(vertDiff));

                    resultMatrix[x][y + yOffset] = (int) (byte) (multiplier + Math.abs(horzDiff));

                    resultMatrix[x + xOffset][y + yOffset] = (int) (byte) (multiplier + Math.abs(diagDiff));


                } else {

                    if ((x >= inputMatrix.length / level) || (y >= inputMatrix[0].length / level))

                    {
                        resultMatrix[x][y] = inputMatrix[x][y];
                    }

                }

            }

        }

        double Temp1 = 0;
        double Temp2 = 0;
        double Temp3 = 0;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                Temp1 += 0.25 * Math.pow(resultMatrix[2 + i][j], 2);
                Temp2 += 0.25 * Math.pow(resultMatrix[i][2 + j], 2);
                Temp3 += 0.25 * Math.pow(resultMatrix[2 + i][2 + j], 2);


            }

        }

        matrix.F1 = Math.sqrt(Temp1);
        matrix.F2 = Math.sqrt(Temp2);
        matrix.F3 = Math.sqrt(Temp3);

        matrix.Entropy = 0;

        return matrix;

    }
}
