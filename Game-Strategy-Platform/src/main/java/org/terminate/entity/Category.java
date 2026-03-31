package org.terminate.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 游戏分类实体类
 * 对应数据库 category 表，用于区分 MOBA、RPG 等不同游戏品类
 */
@Data
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // 分类名称，如：MOBA、第一人称射击、角色扮演

    private String icon; // 分类图标 URL（预留给前端显示用）

    private LocalDateTime createTime = LocalDateTime.now(); // 创建时间
}