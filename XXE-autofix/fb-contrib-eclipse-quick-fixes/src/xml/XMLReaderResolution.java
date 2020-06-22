package xml;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.StringLiteral;

public class XMLReaderResolution extends AbstractInstanceTrackResolution {

    @Override
    public List<NodeToFind> addNodesToFind() {
        return Arrays.asList(new NodeToFind("XMLReader", "createXMLReader", true));
    }

    @Override
    public List<QuickFix> getQuickFixes(AST ast) {
        StringLiteral disallowDoctypeDecl = ast.newStringLiteral();
        disallowDoctypeDecl.setLiteralValue("http://apache.org/xml/features/disallow-doctype-decl");
        StringLiteral loadExternalDtd = ast.newStringLiteral();
        loadExternalDtd.setLiteralValue("http://apache.org/xml/features/nonvalidating/load-external-dtd");
        StringLiteral externalGeneralEntities = ast.newStringLiteral();
        externalGeneralEntities.setLiteralValue("http://xml.org/sax/features/external-general-entities");
        StringLiteral externalParameterEntities = ast.newStringLiteral();
        externalParameterEntities.setLiteralValue("http://xml.org/sax/features/external-parameter-entities");
        QuickFix quickFix1 = new QuickFix(ast.newSimpleName("setFeature"),
                Arrays.asList(disallowDoctypeDecl, ast.newBooleanLiteral(true)));
        QuickFix quickFix2 = new QuickFix(ast.newSimpleName("setFeature"),
                Arrays.asList(loadExternalDtd, ast.newBooleanLiteral(false)));
        QuickFix quickFix3 = new QuickFix(ast.newSimpleName("setFeature"),
                Arrays.asList(externalGeneralEntities, ast.newBooleanLiteral(false)));
        QuickFix quickFix4 = new QuickFix(ast.newSimpleName("setFeature"),
                Arrays.asList(externalParameterEntities, ast.newBooleanLiteral(false)));
       return Arrays.asList(quickFix1, quickFix2, quickFix3, quickFix4);
    }

    @Override
    public String[] importsToAdd() {
        return new String[0];
    }

}
