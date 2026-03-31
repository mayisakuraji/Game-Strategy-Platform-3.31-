package org.terminate.service;

import org.terminate.entity.Comment;
import org.terminate.entity.User;
import org.terminate.repository.CommentRepository;
import org.terminate.repository.UserRepository;
import org.terminate.vo.CommentVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public Comment save(Comment comment) {
        comment.setCreateTime(java.time.LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<CommentVO> findByStrategyId(Long strategyId) {
        List<Comment> comments = commentRepository.findByStrategyIdOrderByCreateTimeDesc(strategyId);
        
        // 1. Convert to VOs
        List<CommentVO> vos = comments.stream().map(c -> {
            User user = userRepository.findById(c.getUserId()).orElse(new User());
            return CommentVO.from(c, user.getUsername(), user.getAvatar());
        }).collect(Collectors.toList());

        // 2. Build Tree
        Map<Long, CommentVO> map = vos.stream().collect(Collectors.toMap(CommentVO::getId, v -> v));
        List<CommentVO> rootComments = new ArrayList<>();

        for (CommentVO vo : vos) {
            if (vo.getParentId() == null) {
                rootComments.add(vo);
            } else {
                CommentVO parent = map.get(vo.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                } else {
                    // Orphaned comment or parent not loaded, treat as root or ignore
                    rootComments.add(vo); 
                }
            }
        }
        
        return rootComments;
    }
}
