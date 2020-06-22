package quickfix;
import static edu.umd.cs.findbugs.plugin.eclipse.quickfix.util.ASTUtil.getASTNode;
import static edu.umd.cs.findbugs.plugin.eclipse.quickfix.util.ASTUtil.getMethodDeclaration;
import static edu.umd.cs.findbugs.plugin.eclipse.quickfix.util.ASTUtil.getTypeDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import util.TraversalUtil;

public class SAXParserResolution extends BugResolution {

    @Override
    protected boolean resolveBindings() {
        return true;
    }

    @Override
    protected void repairBug(ASTRewrite rewrite, CompilationUnit workingUnit, BugInstance bug) throws BugResolutionException {
        ASTNode node = getASTNode(workingUnit, bug.getPrimarySourceLineAnnotation());
        AST ast = node.getAST();
//        TypeDeclaration type = getTypeDeclaration(workingUnit, bug.getPrimaryClass());
//        SAXParserVisitor foo = new SAXParserVisitor();
//        workingUnit.accept(foo);

        
//        MethodDeclaration method = getMethodDeclaration(type, bug.getPrimaryMethod());
//        FieldDeclaration[] classFields = type.getFields();
        
        // Get variable which the saxparser is initialized to
        SimpleName parserVariable = getParserName(node);
        
        
        // TODO: For parser fields, need to handle ALL methods the parser is initialized in
        // TODO: Need to fix False positive in detection
        IVariableBinding b = (IVariableBinding) parserVariable.resolveBinding();
        System.out.println(b.isField());
        SAXParserVisitor typeVisitor = new SAXParserVisitor();
        type.accept(typeVisitor);
        VariableDeclarationFragment saxParserr = typeVisitor.getSaxParserNode(parserVariable);
        
        SAXParserVisitor visitor = new SAXParserVisitor();
        Block parentBlock = TraversalUtil.findClosestAncestor(node, Block.class);
        parentBlock.accept(visitor);

        // Get the SAXParser declaration (case 2, 5, 6)
        VariableDeclarationFragment saxParser = visitor.getSaxParserNode(parserVariable);
        // Case 2, 5, 6
        if (saxParser != null) {
            MethodInvocation in = (MethodInvocation)saxParser.getInitializer();
            Expression e = in.getExpression();
            // Case 6
            if (e instanceof MethodInvocation) {
                MethodInvocation newSaxParser = (MethodInvocation) e;
                Expression f = newSaxParser.getExpression();
                VariableDeclarationStatement st = (VariableDeclarationStatement) saxParser.getParent();
                SimpleType t = (SimpleType) st.getType();
                rewrite.replace(t, createSAXParserFactoryType(ast), null);
                rewrite.replace(in, newSaxParser, null);
                rewrite.replace(saxParser.getName(), ast.newSimpleName("secureFactory"), null);
                MethodInvocation secureFactory = setSecureFactoryFeatures(ast, "secureFactory");
                ListRewrite statements = rewrite.getListRewrite(parentBlock, Block.STATEMENTS_PROPERTY);
                statements.insertAfter(createSaxParser(ast, saxParser.getName(), rewrite), st, null);
                statements.insertAfter(ast.newExpressionStatement(secureFactory), st, null);
            } 
            // Case 2 and 5
            if (e instanceof SimpleName) {
                SimpleName factoryVariable = getFactoryName(saxParser);
                // Case 5
                if (factoryVariable != null) {
                    VariableDeclarationFragment saxParserFactory = visitor.getSaxParserFactoryNode(factoryVariable);
                    if (saxParserFactory != null) {
                        MethodInvocation secureFactory = setSecureFactoryFeatures(ast, saxParserFactory.getName().getIdentifier());
                        ListRewrite statements = rewrite.getListRewrite(parentBlock, Block.STATEMENTS_PROPERTY);
                        statements.insertAfter(ast.newExpressionStatement(secureFactory), saxParserFactory.getParent(), null);                        
                    } 
                    // case 2
                    else {
                        // The factory is initialized into a class field
                        Assignment saxParserFactoryAssignment = visitor.getSaxParserFactoryAssignmentNode(factoryVariable);
                        MethodInvocation secureFactory = setSecureFactoryFeatures(ast, ((SimpleName)saxParserFactoryAssignment.getLeftHandSide()).getIdentifier());
                        ListRewrite statements = rewrite.getListRewrite(parentBlock, Block.STATEMENTS_PROPERTY);
                        statements.insertAfter(ast.newExpressionStatement(secureFactory), saxParserFactoryAssignment.getParent(), null);
                    }

                }
            }
        } 
        // Get the SAXParser assignment (case 1, 3, 4)
        Assignment saxParserAssignment = visitor.getSaxParserAssignmentNode(parserVariable);
        // Case 1, 3, 4
        if (saxParserAssignment != null) {
            MethodInvocation in = (MethodInvocation)saxParserAssignment.getRightHandSide();
            Expression e = in.getExpression();
            // Case 4
            if (e instanceof MethodInvocation) {
             // Create a factory
              VariableDeclarationFragment fr = ast.newVariableDeclarationFragment();
              fr.setName(ast.newSimpleName("secureFactory"));
              fr.setInitializer((Expression) rewrite.createCopyTarget(e));
              
              VariableDeclarationStatement st = ast.newVariableDeclarationStatement(fr);
              st.setType(createSAXParserFactoryType(ast));
              ListRewrite statements = rewrite.getListRewrite(parentBlock, Block.STATEMENTS_PROPERTY);
              statements.insertBefore(st, saxParserAssignment.getParent(), null);
              
              // Make the factory secure
              MethodInvocation secureFactory = setSecureFactoryFeatures(ast, "secureFactory");
              statements.insertAfter(ast.newExpressionStatement(secureFactory), st, null);
              
              // Initialize the parser using the secure factory
              Assignment secureSaxParserAssignment = ast.newAssignment();
              secureSaxParserAssignment.setLeftHandSide((Expression) rewrite.createCopyTarget(saxParserAssignment.getLeftHandSide()));
              MethodInvocation newSecureSaxParser = ast.newMethodInvocation();
              newSecureSaxParser.setExpression(ast.newSimpleName("secureFactory"));
              newSecureSaxParser.setName(ast.newSimpleName("newSAXParser"));
              secureSaxParserAssignment.setRightHandSide(newSecureSaxParser);
              
              // Replace the insecure assignment with the secure one
              rewrite.replace(saxParserAssignment, secureSaxParserAssignment, null);
            }
            // Case 1 and 3
            if (e instanceof SimpleName) {
                SimpleName factoryVariable = getFactoryName(saxParserAssignment);
                if (factoryVariable != null) {
                    VariableDeclarationFragment saxParserFactory = visitor.getSaxParserFactoryNode(factoryVariable);
                    // Case 1
                    if (saxParserFactory != null) {
                        MethodInvocation secureFactory = setSecureFactoryFeatures(ast, saxParserFactory.getName().getIdentifier());
                        ListRewrite statements = rewrite.getListRewrite(parentBlock, Block.STATEMENTS_PROPERTY);
                        statements.insertAfter(ast.newExpressionStatement(secureFactory), saxParserFactory.getParent(), null);                        
                    } 
                    // case 3
                    else {
                        // The factory is initialized into a class field
                        Assignment saxParserFactoryAssignment = visitor.getSaxParserFactoryAssignmentNode(factoryVariable);
                        MethodInvocation secureFactory = setSecureFactoryFeatures(ast, ((SimpleName)saxParserFactoryAssignment.getLeftHandSide()).getIdentifier());
                        ListRewrite statements = rewrite.getListRewrite(parentBlock, Block.STATEMENTS_PROPERTY);
                        statements.insertAfter(ast.newExpressionStatement(secureFactory), saxParserFactoryAssignment.getParent(), null);
                    }
                }
            }
        }
        
        // Case 7
        
    }
    
    private SimpleName getParserName(ASTNode node) {
        if (node instanceof ExpressionStatement) {
            ExpressionStatement exp = (ExpressionStatement) node;
            Expression ex = exp.getExpression();
            if (ex instanceof MethodInvocation) {
                MethodInvocation in = (MethodInvocation) ex;
                SimpleName name = (SimpleName) in.getExpression();
                return name;                
            }
            
        }
        return null;
    }
    
    private SimpleType createSAXParserFactoryType(AST ast) {
        SimpleType t = ast.newSimpleType(ast.newSimpleName("SAXParserFactory"));
        return t;
    }

    private MethodInvocation setSecureFactoryFeatures(AST ast, String factoryVariableName) {
        MethodInvocation newMethod = ast.newMethodInvocation();
        newMethod.setExpression(ast.newSimpleName(factoryVariableName));
        newMethod.setName(ast.newSimpleName("setFeature"));
        
        QualifiedName xmlConstant = ast.newQualifiedName(ast.newSimpleName("XMLConstants"), ast.newSimpleName("FEATURE_SECURE_PROCESSING"));
        newMethod.arguments().add(xmlConstant);
        newMethod.arguments().add(ast.newBooleanLiteral(true));
        return newMethod;
    }

    private VariableDeclarationStatement createSaxParser(AST ast, SimpleName saxParser, ASTRewrite rewrite) {
        MethodInvocation initializer = ast.newMethodInvocation();
        initializer.setExpression(ast.newSimpleName("secureFactory"));
        initializer.setName(ast.newSimpleName("newSAXParser"));
        VariableDeclarationFragment f = ast.newVariableDeclarationFragment();
        f.setName((SimpleName) rewrite.createCopyTarget(saxParser));
        f.setInitializer(initializer);
        VariableDeclarationStatement s = ast.newVariableDeclarationStatement(f);
        SimpleType saxParserType = ast.newSimpleType(ast.newName("SAXParser"));
        s.setType(saxParserType);
        return s;
    }
    
    private SimpleName getFactoryName(VariableDeclarationFragment saxParser) {
        MethodInvocation saxParserInitializer = (MethodInvocation) saxParser.getInitializer();
        if (saxParserInitializer.getExpression() instanceof SimpleName) {
            SimpleName ex = (SimpleName) saxParserInitializer.getExpression();
            return ex;
            
        }
        return null;
    }
    
    private SimpleName getFactoryName(Assignment saxParserAssignment) {
        MethodInvocation in = (MethodInvocation) saxParserAssignment.getRightHandSide();
        if (in.getExpression() instanceof SimpleName) {
            SimpleName ex = (SimpleName) in.getExpression();
            return ex;
        }
        return null;
    }
    
    private static class SAXParserVisitor extends ASTVisitor {
        public List<VariableDeclarationFragment> saxFragment = new ArrayList<VariableDeclarationFragment>();
        public List<VariableDeclarationFragment> saxParserFactoryFragment = new ArrayList<VariableDeclarationFragment>();
        
        public List<Assignment> saxParserAssignment = new ArrayList<Assignment>();
        public List<Assignment> saxParserFactoryAssignment = new ArrayList<Assignment>();
        
        @Override
        public boolean visit(VariableDeclarationStatement node) {
            Type type = node.getType();
            if (type instanceof SimpleType) {
                String qualifiedTypeName = ((SimpleType) type).getName().getFullyQualifiedName();
                if("SAXParser".equals(qualifiedTypeName)) {
                    List args = node.fragments();
                    if (args.size() == 1 && args.get(0) instanceof VariableDeclarationFragment) {
                        VariableDeclarationFragment f = (VariableDeclarationFragment) args.get(0);
                        this.saxFragment.add(f);
                    }
                }
                if("SAXParserFactory".equals(qualifiedTypeName)) {
                    List args = node.fragments();
                    if (args.size() == 1 && args.get(0) instanceof VariableDeclarationFragment) {
                        VariableDeclarationFragment f = (VariableDeclarationFragment) args.get(0);
                        this.saxParserFactoryFragment.add(f);                            
                    }
                }
            }            
            return true;
        }
        
        @Override
        public boolean visit(ExpressionStatement node) {
            Expression ex = node.getExpression();
            if (ex instanceof Assignment) {
                Assignment as = (Assignment) ex;
                ITypeBinding localVariable = as.getLeftHandSide().resolveTypeBinding();
                if ("SAXParserFactory".equals(localVariable.getName())) {
                    saxParserFactoryAssignment.add(as);
                }
                if ("SAXParser".equals(localVariable.getName())) {
                    saxParserAssignment.add(as);
                }
            }
            return true;
        }
        
        public VariableDeclarationFragment getSaxParserNode(final SimpleName parserVariable) {
            VariableDeclarationFragment fragment = saxFragment.stream().filter(new Predicate<VariableDeclarationFragment>() {
                @Override
                public boolean test(VariableDeclarationFragment x) {
                    return x.getName().getFullyQualifiedName().equals(parserVariable.getFullyQualifiedName());
                }
            }).findAny().orElse(null);
            return fragment;
        }
        
        public VariableDeclarationFragment getSaxParserFactoryNode(final SimpleName factoryVariable) {
            VariableDeclarationFragment fragment = saxParserFactoryFragment.stream().filter(new Predicate<VariableDeclarationFragment>() {
                @Override
                public boolean test(VariableDeclarationFragment x) {
                    return x.getName().getFullyQualifiedName().equals(factoryVariable.getFullyQualifiedName());
                }
            }).findAny().orElse(null);
            return fragment;
        }
        
       public Assignment getSaxParserAssignmentNode(final SimpleName parserVariable) {
           Assignment assignment = saxParserAssignment.stream().filter(new Predicate<Assignment>() {
            @Override
            public boolean test(Assignment t) {
                if (t.getLeftHandSide() instanceof SimpleName) {
                    SimpleName parser = (SimpleName) t.getLeftHandSide();
                    
                    return parser.getIdentifier().equals(parserVariable.getFullyQualifiedName());
                }
                return false;
            }
           }).findAny().orElse(null);
           return assignment;
       }
       
       public Assignment getSaxParserFactoryAssignmentNode(final SimpleName factoryVariable) {
           Assignment assignment = saxParserFactoryAssignment.stream().filter(new Predicate<Assignment>() {
            @Override
            public boolean test(Assignment t) {
                if (t.getLeftHandSide() instanceof SimpleName) {
                    SimpleName parser = (SimpleName) t.getLeftHandSide();
                    
                    return parser.getIdentifier().equals(factoryVariable.getFullyQualifiedName());
                }
                return false;
            }
           }).findAny().orElse(null);
           return assignment;
       }
    }
}
