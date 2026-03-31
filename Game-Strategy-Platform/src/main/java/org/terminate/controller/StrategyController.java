package org.terminate.controller;

import org.terminate.common.Result;
import org.terminate.entity.Strategy;
import org.terminate.entity.User;
import org.terminate.repository.UserRepository;
import org.terminate.service.StrategyService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/strategy")
@CrossOrigin
public class StrategyController {

    private final StrategyService strategyService;
    private final UserRepository userRepository;

    public StrategyController(StrategyService strategyService, UserRepository userRepository) {
        this.strategyService = strategyService;
        this.userRepository = userRepository;
    }

    // 发布攻略
    @PostMapping("/publish")
    public Result<Strategy> publish(@RequestBody Strategy strategy) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return Result.success(strategyService.create(strategy, user.getId()));
    }

    // 公开列表
    @GetMapping("/public/list")
    public Result<List<Strategy>> publicList() {
        return Result.success(strategyService.findAllPublic());
    }

    // 管理员获取待审核列表
    @GetMapping("/admin/list")
    public Result<List<Strategy>> adminList(@RequestParam(required = false, defaultValue = "0") Integer status) {
        return Result.success(strategyService.findByStatus(status));
    }

    // 管理员审核
    @PutMapping("/admin/audit/{id}")
    public Result<Strategy> audit(@PathVariable Long id, @RequestParam Integer status) {
        return Result.success(strategyService.audit(id, status));
    }
    
    // 获取当前作者的攻略
    @GetMapping("/my")
    public Result<List<Strategy>> myStrategies() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return Result.success(strategyService.findByAuthor(user.getId()));
    }

    // 搜索攻略
    @GetMapping("/search")
    public Result<List<Strategy>> search(@RequestParam String keyword) {
        return Result.success(strategyService.search(keyword));
    }

    // 点赞
    @PostMapping("/like/{id}")
    public Result<String> like(@PathVariable Long id) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        strategyService.toggleLike(user.getId(), id);
        return Result.success("操作成功");
    }

    // 收藏
    @PostMapping("/favorite/{id}")
    public Result<String> favorite(@PathVariable Long id) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        strategyService.toggleFavorite(user.getId(), id);
        return Result.success("操作成功");
    }

    // 获取我的收藏
    @GetMapping("/my-favorites")
    public Result<List<Strategy>> myFavorites() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return Result.success(strategyService.findMyFavorites(user.getId()));
    }
    
    // 获取详情状态（是否点赞/收藏）
    @GetMapping("/{id}/status")
    public Result<java.util.Map<String, Boolean>> getStatus(@PathVariable Long id) {
        String username = getCurrentUsername();
        boolean liked = false;
        boolean favorited = false;
        try {
             User user = userRepository.findByUsername(username).orElse(null);
             if (user != null) {
                 liked = strategyService.isLiked(user.getId(), id);
                 favorited = strategyService.isFavorited(user.getId(), id);
             }
        } catch (Exception e) {
             // ignore, default false
        }
        java.util.Map<String, Boolean> map = new java.util.HashMap<>();
        map.put("liked", liked);
        map.put("favorited", favorited);
        return Result.success(map);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
