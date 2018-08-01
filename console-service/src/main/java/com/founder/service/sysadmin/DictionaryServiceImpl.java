package com.founder.service.sysadmin;

import com.founder.contract.sysadmin.DictionaryService;
import com.founder.dao.sysadmin.DictionaryDao;
import com.founder.domain.sysadmin.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DictionaryServiceImpl implements DictionaryService {

    @Autowired
    private DictionaryDao dictionaryDao;


    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getDictionaryMapByType(String type) {
        return getByType(type);
    }

    private Map<String, String> getByType(String type) {
        return dictionaryDao.getByTypeOrderByDtOrder(type).stream().collect(Collectors.toMap(Dictionary::getDtKey, Dictionary::getDtValue));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Map<String, String>> getDictionaryMapsByTypes(String... types) {
        return Arrays.stream(types).collect(Collectors.toMap(String::toString, this::getByType));
    }

    @Override
    @Transactional(readOnly = true)
    public String getDictionaryMapByTypeAndDtKey(String type, String dtKey) {
        return getDictionaryMapByType(type).get(dtKey);
    }

}
