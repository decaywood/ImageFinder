package imageFinder.analyzeStrategy.JDCStrategy;

import java.util.Arrays;


/**
 * 2014年12月6日
 * @author decaywood
 * 
 * 10-bins模糊过滤器的工作过程是通过三个通道输入HSV信息,然后输出10个模糊的直方图信息值。
 * 
 * 10个直方图信息值的含义如下：
 * (0)黒色(Black),
 * (1)灰色(Gray),
 * (2)白色(White),
 * (3)红色(Red), 
 * (4)橙色(Orange),
 * (5)黄色(Yellow),
 * (6)绿色(Green),
 * (7)青色(Cyan),
 * (8)蓝色(Blue),
 * (9)品红色(Magenta)。 
 *   
 * 10-bins模糊过滤器是基于模糊理论的,我们先来分析一下模糊理论中颜色径向边缘的生成。
 * 由于H代表的是色调,从它的计算方法可以看出H的取值范围为0-360,则当一张图片上出现由一种颜色向另一种颜色过渡时,
 * H值的变化就会较快,这时就会出现所谓的颜色径向边缘。根据模糊理论可以找出这些径向边缘的位置.
 * 
 * 其他同理
 *
 */
public class Fuzzy10Bin {
    
    
    
    private static class FuzzyRules {
        int Input1;
        int Input2;
        int Input3;
        int Output;
    }
    
    /**
     * 将H通道的值分为八个模糊区域,每一区域依次命名为：
     * (0)红色-橙色(Redto Orange),
     * (1)橙色(Orange),
     * (2)黄色(Yellow),
     * (3)绿色(Green),
     * (4)青色(Cyan),
     * (5)蓝色(Blue),
     * (6)品红色(Magenta),
     * (7)蓝色-红色(Blueto Red)。
     *  每两个相邻区域都有交叉的部分。
     *  
     *  其他同理
     */

    private double[] HueMembershipValues = {0, 0, 5, 10,
            5, 10, 35, 50,
            35, 50, 70, 85,
            70, 85, 150, 165,
            150, 165, 195, 205,
            195, 205, 265, 280,
            265, 280, 315, 330,
            315, 330, 360, 360}; // Table Dimensions= Number Of Triangles X 4 (Start - Stop)

    private double[] SaturationMembershipValues = {0, 0, 10, 75,
            10, 75, 255, 255};

    private double[] ValueMembershipValues = {0, 0, 10, 75,
            10, 75, 180, 220,
            180, 220, 255, 255};

    //Vector fuzzy10BinRules = new Vector();
    private FuzzyRules[] Fuzzy10BinRules;

    private double[] Fuzzy10BinHisto;
    private double[] HueActivation;
    private double[] SaturationActivation;
    private double[] ValueActivation;

    private int[][] Fuzzy10BinRulesDefinition = {
            {0, 0, 0, 2},
            {0, 1, 0, 2},
            {0, 0, 2, 0},
            {0, 0, 1, 1},
            {1, 0, 0, 2},
            {1, 1, 0, 2},
            {1, 0, 2, 0},
            {1, 0, 1, 1},
            {2, 0, 0, 2},
            {2, 1, 0, 2},
            {2, 0, 2, 0},
            {2, 0, 1, 1},
            {3, 0, 0, 2},
            {3, 1, 0, 2},
            {3, 0, 2, 0},
            {3, 0, 1, 1},
            {4, 0, 0, 2},
            {4, 1, 0, 2},
            {4, 0, 2, 0},
            {4, 0, 1, 1},
            {5, 0, 0, 2},
            {5, 1, 0, 2},
            {5, 0, 2, 0},
            {5, 0, 1, 1},
            {6, 0, 0, 2},
            {6, 1, 0, 2},
            {6, 0, 2, 0},
            {6, 0, 1, 1},
            {7, 0, 0, 2},
            {7, 1, 0, 2},
            {7, 0, 2, 0},
            {7, 0, 1, 1},
            {0, 1, 1, 3},
            {0, 1, 2, 3},
            {1, 1, 1, 4},
            {1, 1, 2, 4},
            {2, 1, 1, 5},
            {2, 1, 2, 5},
            {3, 1, 1, 6},
            {3, 1, 2, 6},
            {4, 1, 1, 7},
            {4, 1, 2, 7},
            {5, 1, 1, 8},
            {5, 1, 2, 8},
            {6, 1, 1, 9},
            {6, 1, 2, 9},
            {7, 1, 1, 3},
            {7, 1, 2, 3}
    };  // 48 0 
    //

    public Fuzzy10Bin() {

        this.Fuzzy10BinRules = new FuzzyRules[48];

        this.Fuzzy10BinHisto = new double[10];
        this.HueActivation = new double[8];
        this.SaturationActivation = new double[2];
        this.ValueActivation = new double[3];
        
        for (int R = 0; R < 48; R++) {
            this.Fuzzy10BinRules[R] = new FuzzyRules();
            this.Fuzzy10BinRules[R].Input1 = this.Fuzzy10BinRulesDefinition[R][0];
            this.Fuzzy10BinRules[R].Input2 = this.Fuzzy10BinRulesDefinition[R][1];
            this.Fuzzy10BinRules[R].Input3 = this.Fuzzy10BinRulesDefinition[R][2];
            this.Fuzzy10BinRules[R].Output = this.Fuzzy10BinRulesDefinition[R][3];
        }
    }


    private void FindMembershipValueForTriangles(double Input, double[] Triangles, double[] MembershipFunctionToSave) {
        int Temp = 0;

        for (int i = 0; i <= Triangles.length - 1; i += 4) {

            MembershipFunctionToSave[Temp] = 0;

            if (Input >= Triangles[i + 1] && Input <= +Triangles[i + 2]) {
                MembershipFunctionToSave[Temp] = 1;
            }

            if (Input >= Triangles[i] && Input < Triangles[i + 1]) {
                MembershipFunctionToSave[Temp] = (Input - Triangles[i]) / (Triangles[i + 1] - Triangles[i]);
            }

            if (Input > Triangles[i + 2] && Input <= Triangles[i + 3]) {
                MembershipFunctionToSave[Temp] = (Input - Triangles[i + 2]) / (Triangles[i + 2] - Triangles[i + 3]) + 1;
            }

            Temp += 1;
        }

    }

 
   
    private void MultiParticipate_Defazzificator(FuzzyRules[] Rules, double[] Input1, double[] Input2, double[] Input3, double[] ResultTable) {

        int RuleActivation = -1;

        for (int i = 0; i < Rules.length; i++) {
            if ((Input1[Rules[i].Input1] > 0) && (Input2[Rules[i].Input2] > 0) && (Input3[Rules[i].Input3] > 0)) {
                RuleActivation = Rules[i].Output;
                double Min = 0;
                Min = Math.min(Input1[Rules[i].Input1], Math.min(Input2[Rules[i].Input2], Input3[Rules[i].Input3]));

                ResultTable[RuleActivation] += Min;

            }

        }
    }

    public double[] ApplyFilter(double H, double S, double V) {
        // Method   0 = LOM
        //          1 = Multi Equal Participate
        //          2 = Multi Participate
        reset();

        FindMembershipValueForTriangles(H, HueMembershipValues, HueActivation);
        FindMembershipValueForTriangles(S, SaturationMembershipValues, SaturationActivation);
        FindMembershipValueForTriangles(V, ValueMembershipValues, ValueActivation);
      
        MultiParticipate_Defazzificator(Fuzzy10BinRules, HueActivation, SaturationActivation, ValueActivation, Fuzzy10BinHisto);

        return (Fuzzy10BinHisto);

    }
    
    private void reset(){
        Arrays.fill(Fuzzy10BinHisto, 0);
    }
}
