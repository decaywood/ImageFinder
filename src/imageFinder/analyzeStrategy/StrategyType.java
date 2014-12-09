package imageFinder.analyzeStrategy;

/**
 * 2014年12月5日
 * @author decaywood
 *
 */
public enum StrategyType {

    CEDD("CEDD"),
    
    CORLOR_LAYOUT("ColorLayout");
    
    private String strategyName;
    
    private StrategyType(String strategyName) {
        this.strategyName = strategyName;
    }
    
    public String strategyName(){ return strategyName; }
}
