package com.collaberaproducts.collaberaproducts.util;

import org.apache.commons.lang3.RandomStringUtils;

public class IdGenerator {

    /**
     * This method is used to generate random String
     * @param count number of characters
     * @return Random String
     */
    public String generate(final int count){
        return RandomStringUtils.random(count, true, true);
    }
}
