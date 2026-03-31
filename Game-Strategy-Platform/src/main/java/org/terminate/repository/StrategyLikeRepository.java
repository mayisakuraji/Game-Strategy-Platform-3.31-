package org.terminate.repository;

import org.terminate.entity.StrategyLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StrategyLikeRepository extends JpaRepository<StrategyLike, Long> {
    Optional<StrategyLike> findByUserIdAndStrategyId(Long userId, Long strategyId);
    void deleteByUserIdAndStrategyId(Long userId, Long strategyId);
}
