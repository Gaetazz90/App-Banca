package com.example.Bank_App.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transazione_schedulata")
@EntityListeners(AuditingEntityListener.class)
public class TransazioneSchedulata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Check(constraints = "amount > 0", name = "check_amount_positive")
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private LocalDateTime publishTime;
    @ManyToOne
    @JoinColumn(name = "conto_mittente")
    private Conto contoMittente;
    @ManyToOne(optional = false)
    @JoinColumn(name = "conto_destinatario")
    private Conto contoDestinatario;
    @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id")
    private Utente utente;
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

}
