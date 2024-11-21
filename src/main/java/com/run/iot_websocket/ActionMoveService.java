package com.run.iot_websocket;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ActionMoveService {

    private final ActionMoveRepository repository;

    public ActionMoveService(ActionMoveRepository repository) {
        this.repository = repository;
    }

    public Mono<ActionMove> saveActionMove(ActionMove actionMove) {
        return repository.save(actionMove);
    }

    public Flux<ActionMove> getAllActionMoves() {
        return repository.findAll();
    }

    public Mono<ActionMove> getActionMoveById(String id) {
        return repository.findById(id);
    }

    public Mono<Void> deleteActionMove(String id) {
        return repository.deleteById(id);
    }
}

