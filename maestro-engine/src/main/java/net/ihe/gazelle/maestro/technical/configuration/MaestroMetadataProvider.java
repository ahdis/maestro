package net.ihe.gazelle.maestro.technical.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import net.ihe.gazelle.servicemetadata.api.technical.AbstractMetadataService;

@Default
@ApplicationScoped
public class MaestroMetadataProvider extends AbstractMetadataService {

    protected MaestroMetadataProvider() {
        super(MaestroMetadataProvider.class);
    }

    @Override
    public String getServiceName() {
        return "Maestro";
    }

    @Override
    public String getServiceDescription() {
        return "The Gazelle test automation engine";
    }
}
