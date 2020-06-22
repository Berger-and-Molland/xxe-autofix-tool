package quickfix;

import static edu.umd.cs.findbugs.plugin.eclipse.quickfix.util.ASTUtil.getASTNode;

import java.util.List;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.plugin.eclipse.quickfix.BugResolution;
import edu.umd.cs.findbugs.plugin.eclipse.quickfix.exception.BugResolutionException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import util.TraversalUtil;

public class XpathExpressionResolution extends BugResolution {

@Override
    protected boolean resolveBindings() {
        return true;
    }

    @Override
    protected void repairBug(ASTRewrite rewrite, CompilationUnit workingUnit, BugInstance bug) throws BugResolutionException {
        ASTNode node = getASTNode(workingUnit, bug.getPrimarySourceLineAnnotation());
        DocumentBuilderVisitor visitor = new DocumentBuilderVisitor();
        Block parentBlock = TraversalUtil.findClosestAncestor(node, Block.class);
        parentBlock.accept(visitor);
        
        if (visitor.documentParser != null) {
            AST ast = node.getAST();
            MethodInvocation newMethod = ast.newMethodInvocation();
            newMethod.setExpression(ast.newSimpleName(visitor.documentParser.getIdentifier()));
            newMethod.setName(ast.newSimpleName("setFeature"));
            
            QualifiedName xmlConstant = ast.newQualifiedName(ast.newSimpleName("XMLConstants"), ast.newSimpleName("FEATURE_SECURE_PROCESSING"));
            newMethod.arguments().add(xmlConstant);
            newMethod.arguments().add(ast.newBooleanLiteral(true));
            ListRewrite statements = rewrite.getListRewrite(parentBlock, Block.STATEMENTS_PROPERTY);
            statements.insertAfter(ast.newExpressionStatement(newMethod), visitor.documentParserNode, null);         
        }
    }
    
    private static class DocumentBuilderVisitor extends ASTVisitor {
        public SimpleName documentParser;
        public VariableDeclarationStatement documentParserNode;

        @Override
        public boolean visit(VariableDeclarationStatement node) {
            Type type = node.getType();
            if (type instanceof SimpleType && "DocumentBuilderFactory".equals(((SimpleType) type).getName().getFullyQualifiedName())) {
                List args = node.fragments();
                if (args.size() == 1 && args.get(0) instanceof VariableDeclarationFragment) {
                    VariableDeclarationFragment f = (VariableDeclarationFragment) args.get(0);
                    this.documentParser = f.getName();
                    this.documentParserNode = node;
                    return false;
                }
            }
            return true;
        }
    }

}
