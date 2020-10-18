package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void testSensitive(){
        String text = "这里可以赌博，可以嫖娼，可以吸毒，可以开票";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "这里可以赌 博，可以嫖2娼，可以吸x毒，可以x开票   dd的";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
