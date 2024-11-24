package ppl.server.iam.data.dao.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ppl.server.iam.data.dao.entities.Organization;
import ppl.server.iam.data.dao.entities.UserOrganization;

import java.util.Collection;
import java.util.List;

@Mapper
public interface UserOrganizationMapper {
    void insert(UserOrganization userOrganization);

    UserOrganization getById(@Param("id") Long id);

    List<UserOrganization> getByIds(@Param("ids") Collection<Long> ids);

    List<UserOrganization> getByUserId(@Param("user_id") Long userId);

    boolean delete(@Param("id") Long id);
}
