package net.ihe.gazelle.report.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.TestService;
import net.ihe.gazelle.report.validator.supplier.TestServiceValidatorBuilderSupplier;

public class TestServiceBuilderVisitor implements BuilderVisitor {

    public static final TestServiceBuilderVisitor INSTANCE = new TestServiceBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(TestService.class, new TestServiceValidatorBuilderSupplier(validatorBuilderFactory));
        EntityIdentificationBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
    }
}
