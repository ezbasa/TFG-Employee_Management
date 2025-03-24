package com.availability_manager.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ConfigTeamWork {
    @Value("${employee.max.teams}")
    private int maxTeams;
}
