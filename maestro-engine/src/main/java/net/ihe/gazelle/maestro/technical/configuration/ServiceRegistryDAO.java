package net.ihe.gazelle.maestro.technical.configuration;

import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;

import java.util.List;

public interface ServiceRegistryDAO {

    /**
     * @return a list of service load from DataBase
     * @throws ReadingFileException if it can't read Database content
     */
    List<Service> getServices();

}
