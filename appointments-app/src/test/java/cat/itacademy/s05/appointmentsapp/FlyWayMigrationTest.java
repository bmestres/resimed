package cat.itacademy.s05.appointmentsapp;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest

public class FlyWayMigrationTest {
    @Autowired
    private Flyway flyway;

    @Test
    void allMigrationsAreAppliedSuccessfully() {
        MigrationInfoService info = flyway.info();

        assertThat(info.applied()).isNotEmpty();
        assertThat(info.pending()).isEmpty();

        MigrationInfo current = info.current();
        assertThat(current).isNotNull();
        assertThat(current.getState()).isEqualTo(MigrationState.SUCCESS);
    }
}
