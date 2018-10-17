package com.anelsoftware.beer.service.impl;

import com.anelsoftware.beer.service.InsumoService;
import com.anelsoftware.beer.domain.Insumo;
import com.anelsoftware.beer.repository.InsumoRepository;
import com.anelsoftware.beer.repository.search.InsumoSearchRepository;
import com.anelsoftware.beer.service.dto.InsumoDTO;
import com.anelsoftware.beer.service.mapper.InsumoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Insumo.
 */
@Service
@Transactional
public class InsumoServiceImpl implements InsumoService {

    private final Logger log = LoggerFactory.getLogger(InsumoServiceImpl.class);

    private InsumoRepository insumoRepository;

    private InsumoMapper insumoMapper;

    private InsumoSearchRepository insumoSearchRepository;

    public InsumoServiceImpl(InsumoRepository insumoRepository, InsumoMapper insumoMapper, InsumoSearchRepository insumoSearchRepository) {
        this.insumoRepository = insumoRepository;
        this.insumoMapper = insumoMapper;
        this.insumoSearchRepository = insumoSearchRepository;
    }

    /**
     * Save a insumo.
     *
     * @param insumoDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public InsumoDTO save(InsumoDTO insumoDTO) {
        log.debug("Request to save Insumo : {}", insumoDTO);

        Insumo insumo = insumoMapper.toEntity(insumoDTO);
        insumo = insumoRepository.save(insumo);
        InsumoDTO result = insumoMapper.toDto(insumo);
        insumoSearchRepository.save(insumo);
        return result;
    }

    /**
     * Get all the insumos.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<InsumoDTO> findAll() {
        log.debug("Request to get all Insumos");
        return insumoRepository.findAll().stream()
            .map(insumoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one insumo by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<InsumoDTO> findOne(Long id) {
        log.debug("Request to get Insumo : {}", id);
        return insumoRepository.findById(id)
            .map(insumoMapper::toDto);
    }

    /**
     * Delete the insumo by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Insumo : {}", id);
        insumoRepository.deleteById(id);
        insumoSearchRepository.deleteById(id);
    }

    /**
     * Search for the insumo corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<InsumoDTO> search(String query) {
        log.debug("Request to search Insumos for query {}", query);
        return StreamSupport
            .stream(insumoSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(insumoMapper::toDto)
            .collect(Collectors.toList());
    }
}
