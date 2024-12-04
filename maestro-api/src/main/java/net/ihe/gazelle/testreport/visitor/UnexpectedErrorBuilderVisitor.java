package net.ihe.gazelle.testreport.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.UnexpectedError;
import net.ihe.gazelle.testreport.validator.supplier.UnexpectedErrorValidatorBuilderSupplier;

public class UnexpectedErrorBuilderVisitor implements BuilderVisitor {

    public static final UnexpectedErrorBuilderVisitor INSTANCE = new UnexpectedErrorBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(UnexpectedError.class, new UnexpectedErrorValidatorBuilderSupplier(validatorBuilderFactory));
    }
}
