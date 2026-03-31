package org.terminate.controller;

import org.terminate.common.Result;
import org.terminate.entity.Comment;
import org.terminate.entity.User;
import org.terminate.repository.UserRepository;
import org.terminate.service.CommentService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    public CommentController(CommentService commentService, UserRepository userRepository) {
        this.commentService = commentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public Result<Comment> add(@RequestBody Comment comment) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        comment.setUserId(user.getId());
        return Result.success(commentService.save(comment));
    }

    @GetMapping("/list/{strategyId}")
    public Result<List<org.terminate.vo.CommentVO>> list(@PathVariable Long strategyId) {
        return Result.success(commentService.findByStrategyId(strategyId));
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
