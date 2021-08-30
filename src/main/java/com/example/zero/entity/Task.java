package com.example.zero.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tsk_id")
    private Long id;

    @Column(name = "tsk_description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tsk_assignee_usr_id")
//    @Column(name = "tsk_assignee_usr_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tsk_reporter_usr_id")
//    @Column(name = "tsk_reporter_usr_id")
    private User reporter;

    @Column(name = "tsk_created_date")
    private LocalDate createdDate = LocalDate.now();

    @Builder.Default
    @Enumerated
    @Column(name = "tsk_status")
    private TaskStatus taskStatus = TaskStatus.BACKLOG;

    @Builder.Default
    @Enumerated
    @Column(name = "tsk_priority")
    private TaskPriority taskPriority = TaskPriority.MINOR;

    public Task(Long id) {
        this.id = id;
    }

}
