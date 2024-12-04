package net.ihe.gazelle.testreport.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.SystemUnderTest;
import net.ihe.gazelle.testreport.validator.supplier.SystemUnderTestValidatorBuilderSupplier;

public class SystemUnderTestBuilderVisitor implements BuilderVisitor {

    public static final SystemUnderTestBuilderVisitor INSTANCE = new SystemUnderTestBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(SystemUnderTest.class, new SystemUnderTestValidatorBuilderSupplier(validatorBuilderFactory));
        EntityIdentificationBuilderVisitor.INSTANCE.visit(rootValidatorBuilder, validatorBuilderFactory);
    }
}
