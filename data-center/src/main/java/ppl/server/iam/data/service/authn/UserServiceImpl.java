package ppl.server.iam.data.service.authn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ppl.common.utils.string.Strings;
import ppl.server.iam.authn.UserService;
import ppl.server.iam.authn.bo.EnhancedUser;
import ppl.server.iam.data.dao.entities.Organization;
import ppl.server.iam.data.dao.entities.User;
import ppl.server.iam.data.dao.entities.UserOrganization;
import ppl.server.iam.data.dao.entities.UserPassword;
import ppl.server.iam.data.dao.mappers.OrganizationMapper;
import ppl.server.iam.data.dao.mappers.UserMapper;
import ppl.server.iam.data.dao.mappers.UserOrganizationMapper;
import ppl.server.iam.data.dao.mappers.UserPasswordMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserPasswordMapper userPasswordMapper;
    @Autowired
    private UserOrganizationMapper userOrganizationMapper;
    @Autowired
    private OrganizationMapper organizationMapper;

    @Override
    @Transactional
    public EnhancedUser getByUsername(String username) {
        if (Strings.isBlank(username)) {
            return null;
        }

        User user = userMapper.getByUsername(username);
        if (user == null) {
            return null;
        }

        UserPassword userPassword = userPasswordMapper.getByUserId(user.getId());
        List<UserOrganization> userOrganizations = userOrganizationMapper.getByUserId(user.getId());

        List<ppl.server.iam.authn.bo.Organization> orgs = Collections.emptyList();
        if (!userOrganizations.isEmpty()) {
            List<Long> organizationIds = userOrganizations.stream()
                    .map(UserOrganization::getId)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            List<Organization> organizations = organizationMapper.getByIds(organizationIds);
            Set<Long> organizationSet = organizations.stream()
                    .map(Organization::getId)
                    .collect(Collectors.toSet());

            for (Long id : organizationIds) {
                if (!organizationSet.contains(id)) {
                    log.info("User '" + user.getId() + "' is in organization '" + id + "' which not exists.");
                }
            }

            orgs = organizations.stream()
                    .map(o -> new ppl.server.iam.authn.bo.Organization(o.getId(), o.getName()))
                    .distinct()
                    .collect(Collectors.toList());
        }

        return EnhancedUser.newBuilder()
                .id(user.getId())
                .username(user.getUsername())
                .password(userPassword.getPassword())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .organizations(orgs)
                .enabled(user.getEnabled())
                .expires(user.getExpires())
                .permissionCodes(Collections.emptyList())
                .locked(false)
                .build();
    }
}
