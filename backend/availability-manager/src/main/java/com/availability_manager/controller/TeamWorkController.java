package com.availability_manager.controller;

import com.availability_manager.exception.MaxTeamsReachedException;
import com.availability_manager.model.DTO.TeamWorkDTO;
import com.availability_manager.service.TeamWorkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/teamwork")
public class TeamWorkController {

    private final TeamWorkService teamWorkService;

    @GetMapping()
    public ResponseEntity<List<TeamWorkDTO>> getAllTeamWork() {
        return new ResponseEntity<>(teamWorkService.getAllTeamWork(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TeamWorkDTO> createTeamWork(@RequestBody @Valid @NotNull TeamWorkDTO teamWorkDTO) {
        return new ResponseEntity<>(teamWorkService.saveTeamWork(teamWorkDTO), HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<TeamWorkDTO> updateTeamWork(@RequestBody @Valid @NotNull TeamWorkDTO teamWorkDTO) {
        return new ResponseEntity<>(teamWorkService.updateTeamWork(teamWorkDTO), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTeamWork(@RequestParam @NotNull Long teamWorkId) {
        teamWorkService.deleteTeamWork(teamWorkId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MaxTeamsReachedException.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
