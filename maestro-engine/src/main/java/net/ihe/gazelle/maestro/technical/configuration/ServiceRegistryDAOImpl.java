package net.ihe.gazelle.maestro.technical.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.lang.InterruptedRuntimeException;
import net.ihe.gazelle.search.api.Range;
import net.ihe.gazelle.search.api.SearchQuery;
import net.ihe.gazelle.search.api.SearchResult;
import net.ihe.gazelle.servicemetadata.api.business.Service;
import net.ihe.gazelle.serviceregistry.api.business.DeployedService;
import net.ihe.gazelle.serviceregistry.api.business.lookup.ServiceSearchCriteria;
import net.ihe.gazelle.serviceregistry.client.business.ServiceLookupClient;
import net.ihe.gazelle.serviceregistry.client.technical.rest.ServiceLookupClientImpl;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ServiceRegistryDAOImpl implements ServiceRegistryDAO {

    @ConfigProperty(name = "gzl.service.registry.url")
    String serviceRegistryUrl;

    @Override
    public List<Service> getServices() {
        try {
            ServiceLookupClient client = new ServiceLookupClientImpl(serviceRegistryUrl);
            SearchResult<DeployedService> searchResult = client.search(new SearchQuery<>(
                    new ServiceSearchCriteria(),
                    new Range(0, Integer.MAX_VALUE),
                    null
            ));
            return new ArrayList<>(searchResult.objects().stream().map(
                    Service.class::cast
            ).toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedRuntimeException(e);
        }
    }

}
