package com.example.crm.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.crm.companies.CompanyRepository;
import com.example.crm.interactions.InteractionRepository;
import com.example.crm.projectvendors.ProjectVendorRepository;
import com.example.crm.projects.ProjectRepository;
import com.example.crm.projects.ProjectStatus;
import com.example.crm.quotations.QuotationRepository;
import com.example.crm.users.UserRepository;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class ProjectDemoDataSeederTest {
    @Test
    void projectPlanUsesStableReferencesAndBalancedBusinessData() {
        var seeds = ProjectDemoDataSeeder.projectSeeds();

        assertThat(seeds).hasSize(20);
        assertThat(seeds).extracting(ProjectDemoDataSeeder.ProjectSeed::reference).doesNotHaveDuplicates();
        assertThat(seeds).extracting(ProjectDemoDataSeeder.ProjectSeed::currency).contains("USD", "EUR", "MAD");
        assertThat(seeds).extracting(ProjectDemoDataSeeder.ProjectSeed::status).containsAll(Set.of(
            ProjectStatus.PLANNED,
            ProjectStatus.IN_PROGRESS,
            ProjectStatus.ON_HOLD,
            ProjectStatus.COMPLETED,
            ProjectStatus.CANCELLED
        ));
        assertThat(ProjectDemoDataSeeder.ASSIGNMENTS_PER_PROJECT * seeds.size()).isEqualTo(80);
        assertThat(ProjectDemoDataSeeder.ACTIVITIES_PER_PROJECT * seeds.size()).isEqualTo(120);
    }

    @Test
    void seederRefusesAnyDatabaseOtherThanLocalCrmDev() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        DatabaseMetaData metadata = mock(DatabaseMetaData.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getCatalog()).thenReturn("production");
        when(connection.getMetaData()).thenReturn(metadata);
        when(metadata.getURL()).thenReturn("jdbc:postgresql://db.example.com:5432/production");

        ProjectDemoDataSeeder seeder = new ProjectDemoDataSeeder(
            mock(ProjectRepository.class),
            mock(ProjectVendorRepository.class),
            mock(QuotationRepository.class),
            mock(InteractionRepository.class),
            mock(CompanyRepository.class),
            mock(UserRepository.class),
            dataSource
        );

        assertThatThrownBy(seeder::seed)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("local crm_dev");
    }
}
