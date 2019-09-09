package com.example.community.mapper;

import com.example.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


@Mapper
public interface UserMapper {
    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modified,avatar_url) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    void insert(User user);

    @Select("select * from user where token = #{token}")
    //当方法的参数不是类的时候，就需要用@Param，是类的时候就不需要，就如上面的insert方法，因为传入的是User类参数，所以没有使用@Param注解
    User finByToken(@Param("token") String token);

    @Select("select * from user where id=#{id}")
    User finById(@Param("id") Integer id);
}
