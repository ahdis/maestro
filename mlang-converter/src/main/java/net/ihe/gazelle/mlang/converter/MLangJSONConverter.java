package net.ihe.gazelle.mlang.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kereval.mlang.converter.ConverterException;
import com.kereval.mlang.converter.FromMLangConverter;
import com.kereval.mlang.converter.ParserException;
import com.kereval.mlang.parser.MLANGParser;
import com.kereval.mlang.parser.MLANGParserBaseVisitor;
import net.ihe.gazelle.model.*;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class MLangJSONConverter extends FromMLangConverter {
    @Override
    public void convert(MLANGParser.TestFileContext testFileContext, OutputStreamWriter outputStreamWriter) throws ConverterException, ParserException {
        MLangJSONVisitor visitor = new MLangJSONVisitor();
        TestCaseDTO testCase = visitor.build(testFileContext);

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(outputStreamWriter, testCase);
        } catch (IOException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    private class MLangJSONVisitor extends MLANGParserBaseVisitor<Object> {
        private TestCaseDTO m_testCase;
        private int stepID;

        private MLANGParser.Transaction_stmtContext currentTransaction = null;


        public TestCaseDTO build(MLANGParser.TestFileContext testFile) {
            m_testCase = new TestCaseDTO();

            testFile.accept(this);

            return m_testCase;
        }

        private int getStepID() {
            return stepID++;
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
        public Object visitTransaction_stmt(MLANGParser.Transaction_stmtContext ctx) {
            InstructionStepDTO step = new InstructionStepDTO();
            step.setId("Transaction preparation (Step " + this.getStepID() + ")");
            String description = "Transaction preparation " + ctx.ID().getText() +
                    " from " + ctx.from().ID().getText() +
                    " to " + ctx.to().ID().getText();
            step.setDescription(description);
            m_testCase.addStep(step);
            MLANGParser.Transaction_stmtContext save = this.currentTransaction;
            this.currentTransaction = ctx;
            Object ret = super.visitTransaction_stmt(ctx);
            this.currentTransaction = save;
            return ret;
        }

        @Override
        public Object visitSend_message(MLANGParser.Send_messageContext ctx) {
            InstructionStepDTO step = new InstructionStepDTO();
            step.setId("Send message (Step " + this.getStepID() + ")");
            StringBuilder builder = new StringBuilder();

            builder.append("Sending message ").append(ctx.ID().getText())
                    .append(" from ").append(this.currentTransaction.from().ID().getText())
                    .append(" to ").append(this.currentTransaction.to().ID().getText());
            if (ctx.send_message_param() != null) {
                builder.append(" with parameters :\n");
                for (int i = 0 ; i < ctx.send_message_param().value_name.size() ; i++) {
                    String name = ctx.send_message_param().value_name.get(i).getText();
                    String value = ctx.send_message_param().value.get(i).getText();
                    if (value.length() <= 2) {
                        builder.append("  UNDEFINEDVALUE as ").append(name);
                    }
                    else {
                        builder.append("  ").append(value, 1, value.length() - 1).append(" as ").append(name);
                    }
                }

                for (int i = 0 ; i < ctx.send_message_param().variable_name.size() ; i++) {
                    String name = ctx.send_message_param().variable_name.get(i).getText();
                    String value = ctx.send_message_param().variable.get(i).getText();
                    if (value.isEmpty()) {
                        builder.append("  UNDEFINEDVALUE as ").append(name);
                    }
                    else {
                        builder.append("  ").append(value).append(" as ").append(name);
                    }
                }
            }

            if (ctx.storein() != null) {
                builder.append("\n");
                builder.append("Message sent stored in ").append(ctx.storein().name.getText());
            }

            step.setDescription(builder.toString());
            m_testCase.addStep(step);
            return super.visitSend_message(ctx);
        }

        @Override
        public Object visitReceive_message(MLANGParser.Receive_messageContext ctx) {
            InstructionStepDTO step = new InstructionStepDTO();
            step.setId("Waiting message (Step " + this.getStepID() + ")");
            StringBuilder builder = new StringBuilder();

            builder.append("Waiting message ").append(ctx.ID().getText())
                    .append(" from ").append(this.currentTransaction.from().ID().getText());
            if (ctx.storein() != null) {
                builder.append(" and stored it in ").append(ctx.storein().name.getText());
            }

            step.setDescription(builder.toString());
            m_testCase.addStep(step);
            return super.visitReceive_message(ctx);
        }

        @Override
        public Object visitAssertValid(MLANGParser.AssertValidContext ctx) {
            ValidationStepDTO step = new ValidationStepDTO();
            step.setId("Assertion valid (Step " + this.getStepID() + ")");

            step.addProperty(buildPropertyDTO("validationService", ctx.serviceName.getText()));
            step.addProperty(buildPropertyDTO("validationProfile", deleteMLangStringQuote(ctx.profileName.getText())));

            StringBuilder description = new StringBuilder("This step will validate");
            for (var param : ctx.assertParams) {
                description.append(" ").append(param.ID().getText());
                step.addProperty(buildPropertyDTO("contentToValidate", "${" + param.ID().getText() + "}"));

            }
            description.append(" against the validator ").append(ctx.serviceName.getText()).append(" with the profile ").append(deleteMLangStringQuote(ctx.profileName.getText()));
            step.setDescription(description.toString());

            m_testCase.addStep(step);
            return super.visitAssertValid(ctx);
        }

        @Override
        public Object visitProcess_stmt(MLANGParser.Process_stmtContext ctx) {
            InstructionStepDTO step = new InstructionStepDTO();
            step.setId("Processing data (Step " + this.getStepID() + ")");

            StringBuilder builder = new StringBuilder();
            builder.append("Processing data ").append(ctx.data.getText())
                    .append(" with processing service ").append(ctx.processingService.getText());

            if (ctx.send_message_param() != null) {
                builder.append(" with parameters :\n");
                for (int i = 0 ; i < ctx.send_message_param().value_name.size() ; i++) {
                    String name = ctx.send_message_param().value_name.get(i).getText();
                    String value = ctx.send_message_param().value.get(i).getText();
                    if (value.length() <= 2) {
                        builder.append("  UNDEFINEDVALUE as ").append(name);
                    }
                    else {
                        builder.append("  ").append(value, 1, value.length() - 1).append(" as ").append(name);
                    }
                }

                for (int i = 0 ; i < ctx.send_message_param().variable_name.size() ; i++) {
                    String name = ctx.send_message_param().variable_name.get(i).getText();
                    String value = ctx.send_message_param().variable.get(i).getText();
                    if (value.isEmpty()) {
                        builder.append("  UNDEFINEDVALUE as ").append(name);
                    }
                    else {
                        builder.append("  ").append(value).append(" as ").append(name);
                    }
                }
            }

            if (ctx.storein() != null) {
                builder.append("\n");
                builder.append("Result stored it in ").append(ctx.storein().name.getText());
            }
            step.setDescription(builder.toString());
            m_testCase.addStep(step);
            return super.visitProcess_stmt(ctx);
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
