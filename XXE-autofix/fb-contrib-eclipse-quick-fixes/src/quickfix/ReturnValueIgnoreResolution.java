package quickfix;

import static edu.umd.cs.findbugs.plugin.eclipse.quickfix.util.ASTUtil.addImports;
import static edu.umd.cs.findbugs.plugin.eclipse.quickfix.util.ASTUtil.getASTNode;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import edu.umd.cs.findbugs.plugin.eclipse.quickfix.ApplicabilityVisitor;
import edu.umd.cs.findbugs.plugin.eclipse.quickfix.BugResolution;
import edu.umd.cs.findbugs.plugin.eclipse.quickfix.CustomLabelVisitor;
import edu.umd.cs.findbugs.plugin.eclipse.quickfix.exception.BugResolutionException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;

import util.QMethod;

public class ReturnValueIgnoreResolution extends BugResolution {

    private final static String exceptionalSysOut = "System.out.println(\"Exceptional return value\");";

    public final static String descriptionForWrapIf = "Replace with <code><pre>if (YYY) {\n\t" + exceptionalSysOut
            + "\n}</pre></code>";

    public final static String descriptionForNegatedWrapIf = "Replace with <code><pre>if (!YYY) {\n\t" + exceptionalSysOut
            + "\n}</pre></code>";

    public final static String descriptionForNewLocal = "Makes a new local variable and assigns the result of the method call to it.";

    public final static String descriptionForStoreToSelf = "Stores the result of the method call back to the original method caller.";

    private String description;

    private static Set<String> immutableTypes = new HashSet<String>();

    private static Set<QMethod> shouldNotBeIgnored = new HashSet<QMethod>();

    static {
        immutableTypes.add("java.lang.String");
        immutableTypes.add("java.math.BigDecimal");
        immutableTypes.add("java.math.BigInteger");
        immutableTypes.add("java.sql.Connection");
        immutableTypes.add("java.net.InetAddress");
        immutableTypes.add("jsr166z.forkjoin.ParallelArray");
        immutableTypes.add("jsr166z.forkjoin.ParallelLongArray");
        immutableTypes.add("jsr166z.forkjoin.ParallelDoubleArray");

        shouldNotBeIgnored.add(new QMethod("java.util.Iterator", "hasNext"));
        shouldNotBeIgnored.add(new QMethod("java.security.MessageDigest", "digest"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.locks.ReadWriteLock", "readLock"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.locks.ReadWriteLock", "writeLock"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.locks.Condition", "await"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.CountDownLatch", "await"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.locks.Condition", "awaitUntil"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.locks.Condition", "awaitNanos"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.Semaphore", "tryAcquire"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.locks.Lock", "tryLock"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.locks.Lock", "newCondition"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.locks.Lock", "tryLock"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.BlockingQueue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.ConcurrentLinkedQueue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.DelayQueue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.LinkedBlockingQueue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.LinkedList", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.Queue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.ArrayBlockingQueue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.SynchronousQueue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.PriorityQueue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.PriorityBlockingQueue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.BlockingQueue", "poll"));
        shouldNotBeIgnored.add(new QMethod("java.util.Queue", "poll"));
        shouldNotBeIgnored.add(new QMethod("java.lang.String", "getBytes"));
        shouldNotBeIgnored.add(new QMethod("java.lang.String", "charAt"));
        shouldNotBeIgnored.add(new QMethod("java.lang.String", "toString"));
        shouldNotBeIgnored.add(new QMethod("java.lang.String", "length"));
        shouldNotBeIgnored.add(new QMethod("java.lang.String", "matches"));
        shouldNotBeIgnored.add(new QMethod("java.lang.String", "intern"));
        shouldNotBeIgnored.add(new QMethod("java.lang.String", "<init>"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigDecimal", "inflate"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigDecimal", "precision"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigDecimal", "toBigIntegerExact"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigDecimal", "longValueExact"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigDecimal", "intValueExact"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigDecimal", "shortValueExact"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigDecimal", "byteValueExact"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigDecimal", "<init>"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigDecimal", "intValue"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigDecimal", "stripZerosToMatchScale"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigInteger", "addOne"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigInteger", "subN"));
        shouldNotBeIgnored.add(new QMethod("java.math.BigInteger", "<init>"));
        shouldNotBeIgnored.add(new QMethod("java.net.InetAddress", "getByName"));
        shouldNotBeIgnored.add(new QMethod("java.net.InetAddress", "getAllByName"));
        shouldNotBeIgnored.add(new QMethod("java.lang.ProcessBuilder", "redirectErrorStream"));
        shouldNotBeIgnored.add(new QMethod("java.sql.Statement", "executeQuery"));
        shouldNotBeIgnored.add(new QMethod("java.sql.PreparedStatement", "executeQuery"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.LinkedBlockingQueue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.Queue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.ArrayBlockingQueue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.SynchronousQueue", "offer"));
        shouldNotBeIgnored.add(new QMethod("java.util.concurrent.ExecutorService", "submit"));
        shouldNotBeIgnored.add(new QMethod("java.io.File", "createNewFile"));
        shouldNotBeIgnored.add(new QMethod("java.io.File", "delete"));
        shouldNotBeIgnored.add(new QMethod("java.io.File", "mkdir"));
        shouldNotBeIgnored.add(new QMethod("java.io.File", "mkdirs"));
        shouldNotBeIgnored.add(new QMethod("java.io.File", "renameTo"));
        shouldNotBeIgnored.add(new QMethod("java.io.File", "setLastModified"));
        shouldNotBeIgnored.add(new QMethod("java.io.File", "setReadOnly"));
        shouldNotBeIgnored.add(new QMethod("java.io.File", "setWritable"));
    }

    private enum TriStatus {
        UNRESOLVED, TRUE, FALSE
    }

    private enum QuickFixType {
        STORE_TO_NEW_LOCAL(descriptionForNewLocal), STORE_TO_SELF(descriptionForStoreToSelf),
        WRAP_WITH_IF(descriptionForWrapIf), WRAP_WITH_NEGATED_IF(descriptionForNegatedWrapIf);

        private String description;

        QuickFixType(String d) {
            description = d;
        }

        public String getDescription() {
            return description;
        }
    }

    private QuickFixType quickFixType;

    private ImportRewrite typeSource;

    @Override
    public void setOptions(Map<String, String> options) {
        quickFixType = QuickFixType.valueOf(options.get("resolutionType"));
    }

    @Override
    protected boolean resolveBindings() {
        return true;
    }

    @Override
    protected ASTVisitor getApplicabilityVisitor() {
        return new ReturnValueResolutionVisitor();
    }

    @Override
    protected ASTVisitor getCustomLabelVisitor() {
        return new ReturnValueResolutionVisitor();
    }

    @Override
    @SuppressFBWarnings(value = "BAS_BLOATED_ASSIGNMENT_SCOPE",
            justification = "the call to getLabel() is needed to fill out information for custom descriptions")
    public String getDescription() {
        if (description == null) {
            String label = getLabel(); // force traversing, which fills in description
            if (description == null) {
                return label; // something funky is happening, description shouldn't be null
                              // We'll be safe and return label (which is not null)
            }
        }
        return description;
    }

    @Override
    protected void repairBug(ASTRewrite rewrite, CompilationUnit workingUnit, BugInstance bug) throws BugResolutionException {
        ASTNode node = getASTNode(workingUnit, bug.getPrimarySourceLineAnnotation());
        this.typeSource = ImportRewrite.create(workingUnit, true); // these imports won't get added automatically

        ReturnValueResolutionVisitor rvrFinder = new ReturnValueResolutionVisitor();
        node.accept(rvrFinder);

        Statement fixedStatement = makeFixedStatement(rewrite, rvrFinder);

        if (fixedStatement != null && rvrFinder.badMethodInvocation != null) {
            // we have to call getParent() to get the statement.
            // If we simply replace the MethodInvocation with the ifStatement (or whatever),
            // we get an extra semicolon on the end.
            rewrite.replace(rvrFinder.badMethodInvocation.getParent(), fixedStatement, null);
        }
        // this is the easiest way to make the new imports (from the type conversion)
        // actually be added
        addImports(rewrite, workingUnit, typeSource.getAddedImports());
    }

    private Statement makeFixedStatement(ASTRewrite rewrite, ReturnValueResolutionVisitor rvrFinder) {
        switch (quickFixType) {
        case STORE_TO_NEW_LOCAL:
            return makeVariableDeclarationFragment(rewrite, rvrFinder);
        case STORE_TO_SELF:
            return makeSelfAssignment(rewrite, rvrFinder);
        case WRAP_WITH_IF:
            return makeIfStatement(rewrite, rvrFinder, false);

        case WRAP_WITH_NEGATED_IF:
            return makeIfStatement(rewrite, rvrFinder, true);
        default:
            System.err.println("Couldn't make a fixed statement? " + rvrFinder.badMethodInvocation);
            return null;
        }
    }

    private Statement makeSelfAssignment(ASTRewrite rewrite, ReturnValueResolutionVisitor rvrFinder) {
        AST rootNode = rewrite.getAST();
        Assignment newAssignment = rootNode.newAssignment();

        Expression leftExpression = rvrFinder.badMethodInvocation.getExpression();

        while (leftExpression instanceof MethodInvocation) {
            leftExpression = ((MethodInvocation) leftExpression).getExpression();
        }

        newAssignment.setLeftHandSide((Expression) rewrite.createCopyTarget(leftExpression));
        newAssignment.setRightHandSide((Expression) rewrite.createCopyTarget(
                rvrFinder.badMethodInvocation));

        return rootNode.newExpressionStatement(newAssignment);
    }

    private Statement makeVariableDeclarationFragment(ASTRewrite rewrite, ReturnValueResolutionVisitor rvrFinder) {
        AST rootNode = rewrite.getAST();
        VariableDeclarationFragment fragment = rootNode.newVariableDeclarationFragment();
        fragment.setInitializer((Expression) rewrite.createMoveTarget(rvrFinder.badMethodInvocation));
        fragment.setName(rootNode.newSimpleName("local"));
        VariableDeclarationStatement retVal = rootNode.newVariableDeclarationStatement(fragment);

        Type type = getTypeFromTypeBinding(rvrFinder.badMethodInvocation.resolveTypeBinding(), rootNode);

        retVal.setType(type);

        return retVal;
    }

    /*
     * A "proper" way to get the type from a type binding. It allows the import to be added
     * if it doesn't exist. The return value can be used to write new nodes.
     */
    private Type getTypeFromTypeBinding(ITypeBinding typeBinding, AST rootNode) {
        return typeSource.addImport(typeBinding, rootNode);
    }

    @SuppressWarnings("unchecked")
    private Statement makeIfStatement(ASTRewrite rewrite, ReturnValueResolutionVisitor rvrFinder, boolean b) {
        AST rootNode = rewrite.getAST();
        IfStatement ifStatement = rootNode.newIfStatement();

        Expression expression = makeIfExpression(rewrite, rvrFinder, b);
        ifStatement.setExpression(expression);

        // the block surrounds the inner statement with {}
        Block thenBlock = rootNode.newBlock();
        Statement thenStatement = makeExceptionalStatement(rootNode);
        thenBlock.statements().add(thenStatement);
        ifStatement.setThenStatement(thenBlock);

        return ifStatement;
    }

    private Expression makeIfExpression(ASTRewrite rewrite, ReturnValueResolutionVisitor rvrFinder, boolean isNegated) {
        if (isNegated)
        {
            AST rootNode = rewrite.getAST();
            PrefixExpression negation = rootNode.newPrefixExpression();
            negation.setOperator(PrefixExpression.Operator.NOT);
            negation.setOperand((Expression) rewrite.createMoveTarget(rvrFinder.badMethodInvocation));
            return negation;
        }
        return (Expression) rewrite.createMoveTarget(rvrFinder.badMethodInvocation);
    }

    @SuppressWarnings("unchecked")
    private Statement makeExceptionalStatement(AST rootNode) {
        // makes a statement `System.out.println("Exceptional return value");`
        QualifiedName sysout = rootNode.newQualifiedName(rootNode.newSimpleName("System"), rootNode.newSimpleName("out"));
        StringLiteral literal = rootNode.newStringLiteral();
        literal.setLiteralValue("Exceptional return value");

        MethodInvocation expression = rootNode.newMethodInvocation();
        expression.setExpression(sysout);
        expression.setName(rootNode.newSimpleName("println"));
        expression.arguments().add(literal);
        return rootNode.newExpressionStatement(expression);
    }

    private class ReturnValueResolutionVisitor extends ASTVisitor implements ApplicabilityVisitor, CustomLabelVisitor {

        private TriStatus returnsSelf = TriStatus.UNRESOLVED;

        private String returnTypeOfMethod;

        private MethodInvocation badMethodInvocation;

        @Override
        public boolean visit(MethodInvocation node) {
            if (badMethodInvocation != null) {
                return false; // only need to go one layer deep. By definition,
                              // if the return value is ignored, it's not nested in anything
            }

            QMethod qMethod = QMethod.make(node);
            String returnType = node.resolveTypeBinding().getQualifiedName();

            // check for the special cases in shouldNotBeIgnored
            if (shouldNotBeIgnored.contains(qMethod) && !"void".equals(returnType)) {
                badMethodInvocation = node;
                this.returnTypeOfMethod = returnType;

                // look at the returned value and see if it equals the same type
                // as what the method is invoked on.
                if (qMethod.qualifiedTypeString.equals(returnTypeOfMethod)) {
                    returnsSelf = TriStatus.TRUE;
                } else {
                    returnsSelf = TriStatus.FALSE;
                }
            }

            // check for any immutableType methods that return something of the same type
            if (immutableTypes.contains(returnType) && qMethod.qualifiedTypeString.equals(returnType)) {
                returnsSelf = TriStatus.TRUE;

                badMethodInvocation = node;
                this.returnTypeOfMethod = returnType;
            }

            // only need to go one layer deep. By definition,
            // if the return value is ignored, it's not nested in anything
            return false;
        }

        @Override
        public boolean isApplicable() {
            // This answers the question did we find something to replace?
            switch (quickFixType) {
            case STORE_TO_NEW_LOCAL:
                return badMethodInvocation != null;
            case STORE_TO_SELF:
                return returnsSelf == TriStatus.TRUE;
            case WRAP_WITH_IF:
            case WRAP_WITH_NEGATED_IF:
                return "boolean".equals(returnTypeOfMethod);
            default:
                return false;
            }
        }

        @Override
        public String getLabelReplacement() {
            // The if fixes have both a custom label and a custom description.
            // Everything else has a nice static label.
            switch (quickFixType) {
            case STORE_TO_NEW_LOCAL:
            case STORE_TO_SELF:
            default:
                description = quickFixType.getDescription();
                return "";
            case WRAP_WITH_IF:
            case WRAP_WITH_NEGATED_IF:
                String methodSourceCode = badMethodInvocation != null ? badMethodInvocation.toString() : "[method call]";
                description = quickFixType.getDescription().replace("YYY", methodSourceCode);
                // it's okay to invoke toString() here because it's user facing, not actually being turned into code
                return methodSourceCode;
            }
        }

    }

}
