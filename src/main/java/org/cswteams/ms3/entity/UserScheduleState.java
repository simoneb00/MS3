package org.cswteams.ms3.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.Data;
import org.cswteams.ms3.entity.doctor.Doctor;

@Entity
@Data
@Table(uniqueConstraints={
    @UniqueConstraint(columnNames={
        "user_id",
        "schedule_id",
    })
})

public class UserScheduleState {
    
    @Id
    @GeneratedValue
    private Long id;
    
    /**  Utente a cui appartiene questo stato */
    @ManyToOne
    private Doctor doctor;

    /**  Pianificazione a cui appartiene questo stato */
    @OneToOne
    private Schedule schedule;

    private int uffaParziale=0;
    private int uffaCumulativo=0;

    /** tutti i turni assegnati a questo utente nella pianificazione corrente */
    @Transient
    List<AssegnazioneTurno> assegnazioniTurnoCache;



    public List<AssegnazioneTurno> getAssegnazioniTurnoCache(){
        
        if (assegnazioniTurnoCache == null){
            this.assegnazioniTurnoCache = new ArrayList<>();
            for (AssegnazioneTurno at: schedule.getAssegnazioniTurno()){
                for (Doctor collega : at.getUtenti()){
                    if (collega.getId() == this.doctor.getId()){
                        assegnazioniTurnoCache.add(at);
                        break;
                    }
                }
            }
        }
        return assegnazioniTurnoCache;
    }

    /**Aggiunge in ordine la nuova assegnazione alla lista delle assegnazioni dell'utente **/
    public void addAssegnazioneTurno(AssegnazioneTurno nuovaAssegnazione){
        List<AssegnazioneTurno> turniAssegnati = getAssegnazioniTurnoCache();
        int idInsert = turniAssegnati.size();
        for(int i = 0; i < turniAssegnati.size(); i++){
            if(turniAssegnati.get(i).getData().isAfter(nuovaAssegnazione.getData()) || turniAssegnati.get(i).getData().isEqual(nuovaAssegnazione.getData())){
                if(turniAssegnati.get(i).getTurno().getOraInizio().isAfter(nuovaAssegnazione.getTurno().getOraInizio())) {
                    idInsert = i;
                }
            }
        }
        turniAssegnati.add(idInsert,nuovaAssegnazione);
    }

    public void saveUffaTemp(){
        this.uffaCumulativo = this.uffaParziale;
    }

    public void addUffaTemp(int uffa){
        this.uffaParziale =this.uffaCumulativo+ uffa;
    }

    public UserScheduleState() {
    }
    
    public UserScheduleState(Doctor doctor, Schedule schedule) {
        this.doctor = doctor;
        this.schedule = schedule;
    }
}
