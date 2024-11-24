package ppl.server.iam.data.dao.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ppl.server.iam.data.dao.entities.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Mapper
public interface UserMapper {
    void insert(User user);

    User getById(@Param("id") Long id);

    List<User> getByIds(@Param("ids") Collection<Long> ids);

    User getByUsername(@Param("username") String username);

    boolean delete(@Param("id") Long id, @Param("modificationTime") Date modificationTime, @Param("modifiedBy") Long modifiedBy);

    boolean update(User user);
}
