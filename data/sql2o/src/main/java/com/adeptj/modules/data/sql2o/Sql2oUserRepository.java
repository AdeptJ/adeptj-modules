package com.adeptj.modules.data.sql2o;

import com.adeptj.modules.data.sql2o.core.AbstractSql2oRepository;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;

@Component(service = {Sql2oUserRepository.class, Sql2oRepository.class})
public class Sql2oUserRepository extends AbstractSql2oRepository<User, Long> {

    @Override
    public List<ColumnMapping> getDefaultColumnMappings() {
        List<ColumnMapping> columnMappings = new ArrayList<>();
        columnMappings.add(new ColumnMapping("ID", "id"));
        columnMappings.add(new ColumnMapping("FIRST_NAME", "firstName"));
        columnMappings.add(new ColumnMapping("LAST_NAME", "lastName"));
        columnMappings.add(new ColumnMapping("EMAIL", "email"));
        columnMappings.add(new ColumnMapping("MOBILE_NO", "contact"));
        columnMappings.add(new ColumnMapping("SECONDARY_MOBILE_NO", "alternateContact"));
        columnMappings.add(new ColumnMapping("GOVT_ID", "govtId"));
        return columnMappings;
    }
}
