package net.ihe.gazelle.maestro.recording;

import com.kereval.gazelle.datahouse.api.business.record.RefType;
import com.kereval.gazelle.datahouse.api.business.record.Reference;
import net.ihe.gazelle.maestro.api.business.recording.MaestroRefType;
import net.ihe.gazelle.maestro.api.business.recording.MaestroReference;

public class DatahouseMaestroReference implements MaestroReference {

    private final Reference reference;

    public DatahouseMaestroReference(Reference reference) {
        this.reference = reference;
    }

    public DatahouseMaestroReference(MaestroReference reference) {
        this.reference = new Reference();
        this.reference.setName(reference.getName());
        this.reference.setType(reference.getType());
        this.reference.setValue(reference.getValue());
        this.reference.setRefType(toDatahouseRefType(reference.getRefType()));
    }

    @Override
    public String getValue() {
        return reference.getValue();
    }

    @Override
    public String getType() {
        return reference.getType();
    }

    @Override
    public String getName() {
        return reference.getName();
    }

    @Override
    public MaestroRefType getRefType() {
        return switch (reference.getRefType()) {
            case ATTACHMENT -> MaestroRefType.ATTACHMENT;
            case URL -> MaestroRefType.URL;
            case ITEM_ID -> MaestroRefType.ITEM_ID;
        };
    }

    public Reference toDatahouseReference() {
        return reference;
    }


    public static RefType toDatahouseRefType(MaestroRefType refType) {
        return switch (refType) {
            case ATTACHMENT -> RefType.ATTACHMENT;
            case URL -> RefType.URL;
            case ITEM_ID -> RefType.ITEM_ID;
        };
    }

}
