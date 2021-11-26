package ru.kazachkov.statemachinedemo.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "process_task")
public class ProcessTask {
    @Id
    private UUID id;
    private UUID parentId;
    private UUID processId;
    private String processName;
    private ProcessStates status;
}
