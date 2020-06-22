package xml;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class NodeFound {
    private boolean applyFix;
    private VariableDeclarationFragment fragment;
    private Assignment assignment;
    private MethodInvocation invocation;
    
    public NodeFound(VariableDeclarationFragment fragment, boolean applyFix) {
        this.fragment = fragment;
        this.applyFix = applyFix;
    }
    
    public NodeFound(Assignment assignment, boolean applyFix) {
        this.assignment = assignment;
        this.applyFix = applyFix;
    }
    
    public NodeFound(MethodInvocation methodInvocation, boolean applyFix) {
        this.invocation = methodInvocation;
        this.applyFix = applyFix;
    }
    
    public String getVariableNameForNode() {
        if (invocation != null) {
            SimpleName name = invocation.getName(); // Gives the name of the function called
            return name.getFullyQualifiedName();
            
        }
        return "";
    }
    
//    public SimpleName getVariableNameForNode() {
//        if (fragment != null) {
//            return fragment.getName();
//        }
//        if (assignment != null) {
//            if (assignment.getLeftHandSide() instanceof SimpleName) {
//                return (SimpleName) assignment.getLeftHandSide();
//            }
//            if (assignment.getLeftHandSide() instanceof FieldAccess) {
//                FieldAccess fieldAccess = (FieldAccess) assignment.getLeftHandSide();
//                return fieldAccess.getName();
//            }
//
//        }
//        return null;
//    }
//    
    public SimpleName getVariableNodeIsInitializedBy() {
        if (fragment != null && fragment.getInitializer() instanceof MethodInvocation) {
            MethodInvocation initializer = (MethodInvocation) fragment.getInitializer();
            ITypeBinding b = initializer.resolveTypeBinding();
            String type = b.getName(); // Gets the type
            if (initializer.getExpression() instanceof SimpleName) {
                return (SimpleName) initializer.getExpression();                                    
            }
        }
        
        if (assignment != null) {
            MethodInvocation methodInvocation = (MethodInvocation)assignment.getRightHandSide();
            ITypeBinding b = methodInvocation.resolveTypeBinding(); // Gets the type
            String type = b.getName();
            Expression expression = methodInvocation.getExpression();
            if (expression instanceof SimpleName) {
                return (SimpleName) expression;
            }
        }
        return null; // Means we either couldn't traverse upwards, OR we couldn't go any further
    }
    
    public boolean isApplyFix() {
        return this.applyFix;
    }
}
