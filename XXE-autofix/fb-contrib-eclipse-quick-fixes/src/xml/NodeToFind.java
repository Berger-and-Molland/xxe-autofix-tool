package xml;

public class NodeToFind {
    private String type;
    private boolean applyFix;
    private String invokedMethod;
    
    public NodeToFind(String type, String invokedMethod, boolean applyFix) {
        this.type = type;
        this.invokedMethod = invokedMethod;
        this.applyFix = applyFix;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getInvokedMethod() {
        return this.invokedMethod;
    }
    
    public boolean isApplyFix() {
        return this.applyFix;
    }
}
