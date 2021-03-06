package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

/**
 * A string representation of a method call and its arguments.
 * 
 * Usually used as a bridge between external libraries' data structures.
 * 
 * It is suggested clients make a static factory
 * 
 * @author Kevin Lubick
 * 
 */
public class QMethodAndArgs extends QMethod {

    public final List<String> argumentTypes; // dot separated argument types

    /**
     * A string representation of a method call and its arguments.
     * 
     * Usually used as a bridge between external libraries' data structures.
     * 
     */
    public QMethodAndArgs(String qualifiedTypeString, String invokedMethodString, List<String> argumentTypes) {
        super(qualifiedTypeString, invokedMethodString);
        this.argumentTypes = Collections.unmodifiableList(argumentTypes);
    }

    public static List<String> expressionsToTypeStrings(List<Expression> expressions) {
        List<String> list = new ArrayList<>();
        for (Expression type : expressions) {
            list.add(type.resolveTypeBinding().getQualifiedName());
        }
        return list;
    }

    @Override
    public String toString() {
        return "QMethodAndArgs [qualifiedTypeString=" + qualifiedTypeString + ", invokedMethodString=" + invokedMethodString
                + ", argumentTypes=" + argumentTypes + ']';
    }

    public static QMethodAndArgs make(ClassInstanceCreation node) {
        String qtype = node.getType().resolveBinding().getQualifiedName();
        String method = CONSTRUCTOR_METHOD;
        // can't use node.arguments() because those are the concrete types, not necessarily
        // the types of the methods
        IMethodBinding constructor = node.resolveConstructorBinding();
        return new QMethodAndArgs(qtype, method, expressionsToTypeStrings(constructor.getParameterTypes()));
    }

    private static List<String> expressionsToTypeStrings(ITypeBinding[] typeArguments) {
        List<String> retVal = new ArrayList<>();
        for (ITypeBinding binding : typeArguments) {
            retVal.add(binding.getQualifiedName());
        }
        return retVal;
    }

    @SuppressWarnings("unchecked")
    public static QMethodAndArgs make(MethodInvocation node) {
        String qtype = node.getExpression().resolveTypeBinding().getQualifiedName();
        String method = node.getName().getIdentifier();
        return new QMethodAndArgs(qtype, method, expressionsToTypeStrings(node.arguments()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * super.hashCode() + ((argumentTypes == null) ? 0 : argumentTypes.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        QMethodAndArgs other = (QMethodAndArgs) obj;
        if (argumentTypes == null) {
            if (other.argumentTypes != null)
                return false;
        } else if (!argumentTypes.equals(other.argumentTypes))
            return false;
        return true;
    }

}
