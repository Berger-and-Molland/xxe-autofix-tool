package xml;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;

public class DocumentBuilderResolution extends AbstractInstanceTrackResolution {

    @Override
    public List<QuickFix> getQuickFixes(AST ast) {
        QuickFix quickFix1 = new QuickFix(ast.newSimpleName("setFeature"),
                Arrays.asList(ast.newQualifiedName(ast.newSimpleName("XMLConstants"),
                        ast.newSimpleName("FEATURE_SECURE_PROCESSING")), ast.newBooleanLiteral(true)));
       return Arrays.asList(quickFix1);
    }

    @Override
    public String[] importsToAdd() {
        String[] imports = {"javax.xml.XMLConstants"};
        return imports;
    }

    @Override
    public List<NodeToFind> addNodesToFind() {
        return Arrays.asList(
                new NodeToFind("DocumentBuilderFactory", "newInstance", true),
                new NodeToFind("DocumentBuilder", "newDocumentBuilder", false)
                );
    }

}
