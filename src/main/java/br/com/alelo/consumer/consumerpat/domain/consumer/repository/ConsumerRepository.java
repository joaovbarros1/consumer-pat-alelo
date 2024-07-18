package br.com.alelo.consumer.consumerpat.domain.consumer.repository;

import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {

    Consumer findByIdAndActiveTrue(Long id);

    Page<Consumer> findAllByActiveTrue(Pageable pagination);
}
