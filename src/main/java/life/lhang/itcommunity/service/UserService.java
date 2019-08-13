package life.lhang.itcommunity.service;

import life.lhang.itcommunity.mapper.UserMapper;
import life.lhang.itcommunity.mode.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
        //数据库中存储的旧账户数据
        List<User> users = userMapper.findByAccountId(user.getAccountId());
        if (users.size() == 0) {
            // 插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        } else {
            //更新
            User oldUser = users.get(0);
            oldUser.setGmtModified(System.currentTimeMillis());
            oldUser.setAvatarUrl(user.getAvatarUrl());
            oldUser.setName(user.getName());
            oldUser.setToken(user.getToken());
            //将github返回的最新数据更新到数据库中。
            userMapper.update(oldUser);
        }
    }
}
