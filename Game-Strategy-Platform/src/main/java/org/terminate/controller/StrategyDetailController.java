package org.terminate.controller;

import org.terminate.common.Result;
import org.terminate.entity.Strategy;
import org.terminate.service.StrategyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/strategy")
@CrossOrigin
public class StrategyDetailController {

    private final StrategyService strategyService;

    public StrategyDetailController(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @GetMapping("/{id}")
    public Result<Strategy> getById(@PathVariable Long id) {
        return Result.success(strategyService.findById(id));
    }
}
