package ppl.server.iam.data.dao.entities;

import java.util.Date;

/**
 * 组织表;
 */
public class Organization {
    /**
     * ID;大于0
     */
    private Long id;
    /**
     * 组织机构码;a-zA-Z0-9
     */
    private String code;
    /**
     * 组织机构名称
     */
    private String name;
    /**
     * 父组织机构码
     */
    private Long parentId;
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

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Date getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getModificationTime() {
        return this.modificationTime;
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
        return this.modifiedBy;
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