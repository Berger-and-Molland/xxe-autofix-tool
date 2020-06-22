package quickfix;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.plugin.eclipse.quickfix.BugResolution;
import edu.umd.cs.findbugs.plugin.eclipse.quickfix.exception.BugResolutionException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import util.TraversalUtil;

import static edu.umd.cs.findbugs.plugin.eclipse.quickfix.util.ASTUtil.getASTNode;

public class XXEResolution extends BugResolution {

    @Override
    protected boolean resolveBindings() {
        return true;
    }

    @Override
    protected void repairBug(ASTRewrite rewrite, CompilationUnit workingUnit, BugInstance bug) throws BugResolutionException {
        ASTNode node = getASTNode(workingUnit, bug.getPrimarySourceLineAnnotation());
        XXEVisitor visitor = new XXEVisitor();
        Block parentBlock = TraversalUtil.findClosestAncestor(node, Block.class);
        parentBlock.accept(visitor);
       
        
        if (visitor.saxParser != null) {
            AST ast = node.getAST();
            MethodInvocation newMethod = ast.newMethodInvocation();
            newMethod.setExpression(ast.newSimpleName(visitor.saxParser.getIdentifier()));
            newMethod.setName(ast.newSimpleName("setFeature"));
            
            QualifiedName xmlConstant = ast.newQualifiedName(ast.newSimpleName("XMLConstants"), ast.newSimpleName("FEATURE_SECURE_PROCESSING"));
            newMethod.arguments().add(xmlConstant);
            newMethod.arguments().add(ast.newBooleanLiteral(true));
            ListRewrite statements = rewrite.getListRewrite(parentBlock, Block.STATEMENTS_PROPERTY);
            statements.insertAfter(ast.newExpressionStatement(newMethod), visitor.saxParserNode, null);         
        }
    }
    
    private static class XXEVisitor extends ASTVisitor {
        public SimpleName saxParser;
        public VariableDeclarationStatement saxParserNode;

        @Override
        public boolean visit(VariableDeclarationFragment node) {
            if (node.getInitializer() instanceof MethodInvocation) {
                MethodInvocation initializer = (MethodInvocation) node.getInitializer();
                Expression initializerExpression = (Expression) initializer.getExpression();
                if (initializer != null && initializerExpression != null) {
                    String saxInitializer = String.format("%s.%s", initializerExpression.resolveTypeBinding().getName(), initializer.getName());
                    if("SAXParserFactory.newInstance".equals(saxInitializer)) {
                        saxParser = node.getName();
                        saxParserNode = TraversalUtil.findClosestAncestor(node, VariableDeclarationStatement.class);
                        return false;
                    }
                }
            }
            return true;
        }
    }

}
