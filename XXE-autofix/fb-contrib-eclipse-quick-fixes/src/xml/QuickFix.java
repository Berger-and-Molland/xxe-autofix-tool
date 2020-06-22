package xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SimpleName;

public class QuickFix {
    private SimpleName nameOfFunctionToCall;
    private List<Expression> methodParameters = new ArrayList<>();
    

    public QuickFix(SimpleName newSimpleName, List<Expression> list) {
        this.nameOfFunctionToCall = newSimpleName;
        this.methodParameters = list;
    }
    
    public SimpleName getNameOfFunctionToCall() {
        return nameOfFunctionToCall;
    }
    
    public List<Expression> getMethodParameters() {
        return methodParameters;
    }

}
