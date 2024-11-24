package ppl.server.iam.data.dao.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ppl.server.iam.data.dao.entities.Organization;

import java.util.Collection;
import java.util.List;

@Mapper
public interface OrganizationMapper {
    void insert(Organization organization);

    Organization getById(@Param("id") Long id);

    List<Organization> getByIds(@Param("ids") Collection<Long> ids);

    boolean delete(@Param("id") Long id);

    boolean update(Organization organization);
}
