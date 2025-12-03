package com.mugen.backend.controller;

import com.mugen.backend.entity.Race;
import com.mugen.backend.service.RaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/races")
@RequiredArgsConstructor
@Slf4j
public class RaceController {

    private final RaceService raceService;

    /**
     * GET /races
     * Listar todas as raças ativas
     */
    @GetMapping
    public ResponseEntity<List<Race>> listRaces() {
        log.debug("Listing all active races");
        return ResponseEntity.ok(raceService.findAllActive());
    }

    /**
     * GET /races/{id}
     * Buscar raça por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Race> getRace(@PathVariable Integer id) {
        log.debug("Getting race: {}", id);

        return raceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
