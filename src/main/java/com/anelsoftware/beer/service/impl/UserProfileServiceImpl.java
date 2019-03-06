package com.anelsoftware.beer.service.impl;

import com.anelsoftware.beer.domain.User;
import com.anelsoftware.beer.repository.UserRepository;
import com.anelsoftware.beer.service.UserProfileService;
import com.anelsoftware.beer.domain.UserProfile;
import com.anelsoftware.beer.repository.UserProfileRepository;
import com.anelsoftware.beer.repository.search.UserProfileSearchRepository;
import com.anelsoftware.beer.service.dto.UserDTO;
import com.anelsoftware.beer.service.dto.UserProfileDTO;
import com.anelsoftware.beer.service.mapper.UserProfileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing UserProfile.
 */
@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final Logger log = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private final UserProfileRepository userProfileRepository;

    private final UserProfileMapper userProfileMapper;

    private final UserProfileSearchRepository userProfileSearchRepository;

    private final UserRepository userRepository;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper, UserProfileSearchRepository userProfileSearchRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
        this.userProfileSearchRepository = userProfileSearchRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a userProfile.
     *
     * @param userProfileDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public UserProfileDTO save(UserProfileDTO userProfileDTO) {
        log.debug("Request to save UserProfile : {}", userProfileDTO);

        UserProfile userProfile = userProfileMapper.toEntity(userProfileDTO);
        userProfile = userProfileRepository.save(userProfile);
        UserProfileDTO result = userProfileMapper.toDto(userProfile);
        userProfileSearchRepository.save(userProfile);
        return result;
    }

    /**
     * Get all the userProfiles.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UserProfiles");
        return userProfileRepository.findAll(pageable)
            .map(userProfileMapper::toDto);
    }


    /**
     * Get one userProfile by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileDTO> findOne(Long id) {
        log.debug("Request to get UserProfile : {}", id);
        return userProfileRepository.findById(id)
            .map(userProfileMapper::toDto);
    }

    /**
     * Delete the userProfile by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserProfile : {}", id);
        userProfileRepository.deleteById(id);
        userProfileSearchRepository.deleteById(id);
    }

    /**
     * Search for the userProfile corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UserProfiles for query {}", query);
        return userProfileSearchRepository.search(queryStringQuery(query), pageable)
            .map(userProfileMapper::toDto);
    }

    @Override
    public Optional<UserProfileDTO> findByUser(String login) {
        Optional<User> user = this.userRepository.findOneByLogin(login);
        return this.userProfileRepository.findOneByUser(user.get()).map(this.userProfileMapper::toDto);
    }
}
