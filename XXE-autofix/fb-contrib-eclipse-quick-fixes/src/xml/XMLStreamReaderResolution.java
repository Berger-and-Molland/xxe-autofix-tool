package xml;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;

public class XMLStreamReaderResolution extends AbstractInstanceTrackResolution {

    @Override
    public List<QuickFix> getQuickFixes(AST ast) {
        QuickFix quickFix1 = new QuickFix(ast.newSimpleName("setProperty"),
                Arrays.asList(ast.newQualifiedName(ast.newSimpleName("XMLInputFactory"),
                        ast.newSimpleName("SUPPORT_DTD")), ast.newBooleanLiteral(false)));
        
        QuickFix quickFix2 = new QuickFix(ast.newSimpleName("setProperty"),
                Arrays.asList(ast.newQualifiedName(ast.newSimpleName("XMLInputFactory"),
                ast.newSimpleName("IS_SUPPORTING_EXTERNAL_ENTITIES")), ast.newBooleanLiteral(false)));
       return Arrays.asList(quickFix1, quickFix2);
    }

    @Override
    public String[] importsToAdd() {
        return new String[0];
    }

    @Override
    public List<NodeToFind> addNodesToFind() {
        return Arrays.asList(
                new NodeToFind("XMLInputFactory", "newInstance", true),
                new NodeToFind("XMLInputFactory", "newFactory", true)
                );
    }

}
