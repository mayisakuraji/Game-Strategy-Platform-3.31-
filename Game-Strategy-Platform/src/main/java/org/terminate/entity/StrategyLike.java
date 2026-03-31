package org.terminate.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "strategy_like", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "strategyId"})
})
public class StrategyLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long strategyId;

    private LocalDateTime createTime = LocalDateTime.now();
}
