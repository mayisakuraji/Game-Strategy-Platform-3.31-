package org.terminate.repository;

import org.terminate.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndStrategyId(Long userId, Long strategyId);
    List<Favorite> findByUserId(Long userId);
    void deleteByUserIdAndStrategyId(Long userId, Long strategyId);
}
