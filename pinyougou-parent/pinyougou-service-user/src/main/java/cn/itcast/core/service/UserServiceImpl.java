package cn.itcast.core.service;

import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.address.AddrnowDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.address.AddrnowQuery;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import vo.UserVo;

import javax.jms.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理
 */
@Service
@Transactional
public class UserServiceImpl implements  UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private AddrnowDao addrnowDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination smsDestination;
    @Autowired
    private IdWorker idWorker;
    //发短信
    public void sendCode(String phone){
        //1:生成6位验证码
        String randomNumeric = RandomStringUtils.randomNumeric(6);
        //2:保存缓存一份
        redisTemplate.boundValueOps(phone).set(randomNumeric,5, TimeUnit.HOURS);

        //3:发消息
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("SignName","品优购商城");
                mapMessage.setString("TemplateCode","SMS_126462276");
                mapMessage.setString("TemplateParam","{\"number\":\""+randomNumeric+"\"}");
                mapMessage.setString("PhoneNumbers",phone);//17630593193

                return mapMessage;
            }
        });


    }


    //用户添加
    @Override
    public void add(String smscode, User user) {
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        if(null != code){
        //1：判断验证码是否失效
            if(code.equals(smscode)){
        //2:判断验证码是否正确
                //3:先加密密码 再保存用户
                user.setCreated(new Date());
                user.setUpdated(new Date());
                userDao.insertSelective(user);
            }else{
                throw new RuntimeException("验证码错误");
            }

        }else{
            throw new RuntimeException("验证码失败");
        }



    }

    @Override
    public void addPersonalInfo(UserVo userVo,String name) {
        UserQuery userQuery = new UserQuery();
        userQuery.createCriteria().andUsernameEqualTo(name);
        userDao.updateByExampleSelective(userVo.getUser(),userQuery);
        long addrNowId = idWorker.nextId();
        userVo.getAddrnow().setId(new BigDecimal(addrNowId));
        userVo.getAddrnow().setUserId(name);
        addrnowDao.insert(userVo.getAddrnow());
    }

    @Override
    public User findUser(String username) {
        UserQuery userQuery = new UserQuery();
        userQuery.createCriteria().andUsernameEqualTo(username);
        List<User> users = userDao.selectByExample(userQuery);
        if (users!=null&&users.size()>0){
            return users.get(0);
        }
        return null;
    }

}
