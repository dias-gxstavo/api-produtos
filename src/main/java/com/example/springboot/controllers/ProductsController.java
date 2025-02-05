package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductRecordDTO;
import com.example.springboot.entities.Produto;
import com.example.springboot.repositories.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/produtos")
public class ProductsController {

    @Autowired
    public ProdutoRepository repository;

    @PostMapping
    public ResponseEntity<Produto> cadastrarProduto(@RequestBody @Valid ProductRecordDTO productRecordDTO){
        var produto = new Produto();
        // Convertendo DTO em Service
        BeanUtils.copyProperties(productRecordDTO, produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(produto));
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodosProdutos(){
        List<Produto> list = repository.findAll();

        if(!list.isEmpty()){
            for (Produto produto: list){
                UUID id = produto.getId();
                produto.add(linkTo(methodOn(ProductsController.class).listarProduto(id)).withRel("Listagem de produtos"));
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> listarProduto(@PathVariable UUID id){
        Optional<Produto> produtoOp = repository.findById(id);

        if(produtoOp.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
        produtoOp.get().add(linkTo(methodOn(ProductsController.class).listarTodosProdutos()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(produtoOp.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarProduto(@RequestBody @Valid ProductRecordDTO productRecordDTO, @PathVariable UUID id){
        Optional<Produto> produtoOp = repository.findById(id);

        if (produtoOp.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O produto não pode ser atualizado.");
        }

        var produto = produtoOp.get();
        BeanUtils.copyProperties(productRecordDTO, produto);
        return ResponseEntity.status(HttpStatus.OK).body(repository.save(produto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarProduto(@PathVariable UUID id){
        Optional<Produto> produtoOp = repository.findById(id);

        if (produtoOp.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O produto não pode ser deletado.");
        }

        repository.delete(produtoOp.get());
        return ResponseEntity.status(HttpStatus.OK).body("O produto foi excluído com sucesso.");
    }
}
