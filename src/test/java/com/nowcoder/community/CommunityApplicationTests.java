package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.dao.AlphaDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Autowired
    private AlphaService alphaService;

    @Test
    public void testApplicationContext()
    {
        System.out.println(this.applicationContext);
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        System.out.printf(alphaDao.select());
        alphaDao = (AlphaDao) applicationContext.getBean("alphaDarHibernate");
        System.out.println(alphaDao.select());
    }
    @Test
    public void testBeanManagement()
    {
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
    }

    @Test
    public void testBeanConfig()
    {
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
