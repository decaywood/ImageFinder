package imageFinder.analyzeStrategy.JDCStrategy.FCTH;





/**
 * 
 * 2014年12月9日
 * @author decaywood
 * 
 * 计算 FCTH 特征向量的相似性距离我们选择使用 Tanimoto 系数
 *
 */
public class FCTHQuant {

    private double[] QuantTable =
            {   130.0887781556944, 
                9317.31301788632, 
                22434.355689233365,
                43120.548602722061,
                83168.640165905046, 
                101430.52589975641,
                174840.65838706805,
                224480.41479670047  };

    private double[] QuantTable2 =
            {   130.0887781556944,
                9317.31301788632, 
                22434.355689233365,
                43120.548602722061, 
                83168.640165905046, 
                151430.52589975641, 
                174840.65838706805, 
                224480.41479670047  };

    private double[] QuantTable3 =
            {   239.769468748322, 
                17321.704312335689,
                39113.643180734696, 
                69333.512093874378, 
                79122.46400035513, 
                90980.3325940354, 
                161795.93301552488,
                184729.98648386425  };

    private double[] QuantTable4 =
            {   239.769468748322,
                17321.704312335689,
                39113.643180734696, 
                69333.512093874378, 
                79122.46400035513, 
                90980.3325940354, 
                161795.93301552488, 
                184729.98648386425  };

    private double[] QuantTable5 =
            {   239.769468748322, 
                17321.704312335689, 
                39113.643180734696,
                69333.512093874378,
                79122.46400035513, 
                90980.3325940354, 
                161795.93301552488, 
                184729.98648386425  };

    private double[] QuantTable6 =
            {   239.769468748322,
                17321.704312335689, 
                39113.643180734696, 
                69333.512093874378, 
                79122.46400035513, 
                90980.3325940354,
                161795.93301552488,
                184729.98648386425  };

    private double[] QuantTable7 =
            {   80.19686541079636, 
                23730.024499150866, 
                41457.152912541605, 
                53918.55437576842,
                69122.46400035513, 
                81980.3325940354, 
                91795.93301552488, 
                124729.98648386425  };

    private double[] QuantTable8 =
            {   180.19686541079636,
                23730.024499150866,
                41457.152912541605, 
                53918.55437576842, 
                69122.46400035513,
                81980.3325940354, 
                91795.93301552488, 
                124729.98648386425  };

    private double[] Edge_HistogramElement;
    private double[] ElementsDistance;
    
    public FCTHQuant() {
        Edge_HistogramElement = new double[192];
        ElementsDistance = new double[8];
    }
    
    
    
    public void calculate(double[] Local_Edge_Histogram) {
        
        double Max = 1;

        for (int i = 0; i < 24; i++) {
            Edge_HistogramElement[i] = 0;
            for (int j = 0; j < 8; j++) {
                ElementsDistance[j] = Math.abs(Local_Edge_Histogram[i] - QuantTable[j] / 1000000);
            }
            Max = 1;
            for (int j = 0; j < 8; j++) {
                if (ElementsDistance[j] < Max) {
                    Max = ElementsDistance[j];
                    Edge_HistogramElement[i] = j;
                }
            }


        }


        for (int i = 24; i < 48; i++) {
            Edge_HistogramElement[i] = 0;
            for (int j = 0; j < 8; j++) {
                ElementsDistance[j] = Math.abs(Local_Edge_Histogram[i] - QuantTable2[j] / 1000000);
            }
            Max = 1;
            for (int j = 0; j < 8; j++) {
                if (ElementsDistance[j] < Max) {
                    Max = ElementsDistance[j];
                    Edge_HistogramElement[i] = j;
                }
            }


        }


        for (int i = 48; i < 72; i++) {
            Edge_HistogramElement[i] = 0;
            for (int j = 0; j < 8; j++) {
                ElementsDistance[j] = Math.abs(Local_Edge_Histogram[i] - QuantTable3[j] / 1000000);
            }
            Max = 1;
            for (int j = 0; j < 8; j++) {
                if (ElementsDistance[j] < Max) {
                    Max = ElementsDistance[j];
                    Edge_HistogramElement[i] = j;
                }
            }


        }


        for (int i = 72; i < 96; i++) {
            Edge_HistogramElement[i] = 0;
            for (int j = 0; j < 8; j++) {
                ElementsDistance[j] = Math.abs(Local_Edge_Histogram[i] - QuantTable4[j] / 1000000);
            }
            Max = 1;
            for (int j = 0; j < 8; j++) {
                if (ElementsDistance[j] < Max) {
                    Max = ElementsDistance[j];
                    Edge_HistogramElement[i] = j;
                }
            }


        }


        for (int i = 96; i < 120; i++) {
            Edge_HistogramElement[i] = 0;
            for (int j = 0; j < 8; j++) {
                ElementsDistance[j] = Math.abs(Local_Edge_Histogram[i] - QuantTable5[j] / 1000000);
            }
            Max = 1;
            for (int j = 0; j < 8; j++) {
                if (ElementsDistance[j] < Max) {
                    Max = ElementsDistance[j];
                    Edge_HistogramElement[i] = j;
                }
            }


        }


        for (int i = 120; i < 144; i++) {
            Edge_HistogramElement[i] = 0;
            for (int j = 0; j < 8; j++) {
                ElementsDistance[j] = Math.abs(Local_Edge_Histogram[i] - QuantTable6[j] / 1000000);
            }
            Max = 1;
            for (int j = 0; j < 8; j++) {
                if (ElementsDistance[j] < Max) {
                    Max = ElementsDistance[j];
                    Edge_HistogramElement[i] = j;
                }
            }


        }


        for (int i = 144; i < 168; i++) {
            Edge_HistogramElement[i] = 0;
            for (int j = 0; j < 8; j++) {
                ElementsDistance[j] = Math.abs(Local_Edge_Histogram[i] - QuantTable7[j] / 1000000);
            }
            Max = 1;
            for (int j = 0; j < 8; j++) {
                if (ElementsDistance[j] < Max) {
                    Max = ElementsDistance[j];
                    Edge_HistogramElement[i] = j;
                }
            }


        }


        for (int i = 168; i < 192; i++) {
            Edge_HistogramElement[i] = 0;
            for (int j = 0; j < 8; j++) {
                ElementsDistance[j] = Math.abs(Local_Edge_Histogram[i] - QuantTable8[j] / 1000000);
            }
            Max = 1;
            for (int j = 0; j < 8; j++) {
                if (ElementsDistance[j] < Max) {
                    Max = ElementsDistance[j];
                    Edge_HistogramElement[i] = j;
                }
            }


        }

        for(int i = 0; i < Edge_HistogramElement.length; i++){
            Local_Edge_Histogram[i] = Edge_HistogramElement[i];
            Edge_HistogramElement[i] = 0;
        }
    }
}