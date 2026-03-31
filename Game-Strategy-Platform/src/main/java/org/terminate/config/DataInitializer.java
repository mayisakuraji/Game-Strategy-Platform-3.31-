package org.terminate.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.terminate.entity.Strategy;
import org.terminate.entity.User;
import org.terminate.repository.StrategyRepository;
import org.terminate.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StrategyRepository strategyRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, StrategyRepository strategyRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.strategyRepository = strategyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. 初始化用户
        if (userRepository.count() == 0) {
            // 创建管理员
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setNickname("系统管理员");
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);

            // 创建普通用户
            User user = new User();
            user.setUsername("player");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setNickname("骨灰级玩家");
            user.setRole("ROLE_USER");
            userRepository.save(user);
            
            System.out.println(">>> 用户初始化成功！(账号: admin/123456, player/123456)");
        }

        // 2. 初始化攻略数据
        if (strategyRepository.count() == 0) {
            User author = userRepository.findByUsername("player").orElse(null);
            if (author != null) {
                // Strategy 1
                Strategy s1 = new Strategy();
                s1.setTitle("《黑神话：悟空》新手入门战斗技巧详解");
                s1.setGameTag("RPG");
                s1.setContent("# 黑神话：悟空 新手指南\n\n## 1. 棍法基础\n熟悉三种棍势...\n\n## 2. 定身术\n定身术是前期核心...");
                s1.setAuthorId(author.getId());
                s1.setStatus(1);
                s1.setViewCount(1205);
                s1.setLikeCount(342);
                s1.setCoverImage("https://image.gcores.com/71f65485-86a0-4221-995b-060410499092.jpg");
                strategyRepository.save(s1);

                // Strategy 2
                Strategy s2 = new Strategy();
                s2.setTitle("《英雄联盟》S14赛季上单上分英雄推荐");
                s2.setGameTag("MOBA");
                s2.setContent("# S14 上单梯队\n\n1. 剑魔：依然强势\n2. 诺手：低分段杀手\n3. 奎桑提：机制怪");
                s2.setAuthorId(author.getId());
                s2.setStatus(1);
                s2.setViewCount(892);
                s2.setLikeCount(156);
                s2.setCoverImage("https://img.redbull.com/images/c_crop,x_345,y_0,h_1080,w_1620/c_fill,w_1260,h_840/q_auto,f_auto/redbullcom/2022/11/14/ctw8c0w1z8w8w8w8w8w8/league-of-legends-s13-preseason-changes");
                strategyRepository.save(s2);

                // Strategy 3
                Strategy s3 = new Strategy();
                s3.setTitle("《Valorant》霓虹町地图点位教学");
                s3.setGameTag("FPS");
                s3.setContent("A点防守站位...");
                s3.setAuthorId(author.getId());
                s3.setStatus(1);
                s3.setViewCount(450);
                s3.setLikeCount(88);
                s3.setCoverImage("https://images.contentstack.io/v3/assets/bltb6530b271fddd0b1/blt7275cf5222722b5e/620bd1265050845eb5b02663/Neon_Screenshot_1920x1080.jpg");
                strategyRepository.save(s3);
            }
        }
    }
}
