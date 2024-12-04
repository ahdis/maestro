package net.ihe.gazelle.testreport.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.Test;
import net.ihe.gazelle.testreport.validator.supplier.TestValidatorBuilderSupplier;

public class TestBuilderVisitor implements BuilderVisitor {

    public static final TestBuilderVisitor INSTANCE = new TestBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(Test.class, new TestValidatorBuilderSupplier(validatorBuilderFactory));
    }
}
