package br.com.alelo.consumer.consumerpat.controller;

import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataCreateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataListConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataUpdateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.repository.ConsumerRepository;
import br.com.alelo.consumer.consumerpat.domain.consumer.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/consumer-pat/v1/consumers")
public class ConsumerController {

    @Autowired
    private ConsumerRepository repository;

    @Autowired
    private ConsumerService service;

    /* Listar todos os clientes (obs.: tabela possui cerca de 50.000 registros - incluso a paginação) */
    @GetMapping
    public ResponseEntity<Page<DataListConsumer>> list(@PageableDefault(size = 20, page = 0, sort = {"name"}) Pageable pagination) {
        var page = repository.findAllByActiveTrue(pagination).map(DataListConsumer::new);

        return ResponseEntity.ok(page);
    }

    /* Cadastrar novos clientes */
    @PostMapping
    @Transactional
    public ResponseEntity<DataListConsumer> create(@RequestBody @Valid DataCreateConsumer data, UriComponentsBuilder uriBuilder) {

        var consumer = service.createConsumer(data);

        var uri = uriBuilder.path("/consumer-pat/v1/consumers/{id}").buildAndExpand(consumer.getId()).toUri();

        return ResponseEntity.created(uri).body(new DataListConsumer(consumer));
    }

    /* Detalhar um determinado cliente - somente clientes ativos */
    @GetMapping("/{id}")
    public ResponseEntity<DataListConsumer> detail(@PathVariable Long id) {
        var consumer = service.detailConsumer(id);
        return ResponseEntity.ok(new DataListConsumer(consumer));
    }

    /* Atualizar um cliente - somente ativos - não é possível alterar o saldo do cartão, tem um controller do cartão que faz isso */
    @PutMapping
    @Transactional
    public ResponseEntity<DataListConsumer> update(@RequestBody @Valid DataUpdateConsumer data) {
        var consumer = service.updateConsumer(data);

        return ResponseEntity.ok(new DataListConsumer(consumer));
    }

    /* Deletando um cliente - exclusão lógica */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var consumer = repository.getReferenceById(id);
        service.deleteConsumer(consumer);

        return ResponseEntity.noContent().build();
    }
}
