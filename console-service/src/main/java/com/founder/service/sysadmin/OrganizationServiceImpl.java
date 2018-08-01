package com.founder.service.sysadmin;

import com.founder.Exception.OperationException;
import com.founder.contract.sysadmin.OrganizationService;
import com.founder.dao.sysadmin.OrganizationDao;
import com.founder.domain.Status;
import com.founder.domain.sysadmin.Organization;
import com.founder.domain.sysadmin.OrganizationValue;
import com.founder.dto.sysadmin.OrganizationDto;

import com.founder.dto.sysadmin.OrganizationReqDto;
import com.founder.exception.sysadmin.SysadminError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private OrganizationDao organizationDao;

    @Override
    public List<Organization> findOrganizationsByOrganizationNoIn(List<String> organizations) {
        return organizationDao.findOrganizationsByOrganizationNoIn(organizations);
    }

    @Override
    public Set<Organization> findOrganizationsByIds(Iterable<Long> ids) {
       return organizationDao.findAllById(ids).stream().collect(Collectors.toSet());
    }

    @Override
    public List<OrganizationValue> findAllOrganizations() {
        return organizationDao.findAllOrganizations();
    }

    @Override
    public List<Organization> findAllOrganizationList() {
        return organizationDao.findAll();
    }

    @Override
    public Organization create(Organization organization, Long parentId, String createUserName) {
        Organization orgOrganization = organizationDao.findOrganizationByOrganizationNo(organization.getOrganizationNo());
        if (ObjectUtils.allNotNull(orgOrganization))
            throw new OperationException(SysadminError.OrganizationNoExists);

        buildOrganization(organization, parentId, createUserName);
        organizationDao.save(organization);
        return organization;
    }

    private void buildOrganization(Organization organization, Long parentId, String createUserName) {
        organization.setId(null);
        organization.setParent(findOrganizationById(parentId));
        organization.setCreateTime(new Date());
        organization.setCreateUser(createUserName);
        organization.setStatus(Status.AVAILIABLE);
    }

    @Override
    public void update(OrganizationDto organizationDto) {
        Organization orgOrganization = organizationDao.findOrganizationByOrganizationNo(organizationDto.getOrganizationNo());
        if (ObjectUtils.allNotNull(orgOrganization)) {
            if (!organizationDto.getId().equals(orgOrganization.getId()))
                throw new OperationException(SysadminError.OrganizationNoExists);
        }

        Organization organization = findOrganizationById(organizationDto.getId());
        organization.setName(organizationDto.getName());
        organization.setOrganizationNo(organizationDto.getOrganizationNo());
        organization.setOrganizationOrder(organizationDto.getOrganizationOrder());
        organization.setOrganizationType(Organization.OrganizationType.valueOf(organizationDto.getOrganizationType()));
        organization.setAddress(organizationDto.getAddress());
        organization.setZipCode(organizationDto.getZipCode());
    }

    @Override
    public void delete(Long id) {
        Organization organization = findOrganizationById(id);
        removeOrganizationVerify(organization);
        organizationDao.delete(organization);
    }

    private void removeOrganizationVerify(Organization organization) {
        if (organization.hasSubOrganizations())
            throw new OperationException(SysadminError.OrganizationHasAffiliatedOrganization);
        if (organization.hasUser())
            throw new OperationException(SysadminError.OrganizationHasUsers);
    }

    private Organization findOrganizationById(Long id) {
        Assert.notNull(id, "部门id不能为空");
        Organization organization = organizationDao.findById(id).orElse(null);
        if (!ObjectUtils.allNotNull(organization))
            throw new OperationException(SysadminError.OrganizationNotExists);
        return organization;
    }

    @Override
    public Organization findRootOragination() {
        return findOrganizationById(1L);
    }

    @Override
    public OrganizationDto findRootOrganizationDto() {
        List<OrganizationValue> organizationValues = organizationDao.findAllOrganizations();
        return convertOrganizationDto(organizationValues);
    }

    private OrganizationDto convertOrganizationDto(List<OrganizationValue> organizationValues) {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationValues.forEach(ov -> {
            if (ov.getLevel() == 1) {
                BeanUtils.copyProperties(ov, organizationDto);
            } else {
                OrganizationDto subOrganization = new OrganizationDto();
                BeanUtils.copyProperties(ov, subOrganization);
                OrganizationDto parentOrginazation = getParentOrganizationDtos(organizationDto, ov.getParentId());
                if (parentOrginazation == null) {
                    log.error("未找到父节点{},{}", ov.getParentId(), ov.getId());
                } else
                    parentOrginazation.appendSubOrgnazation(subOrganization);
            }
        });
        return organizationDto;
    }

    private OrganizationDto getParentOrganizationDtos(OrganizationDto organizationDto, Long id) {
        if (organizationDto.getId().equals(id))
            return organizationDto;
        else {
            for (OrganizationDto orgdto : organizationDto.getSubOrganization()) {
                OrganizationDto orgdtoRet = getParentOrganizationDtos(orgdto, id);
                if (orgdtoRet != null)
                    return orgdtoRet;
            }
            return null;
        }
    }

    @Override
    public Organization findOrganizationByNo(String organizationNo) {
        return findByNo(organizationNo);
    }

    private Organization findByNo(String organizationNo) {
        Assert.notNull(organizationNo, "部门id不能为空");
        Organization organization = organizationDao.findOrganizationByOrganizationNo(organizationNo);
        if (!ObjectUtils.allNotNull(organization))
            throw new OperationException(SysadminError.OrganizationNotExists);
        return organization;
    }

    @Override
    public OrganizationDto configOrganizationDto(OrganizationDto organizationDto, Long id) {
        removeSubOrganizationDtos(organizationDto, id);
        return organizationDto;
    }

    public void updateParent(OrganizationReqDto organizationReqDto) {
        Organization organization = findOrganizationById(organizationReqDto.getId());
        organization.setParent(findOrganizationById(organizationReqDto.getParentId()));
        organizationDao.save(organization);
    }

    private boolean removeSubOrganizationDtos(OrganizationDto organizationDto, Long id) {
        if (organizationDto.getId().equals(id))
            return true;
        else {
            Iterator<OrganizationDto> iter = organizationDto.getSubOrganization().iterator();
            while (iter.hasNext()) {
                if (removeSubOrganizationDtos(iter.next(), id)) {
                    iter.remove();
                    break;
                }
            }
            return false;
        }
    }

}
