package org.terminate.repository;

import org.terminate.entity.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 攻略持久层：负责与数据库 strategy 表交互
 */
@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Long> {
    List<Strategy> findByStatusOrderByCreateTimeDesc(Integer status);
    List<Strategy> findByAuthorId(Long authorId);
    
    // Search functionality
    List<Strategy> findByTitleContainingAndStatus(String title, Integer status);

    // Filter by game tag
    List<Strategy> findByGameTagAndStatusOrderByCreateTimeDesc(String gameTag, Integer status);
}
