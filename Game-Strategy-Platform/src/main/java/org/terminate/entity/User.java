package org.terminate.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体
 * 对应要求：管理端维护用户权限，普通玩家/作者/管理员角色区分
 */
@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // 账号

    @Column(nullable = false)
    private String password; // 密码（加密存储）

    private String nickname; // 昵称

    private String avatar; // 头像URL

    private String email; // 邮箱

    /**
     * 角色：ROLE_USER(普通玩家), ROLE_AUTHOR(策略作者), ROLE_ADMIN(管理员)
     */
    @Column(nullable = false)
    private String role;

    private LocalDateTime createTime = LocalDateTime.now();
}
