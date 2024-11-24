package ppl.server.iam.data.dao.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ppl.server.iam.data.dao.entities.UserPassword;

import java.util.Collection;
import java.util.List;

@Mapper
public interface UserPasswordMapper {
    void insert(UserPassword userPassword);

    UserPassword getById(@Param("id") Long id);

    UserPassword getByUserId(@Param("user_id") Long userId);

    List<UserPassword> getByIds(@Param("ids") Collection<Long> ids);

    boolean delete(@Param("id") Long id);
}
