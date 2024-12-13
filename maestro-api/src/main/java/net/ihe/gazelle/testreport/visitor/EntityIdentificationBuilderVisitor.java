package net.ihe.gazelle.testreport.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.EntityIdentification;
import net.ihe.gazelle.testreport.validator.supplier.EntityIdentificationValidatorBuilderSupplier;

public class EntityIdentificationBuilderVisitor implements BuilderVisitor {

    public static final EntityIdentificationBuilderVisitor INSTANCE = new EntityIdentificationBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(EntityIdentification.class, new EntityIdentificationValidatorBuilderSupplier(validatorBuilderFactory));
    }
}
