import com.harry.wechat.WechatBotApplication;
import com.harry.wechat.dto.server.BaseRes;
import com.harry.wechat.service.WeChatervice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Harry
 * @date 2021/1/15
 * Time: 09:44
 * Desc: TestMessage
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WechatBotApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestMessage {


    @Autowired
    private WeChatervice weChatervice;


    @Test
    public void testMessage(){


        BaseRes baseRes = new BaseRes();
        baseRes.setId(111111);
        baseRes.setWxid("test");
        baseRes.setContent("德玛西亚");
        baseRes.setType(1);


        try {
            weChatervice.receiveMsg(baseRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
