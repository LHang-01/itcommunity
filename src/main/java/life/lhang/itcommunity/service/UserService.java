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
        List<User> users = userMapper.findByAccountId(user.getAccountId());
        if (users.size() == 0) {
            // 插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        } else {
            //更新
            User oldUser = users.get(0);
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(oldUser.getAvatarUrl());
            updateUser.setName(oldUser.getName());
            updateUser.setToken(oldUser.getToken());
            userMapper.update(updateUser);
        }
    }
}
