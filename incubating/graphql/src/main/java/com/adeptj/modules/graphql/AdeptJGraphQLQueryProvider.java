package com.adeptj.modules.graphql;

import graphql.kickstart.servlet.osgi.GraphQLQueryProvider;
import graphql.schema.GraphQLFieldDefinition;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component(immediate = true)
public class AdeptJGraphQLQueryProvider implements GraphQLQueryProvider {

    @Override
    public Collection<GraphQLFieldDefinition> getQueries() {
        return new ArrayList<>();
    }
}
