package uz.warcom.contest.persistence.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories("uz.warcom.contest.persistence")
//@ComponentScan("uz.warcom.contest.persistence")
@EntityScan("uz.warcom.contest.persistence")
class PersistenceConfiguration {
}