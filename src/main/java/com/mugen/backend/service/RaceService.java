package com.mugen.backend.service;

import com.mugen.backend.entity.Race;
import com.mugen.backend.repository.RaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RaceService {

    private final RaceRepository raceRepository;

    public List<Race> findAll() {
        log.debug("Finding all races");
        return raceRepository.findAll();
    }

    public List<Race> findAllActive() {
        log.debug("Finding all active races");
        return raceRepository.findByIsActiveTrue();
    }

    public Optional<Race> findById(Integer id) {
        log.debug("Finding race by id: {}", id);
        return raceRepository.findById(id);
    }

    public Optional<Race> findByName(String name) {
        log.debug("Finding race by name: {}", name);
        return raceRepository.findByName(name);
    }

    @Transactional
    public Race save(Race race) {
        log.info("Saving race: {}", race.getName());
        return raceRepository.save(race);
    }

    @Transactional
    public void deleteById(Integer id) {
        log.info("Deleting race: {}", id);
        raceRepository.deleteById(id);
    }
}
