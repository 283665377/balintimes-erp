package ServiceTest;

import com.balintimes.erp.center.dao.OrganizationDao;
import com.balintimes.erp.center.model.Organization;
import com.balintimes.erp.center.model.User;
import com.balintimes.erp.center.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.balintimes.erp.center.service.OrganizationService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by AlexXie on 2015/7/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class TestTrans {

    @Resource
    private OrganizationService organizationService;

    @Resource
    private  OrganizationDao organizationDao;

    @Test
    public void TestOrgSet(){

        List<Organization> list = this.organizationDao.GetOrgSet("合同管理员");

        for (Organization organization : list) {
            System.out.println(organization.getName());
        }
    }

    @Test
    public void TestOrgTrans() {

        this.organizationService.UpdateOrgTest("uiduid", "alex");
    }

    @Test
    public void exec() {
        Logger logger1 = LoggerFactory.getLogger("ServicesAspectAdvice");
        logger1.debug("my ServicesAspectAdvice console");
        logger1.warn("my ServicesAspectAdvice warn");
    }

    @Test
    public void execLogger() {
        Logger logger = LoggerFactory.getLogger("ServicesAspectAdvice");

        MDC.put("Exception", "Exception");
        MDC.put("Action", "Action");

        MDC.put("Method", "Method");
        MDC.put("ClientIp", "ClientIp");
        MDC.put("RequestUrl", "RequestUrl");
        MDC.put("AppUid", "AppUid");
        MDC.put("AppName", "AppName");
        MDC.put("UserUid", "UserUid");
        MDC.put("UserName", "UserName");
        MDC.put("EmpUid", "EmpUid");
        MDC.put("EmpName", "EmpName");
        MDC.put("Params", "AAA:111,bbbbb:444444");
        logger.info("message");
        System.out.println("erwerw");
        MDC.clear();
    }

    @Test
    public  void TestTime(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        System.out.println(dateFormat.format(new Date()));
    }

    @Test
    public  void JasonTest(){

        User u = new User();
        u.setUsername("谢云杰");

        System.out.println(JsonUtil.ToJson(u));
    }


}
