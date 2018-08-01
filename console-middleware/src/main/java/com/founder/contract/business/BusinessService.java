package com.founder.contract.business;

import com.founder.domain.business.Business;
import com.founder.domain.business.BusinessOperation;
import com.founder.domain.business.ClientUser;
import com.founder.dto.business.BusinessValueDto;

import java.util.List;
import java.util.Set;

public interface BusinessService {


    List<Business> findAllBusinessByUser(ClientUser user);

    Business create(Business business, ClientUser clientUser);

    BusinessValueDto findBusinessValueDtoById(Long businessId,ClientUser user);

    Business update(BusinessValueDto businessValueDto,ClientUser user);

    void complete(Long businessId,ClientUser user);

    void delete(Long businessId,ClientUser user);






}
