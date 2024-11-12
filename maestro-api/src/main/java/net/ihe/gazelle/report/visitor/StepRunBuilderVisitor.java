package net.ihe.gazelle.report.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.StepRun;
import net.ihe.gazelle.report.validator.supplier.StepRunValidatorBuilderSupplier;

public class StepRunBuilderVisitor implements BuilderVisitor {

    public static final StepRunBuilderVisitor INSTANCE = new StepRunBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(StepRun.class, new StepRunValidatorBuilderSupplier(validatorBuilderFactory));
        ItemBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
    }
}
