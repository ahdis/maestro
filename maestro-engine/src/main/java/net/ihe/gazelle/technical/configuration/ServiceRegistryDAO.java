package net.ihe.gazelle.technical.configuration;

import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;

import java.util.List;

public interface ServiceRegistryDAO {

    List<Service> loadFile();

}
