package net.ihe.gazelle.report.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.TestRun;
import net.ihe.gazelle.report.validator.supplier.TestRunValidatorBuilderSupplier;

public class TestRunBuilderVisitor implements BuilderVisitor {

    public static final TestRunBuilderVisitor INSTANCE = new TestRunBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(TestRun.class, new TestRunValidatorBuilderSupplier(validatorBuilderFactory));
        TestBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
        ItemBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
        StepRunBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
        UnexpectedErrorBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
    }
}
