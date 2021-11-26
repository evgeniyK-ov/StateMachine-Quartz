package ru.kazachkov.statemachinedemo.models.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kazachkov.statemachinedemo.models.entities.ProcessTask;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProcessTaskRepository extends JpaRepository<ProcessTask, UUID> {


    Optional<ProcessTask> findByIdAndStatusIn(UUID id, ProcessStates... processStates);

    List<ProcessTask> findByIdInAndStatusIn(List<UUID> ids, ProcessStates... processStates);

    List<ProcessTask> findByParentIdInAndStatusIn(List<UUID> ids, ProcessStates... processStates);
}
