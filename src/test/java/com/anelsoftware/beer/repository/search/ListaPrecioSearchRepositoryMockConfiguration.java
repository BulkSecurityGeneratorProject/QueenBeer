package com.anelsoftware.beer.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of ListaPrecioSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ListaPrecioSearchRepositoryMockConfiguration {

    @MockBean
    private ListaPrecioSearchRepository mockListaPrecioSearchRepository;

}
