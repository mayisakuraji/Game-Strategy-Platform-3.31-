package org.terminate.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "strategy")
public class Strategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 攻略标题

    @Column(nullable = false)
    private String gameTag; // 游戏标签（如MOBA/RPG）

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content; // 攻略正文（Markdown格式）

    private String videoUrl; // 视频攻略地址（支持视频发布）
    private String coverImage; // 封面图片

    /**
     * 状态：0-待审核, 1-已发布, 2-驳回
     * 对应要求：管理员审核攻略内容
     */
    private Integer status = 0; 

    private Long authorId; // 作者ID

    private Integer viewCount = 0; // 浏览量
    private Integer likeCount = 0; // 点赞量
    private LocalDateTime createTime = LocalDateTime.now(); // 创建时间
}