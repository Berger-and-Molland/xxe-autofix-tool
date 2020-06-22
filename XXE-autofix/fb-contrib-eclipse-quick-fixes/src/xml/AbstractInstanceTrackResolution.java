package xml;

import static edu.umd.cs.findbugs.plugin.eclipse.quickfix.util.ASTUtil.getASTNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.xml.XMLConstants;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.plugin.eclipse.quickfix.BugResolution;
import edu.umd.cs.findbugs.plugin.eclipse.quickfix.exception.BugResolutionException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import edu.umd.cs.findbugs.plugin.eclipse.quickfix.util.ASTUtil;

import util.TraversalUtil;

public abstract class AbstractInstanceTrackResolution extends BugResolution {
    private List<NodeToFind> nodesToFind = new ArrayList<>();
    
    public AbstractInstanceTrackResolution() {
        super();
        nodesToFind.addAll(addNodesToFind());
    }
    
    public abstract List<NodeToFind> addNodesToFind();
    
    public abstract List<QuickFix> getQuickFixes(AST ast);
    
    public abstract String[] importsToAdd();
    
    @Override
    protected boolean resolveBindings() {
        return true;
    }
    
    @Override
    protected void repairBug(ASTRewrite rewrite, CompilationUnit workingUnit, BugInstance bug) throws BugResolutionException {
        ASTNode node = getASTNode(workingUnit, bug.getPrimarySourceLineAnnotation());
        AST ast = node.getAST();
        
        InvocationVisitor invocationVisitor = new InvocationVisitor();
        node.accept(invocationVisitor);
        IVariableBinding nameOfInvokedInstance = invocationVisitor.invokedVariable; // If null means not initialized to variable
        
        InstanceTrackVisitor instanceTrackVisitor = new InstanceTrackVisitor(nodesToFind);
        MethodDeclaration enclosingMethod = TraversalUtil.findEnclosingMethod(workingUnit, bug.getPrimarySourceLineAnnotation());
        Block enclosingMethodBlock = enclosingMethod.getBody();
        enclosingMethodBlock.accept(instanceTrackVisitor);
     
        
        // Try to combine MethodInvocations and Assignments/VariableDeclarationStatements here
        // Once combined, we can traverse upwards because we have left hand sides and right hand sides
        // We'll also know if method invoked on a method
        // Need to split up if method invoked on the type we want to make secure
        Map<IVariableBinding, List<MethodInvocation>> invocationMap = instanceTrackVisitor.invocationMap;
        Map<IVariableBinding, IVariableBinding> assignmentMap = instanceTrackVisitor.assignmentMap;
        
        if (nameOfInvokedInstance != null) {
            System.out.println("Parsing done elsewhere and invoked on a field");
            IVariableBinding name = nameOfInvokedInstance;
            
            // Traverse to the right node
            while(true) {
                if(assignmentMap.get(name) != null) {
                    name = assignmentMap.get(name);                    
                } else {
                    break;
                }
            }

            // Get the method invocations for this node
            List<MethodInvocation> invocations = invocationMap.get(name);
            
            performQuickFix(name, invocations, rewrite, workingUnit);
            
        } else { // The initialization and parsing is done directly on the node the bug was identified on
            performQuickFixForBugOnSameNode(node, rewrite, workingUnit);
            
            System.out.println("Parsing done on this node");
        }
    }
   
    private void performQuickFix(IVariableBinding name, List<MethodInvocation> invocations, ASTRewrite rewrite, CompilationUnit compilationUnit) {
        if (invocations.size() > 1) {
            System.out.println("Cannot perform quickfix because it is indecisive");
            return; // We cannot perform the quickfix cause we found two nodes that corresponded which is indecisive
        }
        MethodInvocation invocation = invocations.get(0);
        Statement initBlock = (Statement) TraversalUtil.findClosestAncestor(invocation, Statement.class).getParent(); // Get the block the variable was initialized in to apply the fix
        
        for (NodeToFind node : nodesToFind) {
            if (node.getType().equals(name.getType().getName()) && invocation.getName().getIdentifier().equals(node.getInvokedMethod()) && node.isApplyFix()) {
                // Fix needs to  be inserted on this node
                fixInsecureVariable(name, initBlock, rewrite, invocation);
                ASTUtil.addImports(rewrite, compilationUnit, importsToAdd());
                // TODO: Can probably optimize this else
            } else if (node.isApplyFix() && isChainedNodeToFix(invocation, node.getType(), node.getInvokedMethod())) { //if(isChainedNodeToFix(invocation, node.getType(), node.getInvokedMethod()) && node.isApplyFix()) {
                fixInsecureChainedMethod(initBlock, rewrite, invocation, node);
                ASTUtil.addImports(rewrite, compilationUnit, importsToAdd());
            }
        }
    }
    
    private void fixInsecureChainedMethod(Statement parentBlock, ASTRewrite rewrite, MethodInvocation invocation, NodeToFind node) {
        MethodInvocation initMethod = chainedNodeToFix(invocation, node.getType(), node.getInvokedMethod());
        SimpleName secureName = rewrite.getAST().newSimpleName("secureFactory"); // TODO: Should probably do something smarter than hard code this
        VariableDeclarationStatement secureVariable = createSecureVariable(rewrite, node.getType(), initMethod, secureName);
            
        ListRewrite statements = getListRewrite(parentBlock, rewrite);
        statements.insertBefore(secureVariable, getDeclarationStatement(initMethod), null);
        insertSecureInvocations(secureName.getIdentifier(), statements, rewrite, secureVariable);
        rewrite.replace(initMethod, secureName, null);
    }


    private void fixInsecureVariable(IVariableBinding name, Statement parentBlock, ASTRewrite rewrite, MethodInvocation invocation) {
        ListRewrite statements = getListRewrite(parentBlock, rewrite);
        insertSecureInvocations(name.getName(), statements, rewrite, getDeclarationStatement(invocation));
    }
    
    private void insertSecureInvocations(String name, ListRewrite statements, ASTRewrite rewrite, Statement statementToInsertAfter) {
        // TODO: Check if quickfix already exists and skip it?
        for(QuickFix quickfix : getQuickFixes(rewrite.getAST())) {
            MethodInvocation secureFactory = invokeSecureMethod(name, rewrite, quickfix.getNameOfFunctionToCall(), quickfix.getMethodParameters());
            ExpressionStatement expressionToInsert = rewrite.getAST().newExpressionStatement(secureFactory);
            statements.insertAfter(expressionToInsert, statementToInsertAfter, null);
            statementToInsertAfter = expressionToInsert;
        }
    }
    
    private ListRewrite getListRewrite(Statement block, ASTRewrite rewrite) {
        if (block instanceof Block) {
            return rewrite.getListRewrite(block, Block.STATEMENTS_PROPERTY);
        }
        if (block instanceof SwitchStatement) {
            return rewrite.getListRewrite(block, SwitchStatement.STATEMENTS_PROPERTY);
        }
        return null;
    }

    
    private void performQuickFixForBugOnSameNode(ASTNode node, ASTRewrite rewrite, CompilationUnit compilationUnit) {
        FindFirstMethodInvocationVisitor visitor = new FindFirstMethodInvocationVisitor();
        node.accept(visitor);
        MethodInvocation invocation = visitor.invocation;
        if (invocation == null) {
            return; // We cannot perform the quickfix
        }
        
        Block initBlock = TraversalUtil.findClosestAncestor(invocation, Block.class);
        for(NodeToFind nodeToFind : nodesToFind) {
            if (nodeToFind.isApplyFix() && isChainedNodeToFix(invocation, nodeToFind.getType(), nodeToFind.getInvokedMethod())) {
                fixInsecureChainedMethod(initBlock, rewrite, invocation, nodeToFind);
                ASTUtil.addImports(rewrite, compilationUnit, importsToAdd());
            }
        }
    }
    
    private Statement getDeclarationStatement(MethodInvocation invocation) {
        ExpressionStatement expressionStatement = TraversalUtil.findClosestAncestor(invocation, ExpressionStatement.class);
        if (expressionStatement != null) {
            return expressionStatement;
        } else  {
            return TraversalUtil.findClosestAncestor(invocation, VariableDeclarationStatement.class);
        }
    }
    
    private VariableDeclarationStatement createSecureVariable(ASTRewrite rewrite, String type, MethodInvocation invocation, SimpleName secureName) {
        AST ast = rewrite.getAST();
        SimpleType simpleType = ast.newSimpleType(ast.newSimpleName(type));
        VariableDeclarationFragment secureFragment = ast.newVariableDeclarationFragment();
        secureFragment.setName(secureName);
        secureFragment.setInitializer((Expression) rewrite.createMoveTarget(invocation));
        
        VariableDeclarationStatement secureVariable = ast.newVariableDeclarationStatement(secureFragment);
        secureVariable.setType(simpleType);
        return secureVariable;
    }
    
    private MethodInvocation chainedNodeToFix(MethodInvocation invocation, String typeToFind, String invokedMethodToFind) {
        IMethodBinding binding = invocation.resolveMethodBinding();
        String type = binding.getReturnType().getName();
        String invokedMethod = binding.getName();
        if (invokedMethod.equals(invokedMethodToFind) && typeToFind.equals(type)) {
            return invocation;
        }
        if (invocation.getExpression() instanceof MethodInvocation) {
            return chainedNodeToFix((MethodInvocation) invocation.getExpression(), typeToFind, invokedMethodToFind);
        }
        return invocation;
    }
    
    private boolean isChainedNodeToFix(MethodInvocation invocation, String typeToFind, String invokedMethodToFind) {
        IMethodBinding binding = invocation.resolveMethodBinding();
        String type = binding.getReturnType().getName();
        String invokedMethod = binding.getName();
        if (invokedMethod.equals(invokedMethodToFind) && typeToFind.equals(type)) {
            return true;
        }
        if (invocation.getExpression() instanceof MethodInvocation) {
            return isChainedNodeToFix((MethodInvocation) invocation.getExpression(), typeToFind, invokedMethodToFind);
        }
        return false;
    }
    
    private MethodInvocation invokeSecureMethod(String name, ASTRewrite rewrite, SimpleName methodToInvoke, List<Expression> methodParameters) {
        AST ast = rewrite.getAST();
        MethodInvocation newMethod = ast.newMethodInvocation();
        newMethod.setExpression(ast.newSimpleName(name));
        newMethod.setName(methodToInvoke);
        for(Expression argument : methodParameters) {
            newMethod.arguments().add(argument);
        }
        return newMethod;
    }
    
    private static class FindFirstMethodInvocationVisitor extends ASTVisitor {
        private MethodInvocation invocation;
        @Override
        public boolean visit(MethodInvocation node) {
            this.invocation = node;
            return false;
        }
    }
  
    private static class InvocationVisitor extends ASTVisitor {
        private IVariableBinding invokedVariable;
        @Override
        public boolean visit(MethodInvocation node) {
            Expression expression = node.getExpression();
            if (expression instanceof FieldAccess) {
                FieldAccess field = (FieldAccess) expression;
                IVariableBinding binding = field.resolveFieldBinding();
                this.invokedVariable = binding;
                return false;
            } else if (expression instanceof SimpleName && ((SimpleName) expression).resolveBinding() instanceof IVariableBinding) {
                SimpleName simpleName = (SimpleName) expression;
                IVariableBinding binding = (IVariableBinding) simpleName.resolveBinding();
                this.invokedVariable = binding;
                return false;
            }
            return true;
        }
    }
    
    private static class InstanceTrackVisitor extends ASTVisitor {
        // Length of this depends on how many fragments we are interested in
        private List<NodeToFind> nodesToFind;
        public Map<IVariableBinding, List<MethodInvocation>> invocationMap = new HashMap<>();
        public Map<IVariableBinding, IVariableBinding> assignmentMap = new HashMap<>();
        private Map<IVariableBinding, Integer> lastInvokedOnLine = new HashMap<>();
                
        public InstanceTrackVisitor(List<NodeToFind> fragmentsToFind) {
            this.nodesToFind = fragmentsToFind;
        }
        
        // Initialized to method field is VariableDeclarationStatement        
        @Override
        public boolean visit(VariableDeclarationStatement node) {
            Type type = node.getType();
            if (type instanceof SimpleType) {
                String qualifiedTypeName = ((SimpleType) type).getName().getFullyQualifiedName();
                
                for(NodeToFind fragmentToFind : nodesToFind) {
                    if(fragmentToFind.getType().equals(qualifiedTypeName)) {
                        List args = node.fragments();
                        // TODO: When does args size differ from 1?
                        if (args.size() == 1 && args.get(0) instanceof VariableDeclarationFragment) {
                            VariableDeclarationFragment fragment = (VariableDeclarationFragment) args.get(0);
                            if (fragment.getInitializer() instanceof MethodInvocation) {
                                MethodInvocation initializer = (MethodInvocation) fragment.getInitializer();
                                if (fragmentToFind.getInvokedMethod().equals(initializer.getName().getIdentifier())) {
                                    handleVariableDeclarationStatementNode(fragment, fragmentToFind.getInvokedMethod());
                                }
                                
                            }
                        }  
                    }
                }
            }            
            return true;
        }
        
        
        // Initialized to class field is an ExpressionStatement node
        // TODO: Handle FieldAccess when using this.something
        @Override
        public boolean visit(ExpressionStatement node) {
            Expression expression = node.getExpression();
            if (expression instanceof Assignment) {
                Assignment assignment = (Assignment) expression;
                ITypeBinding localVariable = assignment.getLeftHandSide().resolveTypeBinding();
                for(NodeToFind localVariableToFind : nodesToFind) {
                    if (localVariableToFind.getType().equals(localVariable.getName())) {
                        handleAssignmentNode(assignment, localVariableToFind.getInvokedMethod());
                    }                    
                }
            }
            return true;
        }
        
        private void handleVariableDeclarationStatementNode(VariableDeclarationFragment fragment, String methodToFind) {
            SimpleName s = fragment.getName();
            IVariableBinding leftSide = (IVariableBinding) s.resolveBinding();
            IVariableBinding rightSide = null;
            if (fragment.getInitializer() == null) {
                return;
            }
            if (fragment.getInitializer() instanceof MethodInvocation) {
                // TODO: Need to navigate to the bottom of the method invocations?
                MethodInvocation initializer = (MethodInvocation) fragment.getInitializer();
                if (initializer.getExpression() instanceof SimpleName) {
                    SimpleName rightSideVariable = (SimpleName) initializer.getExpression();
                    if (rightSideVariable.resolveBinding() instanceof IVariableBinding) {
                        rightSide = (IVariableBinding) rightSideVariable.resolveBinding();
                    }
                }
                findAllMethodInvocationsForSimpleName(leftSide, initializer, methodToFind);
            }
            if (leftSide != null && rightSide != null) {
                assignmentMap.put(leftSide, rightSide);
            }
        }
        
        private void handleAssignmentNode(Assignment assignment, String methodToFind) {
            IVariableBinding leftSideName = null;
            IVariableBinding rightSideName = null;
            Expression leftSide = assignment.getLeftHandSide();
            if (leftSide instanceof SimpleName) {
                SimpleName s = (SimpleName) leftSide;
                leftSideName = (IVariableBinding) s.resolveBinding();
            } else if (leftSide instanceof FieldAccess) {
                FieldAccess field = (FieldAccess) leftSide;
                SimpleName s = field.getName();
                leftSideName = (IVariableBinding) s.resolveBinding();
            }
            Expression rightSide = assignment.getRightHandSide();
            if (rightSide instanceof MethodInvocation) {
                // TODO: Need to navigate to the bottom of the method invocations
                MethodInvocation method = (MethodInvocation) rightSide;
                Expression ex = method.getExpression();
                if (ex instanceof SimpleName) {
                    SimpleName rightSideVariable = (SimpleName) ex;
                    if (rightSideVariable.resolveBinding() instanceof IVariableBinding) {
                        rightSideName = (IVariableBinding) rightSideVariable.resolveBinding();
                    }
                } else if (ex instanceof FieldAccess) {
                    FieldAccess field = (FieldAccess) ex;
                    SimpleName s = field.getName();
                    rightSideName = (IVariableBinding) s.resolveBinding();
                }
                findAllMethodInvocationsForSimpleName(leftSideName, method, methodToFind);
            }
            if (leftSideName != null && rightSideName != null) {
                assignmentMap.put(leftSideName, rightSideName);
            }
        }
        
        private void findAllMethodInvocationsForSimpleName(IVariableBinding name, MethodInvocation invocation, String methodToFind) {
            IMethodBinding methodBinding = invocation.resolveMethodBinding();
            //ITypeBinding methodType = invocation.resolveTypeBinding();
            
               if(methodToFind.equals(methodBinding.getName())) {
                   addToInvocationMap(invocationMap, name, invocation);
                   lastInvokedOnLine.put(name, getLineNumber(invocation)); // TODO: Do something smart with this?
               }
            

//            if(invocation.getExpression() instanceof MethodInvocation) {
                //findAllMethodInvocationsForSimpleName(name, (MethodInvocation) invocation.getExpression());
//            }
        }
       
        private void addToInvocationMap(Map<IVariableBinding, List<MethodInvocation>> invocations,
                IVariableBinding binding, MethodInvocation newInvocation) {
            if (invocations.get(binding) == null) {
                List<MethodInvocation> c = new ArrayList<>();
                c.add(newInvocation);
                invocations.put(binding, c);
            } else {
                invocations.get(binding).add(newInvocation);
            }
        }
        
        public int getLineNumber(ASTNode node) {
            return ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
        }
    }
  
}
