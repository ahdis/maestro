package net.ihe.gazelle.report.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.StepRunReport;
import net.ihe.gazelle.report.validator.supplier.StepRunReportValidatorBuilderSupplier;

public class StepRunReportBuilderVisitor implements BuilderVisitor {

    public static final StepRunReportBuilderVisitor INSTANCE = new StepRunReportBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(StepRunReport.class, new StepRunReportValidatorBuilderSupplier(validatorBuilderFactory));
        ItemBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
    }
}
