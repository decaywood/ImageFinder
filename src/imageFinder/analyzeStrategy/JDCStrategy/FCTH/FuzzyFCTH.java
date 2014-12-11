package imageFinder.analyzeStrategy.JDCStrategy.FCTH;

 
public class FuzzyFCTH {
    
    
    private static class FuzzyRules{
        
        public int Input1;
        public int Input2;
        public int Input3;
        public int Output;
        
    }
    
    
    public double[] ResultsTable = new double[8];

    public double[] HorizontalMembershipValues = {0, 0, 20, 90, 20, 90, 255, 255};

    public double[] VerticalMembershipValues = {0, 0, 20, 90, 20, 90, 255, 255};

    public double[] EnergyMembershipValues = {0, 0, 20, 80, 20, 80, 255, 255};

    public FuzzyRules[] TextureRules = new FuzzyRules[8];

    public double[] FCTH = new double[192];
    public double[] HActivation = new double[2];
    public double[] VActivation = new double[2];
    public double[] EActivation = new double[2];

    public int[][] RulesDefinition = {
            {0, 0, 0, 0},
            {0, 0, 1, 1},
            {0, 1, 0, 2},
            {0, 1, 1, 3},
            {1, 0, 0, 4},
            {1, 0, 1, 5},
            {1, 1, 0, 6},
            {1, 1, 1, 7}};

    public FuzzyFCTH() {
        for (int R = 0; R < 8; R++) {
            TextureRules[R] = new FuzzyRules();
            TextureRules[R].Input1 = RulesDefinition[R][0];
            TextureRules[R].Input2 = RulesDefinition[R][1];
            TextureRules[R].Input3 = RulesDefinition[R][2];
            TextureRules[R].Output = RulesDefinition[R][3];

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


    public double[] ApplyFilter(double F1, double F2, double F3, double[] ColorValues, int NumberOfColors) {

        for (int i = 0; i < 8; i++) {
            ResultsTable[i] = 0;

        }

        FindMembershipValueForTriangles(F1, HorizontalMembershipValues, HActivation);
        FindMembershipValueForTriangles(F2, VerticalMembershipValues, VActivation);
        FindMembershipValueForTriangles(F3, EnergyMembershipValues, EActivation);


        MultiParticipate_Defazzificator(TextureRules, HActivation, VActivation, EActivation, ResultsTable);


        for (int i = 0; i < 8; i++) {
            if (ResultsTable[i] > 0) {
                for (int j = 0; j < NumberOfColors; j++) {
                    if (ColorValues[j] > 0) FCTH[NumberOfColors * i + j] += ResultsTable[i] * ColorValues[j];
                }
            }
        }
        return (FCTH);
    }


}

