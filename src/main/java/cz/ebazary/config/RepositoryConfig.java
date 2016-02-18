package cz.ebazary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;

@Configuration
public class RepositoryConfig {

    @Configuration
    public static class CassandraRepositoryConfig extends AbstractCassandraConfiguration {

        @Override
        protected String getKeyspaceName() {
            return "items";
        }

        @Override
        public SchemaAction getSchemaAction() {
            return SchemaAction.NONE;
        }

        @Override
        public String[] getEntityBasePackages() {
            return new String[]{"cz.ebazary.model"};
        }

    }

}
