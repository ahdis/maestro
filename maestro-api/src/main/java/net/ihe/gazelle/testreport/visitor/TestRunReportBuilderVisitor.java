package net.ihe.gazelle.testreport.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.TestRunReport;
import net.ihe.gazelle.testreport.validator.supplier.TestRunReportValidatorBuilderSupplier;

public class TestRunReportBuilderVisitor implements BuilderVisitor {

    public static final TestRunReportBuilderVisitor INSTANCE = new TestRunReportBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(TestRunReport.class, new TestRunReportValidatorBuilderSupplier(validatorBuilderFactory));
        TestBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
        ItemBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
        StepRunReportBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
        UnexpectedErrorBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
    }
}
