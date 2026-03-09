package com.revconnect.entity;

import com.revconnect.enums.ConnectionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "CONNECTIONS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"SENDER_ID", "RECEIVER_ID"})
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CONNECTION_ID")
    private Long connectionId;


    // =========================
    // Sender (who sent request)
    // =========================
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SENDER_ID", nullable = false)
    private User sender;


    // =========================
    // Receiver (who receives request)
    // =========================
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECEIVER_ID", nullable = false)
    private User receiver;


    // =========================
    // Connection Status
    // =========================
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private ConnectionStatus status;


    // =========================
    // Audit Fields
    // =========================
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;



    // =========================
    // Auto set created time
    // =========================
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

        if (status == null) {
            status = ConnectionStatus.PENDING;
        }

        validateConnection();
    }


    // =========================
    // Auto set updated time
    // =========================
    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

        validateConnection();
    }


    // =========================
    // Prevent self connection
    // =========================
    private void validateConnection() {

        if (sender != null && receiver != null &&
                sender.getUserId().equals(receiver.getUserId())) {

            throw new IllegalStateException("User cannot connect with themselves");
        }
    }

}