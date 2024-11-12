package net.ihe.gazelle.report.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.TestReport;
import net.ihe.gazelle.report.validator.supplier.TestReportValidatorBuilderSupplier;

public class TestReportBuilderVisitor implements BuilderVisitor {

    public static final TestReportBuilderVisitor INSTANCE = new TestReportBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(TestReport.class, new TestReportValidatorBuilderSupplier(validatorBuilderFactory));
        TestServiceBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
        SystemUnderTestBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
        TestCountersBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
        //TestReportBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
        TestRunBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
        UnexpectedErrorBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
    }
}
