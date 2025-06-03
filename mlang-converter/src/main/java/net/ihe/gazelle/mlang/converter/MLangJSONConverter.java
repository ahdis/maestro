package net.ihe.gazelle.mlang.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kereval.mlang.parser.MLANGLexer;
import com.kereval.mlang.parser.MLANGParser;
import com.kereval.mlang.parser.MLANGParserBaseVisitor;
import net.ihe.gazelle.model.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MLangJSONConverter implements Converter {
    @Override
    public void convert(InputStreamReader is, OutputStreamWriter os) throws ConverterException, ParserException {
        try {
            CharStream charStream = CharStreams.fromReader(is);
            MLANGLexer lexer = new MLANGLexer(charStream);
            MLANGParser parser = new MLANGParser(new CommonTokenStream(lexer));

            SyntaxErrorListener errorListener = new SyntaxErrorListener();
            parser.addErrorListener(errorListener);

            MLangJSONVisitor visitor = new MLangJSONVisitor();
            TestCaseDTO testCase = visitor.build(parser.testFile());

            if (!errorListener.getSyntaxErrors().isEmpty()) {
                throw new ParserException(errorListener.getSyntaxErrors());
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(os, testCase);
        } catch (IOException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    private class MLangJSONVisitor extends MLANGParserBaseVisitor<Object> {
        private TestCaseDTO m_testCase;
        private int stepID;

        public TestCaseDTO build(MLANGParser.TestFileContext testFile) {
            m_testCase = new TestCaseDTO();

            testFile.accept(this);

            return m_testCase;
        }

        @Override
        public Object visitTest(MLANGParser.TestContext ctx) {
            m_testCase.setName(ctx.ID().getText());

            return super.visitTest(ctx);
        }

        @Override
        public Object visitTest_comments(MLANGParser.Test_commentsContext ctx) {
            StringBuilder builder = new StringBuilder();
            if (!ctx.comments().isEmpty()) {
                String str = ctx.comments(0).STRING().getText();
                if (str.length() > 2) {
                    builder.append(str, 1, str.length() - 1);
                }
            }
            m_testCase.setDescription(builder.toString());
            return super.visitTest_comments(ctx);
        }

        @Override
        public Object visitRequire_file(MLANGParser.Require_fileContext ctx) {
            InputDTO fileInput = new InputDTO();
            fileInput.setKey(ctx.ID().getText());
            fileInput.setLabel(ctx.ID().getText());
            fileInput.setFormat("JSON");
            fileInput.setType("file");
            fileInput.setRequired(true);

            m_testCase.addInputs(fileInput);
            return super.visitRequire_file(ctx);
        }

        @Override
        public Object visitStmt(MLANGParser.StmtContext ctx) {
            if (ctx.NEWLINE() != null)
                stepID++;
            return super.visitStmt(ctx);
        }

        @Override
        public Object visitAssertValid(MLANGParser.AssertValidContext ctx) {
            ValidationStepDTO step = new ValidationStepDTO();
            step.setId("Assertion valid (Step " + stepID + ")");

            step.addProperty(buildPropertyDTO("validationService", ctx.serviceName.getText()));
            step.addProperty(buildPropertyDTO("validationProfile", deleteMLangStringQuote(ctx.profileName.getText())));

            StringBuilder description = new StringBuilder(("this step will validate"));
            for (var param : ctx.assertParams) {
                description.append(" ").append(param.ID().getText());
                step.addProperty(buildPropertyDTO("contentToValidate", "${" + param.ID().getText() + "}"));

            }
            description.append(" against the validator ").append(ctx.serviceName.getText()).append(" with the profile ").append(deleteMLangStringQuote(ctx.profileName.getText()));
            step.setDescription(description.toString());

            m_testCase.addStep(step);
            return super.visitAssertValid(ctx);
        }

        private static PropertyDTO buildPropertyDTO(String key, String value) {
            PropertyDTO property = new PropertyDTO();
            property.setKey(key);
            property.setValue(value);
            return property;
        }

        private static String deleteMLangStringQuote(String str) {
            if (str.startsWith("\"") && str.endsWith("\"")) {
                return str.substring(1, str.length() - 1);
            }
            else {
                return str;
            }
        }
    }
}
