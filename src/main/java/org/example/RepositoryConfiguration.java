package org.example;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("org.example.repository.dao")
@Slf4j
public class RepositoryConfiguration {


//    @Bean
//    @ConditionalOnMissingBean(name = "mybatisPlusInterceptor")
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
//        paginationInnerInterceptor.setOptimizeJoin(true);
//        paginationInnerInterceptor.setDbType(DbType.MYSQL);
//        paginationInnerInterceptor.setOverflow(false); // 溢出总页数 不处理成第一页
//        interceptor.addInnerInterceptor(paginationInnerInterceptor);
//        return interceptor;
//    }

}