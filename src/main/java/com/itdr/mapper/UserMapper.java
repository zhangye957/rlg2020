package com.itdr.mapper;

import com.itdr.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUserNameAndPassword(String username, String password);

    User selectByUserName(String username);

    int selectByUserNameOrEmail(@Param("str") String str, @Param("type")String type);

    int selectByUserNameAndQuestionAndAnswer(@Param("username") String username, @Param("question") String question,@Param("answer") String answer);

    int updateByUserNameAndPasswordNew(@Param("username") String username, @Param("passwordNew") String passwordNew);

    int updateByUserNameAndPasswordOldAndPasswordNew(@Param("username") String username, @Param("passwordOld") String passwordOld, @Param("passwordNew") String passwordNew);
}