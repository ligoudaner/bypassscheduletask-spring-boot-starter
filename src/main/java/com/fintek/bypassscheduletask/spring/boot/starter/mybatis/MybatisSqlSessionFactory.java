package com.fintek.bypassscheduletask.spring.boot.starter.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

/**
 * @author zhangyongjia balabala.sean@gmail.com
 */
@Slf4j
public class MybatisSqlSessionFactory {

    private SqlSessionFactory sqlSessionFactory;

    public MybatisSqlSessionFactory(DataSource dataSource) {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setConfigLocation(resolver.getResource("classpath:mybatis-conf.xml"));

        ClassPathResource[] classPathResources = new ClassPathResource[]{new ClassPathResource("com/fintek/bypassscheduletask/spring/boot/starter/base/ScheduleMessageMapper.xml")};
        sqlSessionFactoryBean.setMapperLocations(classPathResources);
        sqlSessionFactoryBean.setTypeAliasesPackage("com.fintek.bypassscheduletask.spring.boot.starter.base");

        try {
            sqlSessionFactory = sqlSessionFactoryBean.getObject();
        } catch (Exception e) {
            log.error("create mybatis sql-session factory failed, routingPolicy-client will exit(-1):", e);
            System.exit(-1);
        }
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public SqlSession getSqlSession() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.SIMPLE, false);
        return sqlSession;
    }
}
