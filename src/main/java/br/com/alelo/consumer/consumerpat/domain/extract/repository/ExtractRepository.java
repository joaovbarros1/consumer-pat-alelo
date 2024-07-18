package br.com.alelo.consumer.consumerpat.domain.extract.repository;

import br.com.alelo.consumer.consumerpat.domain.extract.entity.Extract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtractRepository extends JpaRepository<Extract, Long> {

    Page<Extract> findAllByCardId(Long id, Pageable pagination);
}
