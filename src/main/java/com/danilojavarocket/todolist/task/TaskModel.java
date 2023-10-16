package com.danilojavarocket.todolist.task;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * ID
 * Usuário
 * Descrição
 * Título
 * Data de Início
 * Data de Término
 * Prioridade
 *
 */
@Getter
@Entity(name = "tb_tasks")
public class TaskModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Setter
    private UUID idUser;

    @Setter
    private String description;

    @Setter
    @Column(length = 50)
    private String title;

    @Setter
    private LocalDateTime startAt;

    @Setter
    private LocalDateTime endAt;

    @Setter
    private String priority;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void setTitle(String title) throws Exception{
        if (title.length() > 50){
            throw new Exception("O campo title deve conter no máximo 50 caracteres");
        }
        this.title = title;
    }

}
