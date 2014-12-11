package imageFinder.analyzeStrategy.JDCStrategy;

import java.util.Arrays;

import com.sun.org.glassfish.external.statistics.annotations.Reset;


/**
 * 2014年12月6日
 * @author decaywood
 * 
 * 24-bins模糊过滤器就是将10-bins模糊过滤器输出的每种色区再分为3个H值区域,
 * 输入一个10维向量和S、V通道值,输出的是一个24维向量,其系统模型如图3-7所示。
 * 它输出的每一维所代表的信息分别是：
 * (0)黑色(Black),
 * (1)灰色(Grey),
 * (2)白色(White),
 * (3)暗红色(Dark Red),
 * (4)红色(Red),
 * (5)浅红(Light Red),
 * (6)暗橙色(DarkOrange),
 * (7)橙色(Orange),
 * (8)浅橙色(Light Orange),
 * (9)暗黄色(Dark Yellow),
 * (10)黄色(Yellow), 
 * (11)浅黄色(LightYellow),
 * (12)深绿色(Dark Green),
 * (13)绿色(Green),
 * (14)浅绿色(Light Green),
 * (15)暗青色(Dark Cyan),
 * (16)青色(Cyan),
 * (17)浅青色(Light Cyan),
 * (18)深蓝色(Dark Blue),
 * (19)蓝色(Blue),
 * (20)淡蓝色(LightBlue),
 * (21)暗品红色(DarkMagenta),
 * (22)品红色(Magenta),
 * (23)浅品红色(Light Magenta)。
 *
 */
public class Fuzzy24Bin {
    
    public static class FuzzyRules {
        public int Input1;
        public int Input2;
        public int Input3;
        public int Output;

    }

    public double[] ResultsTable = new double[3];
    public double[] Fuzzy24BinHisto = new double[24];

    protected double[] SaturationMembershipValues = {0, 0, 68, 188,
            68, 188, 255, 255};

    protected double[] ValueMembershipValues = {0, 0, 68, 188,
            68, 188, 255, 255};

     

    public FuzzyRules[] Fuzzy24BinRules = new FuzzyRules[4];

    public double[] SaturationActivation = new double[2];
    public double[] ValueActivation = new double[2];

    public int[][] Fuzzy24BinRulesDefinition = {
            {1, 1, 1},
            {0, 0, 2},
            {0, 1, 0},
            {1, 0, 2}
    };


    public Fuzzy24Bin() {
        for (int R = 0; R < 4; R++) {
            Fuzzy24BinRules[R] = new FuzzyRules();
            Fuzzy24BinRules[R].Input1 = Fuzzy24BinRulesDefinition[R][0];
            Fuzzy24BinRules[R].Input2 = Fuzzy24BinRulesDefinition[R][1];
            Fuzzy24BinRules[R].Output = Fuzzy24BinRulesDefinition[R][2];

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
  

    private void MultiParticipate_Defazzificator(FuzzyRules[] Rules, double[] Input1, double[] Input2, double[] ResultTable) {

        int RuleActivation = -1;
        double Min = 0;
        for (int i = 0; i < Rules.length; i++) {
            if ((Input1[Rules[i].Input1] > 0) && (Input2[Rules[i].Input2] > 0)) {
                Min = Math.min(Input1[Rules[i].Input1], Input2[Rules[i].Input2]);

                RuleActivation = Rules[i].Output;
                ResultTable[RuleActivation] += Min;

            }

        }
    }


    public double[] ApplyFilter(double Hue, double Saturation, double Value, double[] ColorValues) {
       
        double Temp = 0;

        reset();

        FindMembershipValueForTriangles(Saturation, SaturationMembershipValues, SaturationActivation);
        FindMembershipValueForTriangles(Value, ValueMembershipValues, ValueActivation);

        

        for (int i = 3; i < 10; i++) {
            Temp += ColorValues[i];
        }

        if (Temp > 0) {
                MultiParticipate_Defazzificator(Fuzzy24BinRules, SaturationActivation, ValueActivation, ResultsTable);
        }

        for (int i = 0; i < 3; i++) {
            Fuzzy24BinHisto[i] += ColorValues[i];
        }


        for (int i = 3; i < 10; i++) {
            Fuzzy24BinHisto[(i - 2) * 3] += ColorValues[i] * ResultsTable[0];
            Fuzzy24BinHisto[(i - 2) * 3 + 1] += ColorValues[i] * ResultsTable[1];
            Fuzzy24BinHisto[(i - 2) * 3 + 2] += ColorValues[i] * ResultsTable[2];
        }

        return (Fuzzy24BinHisto);

    }
    
    private void reset(){
        
        ResultsTable[0] = 0;
        ResultsTable[1] = 0;
        ResultsTable[2] = 0;
        Arrays.fill(Fuzzy24BinHisto, 0);
        
    }

}
