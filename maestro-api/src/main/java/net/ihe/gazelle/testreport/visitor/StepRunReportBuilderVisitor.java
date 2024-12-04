package net.ihe.gazelle.testreport.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.StepRunReport;
import net.ihe.gazelle.testreport.validator.supplier.StepRunReportValidatorBuilderSupplier;

public class StepRunReportBuilderVisitor implements BuilderVisitor {

    public static final StepRunReportBuilderVisitor INSTANCE = new StepRunReportBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(StepRunReport.class, new StepRunReportValidatorBuilderSupplier(validatorBuilderFactory));
        ItemBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
        UnexpectedErrorBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
    }
}
