package com.founder.contract.sysadmin;

import java.util.Map;

public interface DictionaryService {
    Map<String, String> getDictionaryMapByType(String type);

    Map<String, Map<String, String>> getDictionaryMapsByTypes(String... types);

    String getDictionaryMapByTypeAndDtKey(String type, String dtKey);
}
