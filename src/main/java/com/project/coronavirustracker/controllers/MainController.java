package com.project.coronavirustracker.controllers;

import com.project.coronavirustracker.models.AreaStats;
import com.project.coronavirustracker.services.CoronaDataService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class MainController {

    private final CoronaDataService coronaDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<AreaStats> statsList = coronaDataService.getAllStats();
        int sumOfCases = statsList.stream()
                .mapToInt(stat -> stat.getLatestTotalCases())
                .sum();
        int totalNewCases = statsList.stream()
                .mapToInt(stat -> stat.getTendency())
                .sum();
        model.addAttribute("statsList", statsList);
        model.addAttribute("totalReportedCases", sumOfCases);
        model.addAttribute("totalTendency", totalNewCases);
        return "homepage";
    }
}
