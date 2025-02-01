package com.example.Bank_App.domain.dto.mappers;



import com.example.Bank_App.domain.dto.responses.TransazioneResponse;
import com.example.Bank_App.domain.entities.Transazione;
import org.springframework.stereotype.Service;

@Service
public class TransazioneMapper {

    public TransazioneResponse fromTransazioneToResponse(Transazione transazione) {
        return TransazioneResponse.
                builder()
                .id(transazione.getId())
                .id_conto_mittente(transazione.getContoMittente() == null ? null : transazione.getContoMittente().getId())
                .id_conto_destinatario(transazione.getContoDestinatario().getId())
                .id_utente(transazione.getUtente().getId())
                .amount(transazione.getAmount())
                .timestamp(transazione.getTimestamp())
                .tipoOperazione(transazione.getTipoOperazione().getNomeOperazione())
                .build();
    }

}
