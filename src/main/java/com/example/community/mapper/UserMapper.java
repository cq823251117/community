package com.example.community.mapper;

import com.example.community.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {
    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modified,avatar_url) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    void insert(User user);

    @Select("select * from user where token = #{token}")
    //当方法的参数不是类的时候，就需要用@Param，是类的时候就不需要，就如上面的insert方法，因为传入的是User类参数，所以没有使用@Param注解
    User finByToken(@Param("token") String token);

    @Select("select * from user where id=#{id}")
    User finById(@Param("id") Integer id);

    @Select("select * from user where account_id=#{accountId}")
    User findByAccountId(@Param("accountId")String accountId);

    @Update("update user set name=#{name},token=#{token},gmt_modified=#{gmtModified},avatar_url=#{avatarUrl} where id =#{id}")
    void update(User user);
}
