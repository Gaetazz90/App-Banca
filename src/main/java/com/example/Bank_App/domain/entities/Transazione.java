package com.example.Bank_App.domain.entities;



import com.example.Bank_App.domain.enums.TipoOperazione;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Table(name = "transazione")
@EntityListeners(AuditingEntityListener.class)
public class Transazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Check(constraints = "amount > 0", name = "check_amount_positive")
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @ManyToOne
    @JoinColumn(name = "conto_mittente")
    private Conto contoMittente;
    @ManyToOne(optional = false)
    @JoinColumn(name = "conto_destinatario")
    private Conto contoDestinatario;
    @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id")
    private Utente utente;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOperazione tipoOperazione;
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

}
