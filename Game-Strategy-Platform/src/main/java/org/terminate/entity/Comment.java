package org.terminate.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 策略问答/评论实体
 * 对应要求：支持玩家对攻略发起回复或问答交互
 */
@Data
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long strategyId; // 所属攻略ID

    @Column(nullable = false)
    private String content; // 评论或问答内容

    private Long parentId; // 父评论ID（回复）
    
    private Long userId; // 评论人ID

    private LocalDateTime createTime = LocalDateTime.now();
}