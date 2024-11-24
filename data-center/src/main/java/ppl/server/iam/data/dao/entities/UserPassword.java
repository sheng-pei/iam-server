package ppl.server.iam.data.dao.entities;

import java.util.Date;

/**
 * 用户密码表;
 */
public class UserPassword {
    /**
     * ID;大于0
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 密码
     */
    private String password;
    /**
     * 创建时间
     */
    private Date creationTime;
    /**
     * 修改时间
     */
    private Date modificationTime;
    /**
     * 创建人
     */
    private Long createdBy;
    /**
     * 修改人
     */
    private Long modifiedBy;
    /**
     * 是否删除;删除时写入ID
     */
    private Long deleted;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Long getDeleted() {
        return this.deleted;
    }

    public void setDeleted(Long deleted) {
        this.deleted = deleted;
    }
}
