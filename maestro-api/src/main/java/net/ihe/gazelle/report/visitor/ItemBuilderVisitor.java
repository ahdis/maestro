package net.ihe.gazelle.report.visitor;

import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.Item;
import net.ihe.gazelle.report.validator.supplier.ItemValidatorBuilderSupplier;

public class ItemBuilderVisitor implements BuilderVisitor {

    public static final ItemBuilderVisitor INSTANCE = new ItemBuilderVisitor();

    @Override
    public void visit(RootValidatorBuilder<?> rootValidatorBuilder, ValidatorBuilderFactory validatorBuilderFactory) {
        rootValidatorBuilder.addValidatorBuilder(Item.class, new ItemValidatorBuilderSupplier(validatorBuilderFactory));
    }
}
