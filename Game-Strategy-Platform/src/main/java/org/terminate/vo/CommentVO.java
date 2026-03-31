package org.terminate.vo;

import lombok.Data;
import org.terminate.entity.Comment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentVO {
    private Long id;
    private Long strategyId;
    private String content;
    private Long parentId;
    private Long userId;
    private String username;
    private String avatar;
    private LocalDateTime createTime;
    private List<CommentVO> children = new ArrayList<>();

    public static CommentVO from(Comment comment, String username, String avatar) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setStrategyId(comment.getStrategyId());
        vo.setContent(comment.getContent());
        vo.setParentId(comment.getParentId());
        vo.setUserId(comment.getUserId());
        vo.setCreateTime(comment.getCreateTime());
        vo.setUsername(username);
        vo.setAvatar(avatar);
        return vo;
    }
}
