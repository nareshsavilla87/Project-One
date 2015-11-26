package org.obsplatform.portfolio.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientCardDetailsRepository extends JpaRepository<ClientCardDetails, Long>, JpaSpecificationExecutor<ClientCardDetails> {

}
