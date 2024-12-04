package net.ihe.gazelle.testreport.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.Item;
import net.ihe.gazelle.testreport.validator.supplier.ItemValidatorBuilderSupplier;

public class ItemBuilderVisitor implements BuilderVisitor {

    public static final ItemBuilderVisitor INSTANCE = new ItemBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(Item.class, new ItemValidatorBuilderSupplier(validatorBuilderFactory));
    }
}
