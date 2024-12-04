package net.ihe.gazelle.testreport.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.TestCounters;
import net.ihe.gazelle.testreport.validator.supplier.TestCountersValidatorBuilderSupplier;

public class TestCountersBuilderVisitor implements BuilderVisitor {

    public static final TestCountersBuilderVisitor INSTANCE = new TestCountersBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(TestCounters.class, new TestCountersValidatorBuilderSupplier(validatorBuilderFactory));
    }
}
