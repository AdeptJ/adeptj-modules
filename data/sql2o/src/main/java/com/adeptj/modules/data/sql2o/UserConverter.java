package com.adeptj.modules.data.sql2o;

import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;

public class UserConverter implements Converter<User> {

    @Override
    public User convert(Object val) throws ConverterException {
        return new User();
    }

    @Override
    public Object toDatabaseParam(User val) {
        return null;
    }
}
