package org.terminate.service;

import org.terminate.entity.Strategy;
import org.terminate.repository.StrategyRepository;
import org.terminate.repository.StrategyLikeRepository;
import org.terminate.repository.FavoriteRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.concurrent.TimeUnit;
import java.util.List;

@Service
public class StrategyService {

    private final StrategyRepository strategyRepository;
    private final StringRedisTemplate redisTemplate;
    private final StrategyLikeRepository strategyLikeRepository;
    private final FavoriteRepository favoriteRepository;
    private final ObjectMapper objectMapper;

    public StrategyService(StrategyRepository strategyRepository, 
                           StringRedisTemplate redisTemplate,
                           StrategyLikeRepository strategyLikeRepository,
                           FavoriteRepository favoriteRepository,
                           ObjectMapper objectMapper) {
        this.strategyRepository = strategyRepository;
        this.redisTemplate = redisTemplate;
        this.strategyLikeRepository = strategyLikeRepository;
        this.favoriteRepository = favoriteRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 用户上传攻略（发布攻略）
     * 业务逻辑：
     * 1. 校验用户发布频率（限流），防止恶意刷屏
     * 2. 设置攻略初始状态为“待审核”（status=0）
     * 3. 关联作者ID和创建时间
     * 4. 保存至数据库
     *
     * @param strategy 攻略实体对象（包含标题、内容、标签等）
     * @param authorId 作者ID（当前登录用户ID）
     * @return 保存后的攻略对象
     */
    public Strategy create(Strategy strategy, Long authorId) {
        // 1. 限流检查：限制每个用户1小时内最多发布5篇攻略
        String rateLimitKey = "rate_limit:publish:" + authorId;
        String countStr = redisTemplate.opsForValue().get(rateLimitKey);
        int count = countStr == null ? 0 : Integer.parseInt(countStr);
        
        if (count >= 5) {
            // 若超过限制，抛出业务异常
            throw new org.terminate.exception.BusinessException(429, "发布过于频繁，请稍后再试");
        }
        
        // 2. 更新限流计数器
        redisTemplate.opsForValue().increment(rateLimitKey);
        if (count == 0) {
            // 如果是第一次发布，设置过期时间为1小时
            redisTemplate.expire(rateLimitKey, 1, java.util.concurrent.TimeUnit.HOURS);
        }

        // 3. 补全攻略基础信息
        strategy.setAuthorId(authorId);
        strategy.setStatus(0); // 默认为待审核状态
        strategy.setCreateTime(java.time.LocalDateTime.now());
        strategy.setViewCount(0);
        strategy.setLikeCount(0);

        // 4. 持久化到数据库
        return strategyRepository.save(strategy);
    }

    /**
     * 根据游戏标签筛选攻略文章
     * 业务逻辑：
     * 1. 查询指定标签下且状态为“已发布”（status=1）的攻略
     * 2. 结果按创建时间倒序排列（最新发布的在前面）
     *
     * @param gameTag 游戏标签（如 MOBA, RPG, 独立游戏）
     * @return 符合条件的攻略列表
     */
    public List<Strategy> filterByTag(String gameTag) {
        // 参数校验
        if (!StringUtils.hasText(gameTag)) {
            return java.util.Collections.emptyList();
        }
        
        // 调用 Repository 层查询数据库
        // 注意：只查询 status=1 (已发布) 的文章
        return strategyRepository.findByGameTagAndStatusOrderByCreateTimeDesc(gameTag, 1);
    }

    public Strategy audit(Long id, Integer status) {
        Strategy strategy = strategyRepository.findById(id).orElseThrow(() -> new RuntimeException("攻略不存在"));
        strategy.setStatus(status);
        Strategy saved = strategyRepository.save(strategy);
        if (status == 1) {
            redisTemplate.opsForZSet().add("hot_strategies", saved.getId().toString(), 0);
            // Clear Cache
            redisTemplate.delete("public_strategies");
        }
        return saved;
    }

    public List<Strategy> findAllPublic() {
        String cacheKey = "public_strategies";
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(cacheValue)) {
            try {
                return objectMapper.readValue(cacheValue, new TypeReference<List<Strategy>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        List<Strategy> strategies = strategyRepository.findByStatusOrderByCreateTimeDesc(1);
        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(strategies), 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strategies;
    }
    
    public List<Strategy> findByAuthor(Long authorId) {
        return strategyRepository.findByAuthorId(authorId);
    }

    public List<Strategy> findByStatus(Integer status) {
        return strategyRepository.findByStatusOrderByCreateTimeDesc(status);
    }

    public List<Strategy> search(String keyword) {
        return strategyRepository.findByTitleContainingAndStatus(keyword, 1);
    }

    // 新增：根据ID查询
    public Strategy findById(Long id) {
        Strategy strategy = strategyRepository.findById(id).orElseThrow(() -> new RuntimeException("攻略不存在"));
        // 增加浏览量
        strategy.setViewCount(strategy.getViewCount() + 1);
        strategyRepository.save(strategy);
        // 更新Redis热度
        redisTemplate.opsForZSet().incrementScore("hot_strategies", id.toString(), 1);
        return strategy;
    }

    @org.springframework.transaction.annotation.Transactional
    public void toggleLike(Long userId, Long strategyId) {
        java.util.Optional<org.terminate.entity.StrategyLike> like = strategyLikeRepository.findByUserIdAndStrategyId(userId, strategyId);
        Strategy strategy = strategyRepository.findById(strategyId).orElseThrow(() -> new RuntimeException("攻略不存在"));
        
        if (like.isPresent()) {
            strategyLikeRepository.deleteByUserIdAndStrategyId(userId, strategyId);
            strategy.setLikeCount(Math.max(0, strategy.getLikeCount() - 1));
            // 减少热度
            redisTemplate.opsForZSet().incrementScore("hot_strategies", strategyId.toString(), -2);
        } else {
            org.terminate.entity.StrategyLike newLike = new org.terminate.entity.StrategyLike();
            newLike.setUserId(userId);
            newLike.setStrategyId(strategyId);
            strategyLikeRepository.save(newLike);
            strategy.setLikeCount(strategy.getLikeCount() + 1);
            // 增加热度 (点赞权重更高)
            redisTemplate.opsForZSet().incrementScore("hot_strategies", strategyId.toString(), 2);
        }
        strategyRepository.save(strategy);
    }

    @org.springframework.transaction.annotation.Transactional
    public void toggleFavorite(Long userId, Long strategyId) {
        java.util.Optional<org.terminate.entity.Favorite> fav = favoriteRepository.findByUserIdAndStrategyId(userId, strategyId);
        
        if (fav.isPresent()) {
            favoriteRepository.deleteByUserIdAndStrategyId(userId, strategyId);
            redisTemplate.opsForZSet().incrementScore("hot_strategies", strategyId.toString(), -5);
        } else {
            org.terminate.entity.Favorite newFav = new org.terminate.entity.Favorite();
            newFav.setUserId(userId);
            newFav.setStrategyId(strategyId);
            favoriteRepository.save(newFav);
            redisTemplate.opsForZSet().incrementScore("hot_strategies", strategyId.toString(), 5);
        }
    }

    public boolean isLiked(Long userId, Long strategyId) {
        return strategyLikeRepository.findByUserIdAndStrategyId(userId, strategyId).isPresent();
    }

    public boolean isFavorited(Long userId, Long strategyId) {
        return favoriteRepository.findByUserIdAndStrategyId(userId, strategyId).isPresent();
    }

    public List<Strategy> findMyFavorites(Long userId) {
        List<org.terminate.entity.Favorite> favorites = favoriteRepository.findByUserId(userId);
        List<Long> strategyIds = favorites.stream().map(org.terminate.entity.Favorite::getStrategyId).collect(java.util.stream.Collectors.toList());
        return strategyRepository.findAllById(strategyIds);
    }

    public Strategy save(Strategy strategy) {
        return strategyRepository.save(strategy);
    }

    public List<Strategy> findAll() {
        return strategyRepository.findAll();
    }
}
