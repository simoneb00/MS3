package org.cswteams.ms3.control.assegnazioneTurni;

import org.cswteams.ms3.control.utils.MappaAssegnazioneTurni;
import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dao.ScheduleDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Service
public class ControllerAssegnazioniTurni implements IControllerAssegnazioneTurni{
    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private  TurnoDao turnoDao;

    @Autowired
    private ScheduleDao scheduleDao;

    /**
     *
     * @return
     */
    @Override
    public Set<AssegnazioneTurnoDTO> leggiTurniAssegnati()  {
        Set<ConcreteShift> turniSet = new HashSet<>(assegnazioneTurnoDao.findAll());
        Set<AssegnazioneTurnoDTO> turniDTOSet = MappaAssegnazioneTurni.assegnazioneTurnoToDTO(turniSet);
        return turniDTOSet;
    }

    /**
     *
     * @param dto
     * @return
     * @throws AssegnazioneTurnoException
     */
    @Override
    public ConcreteShift creaTurnoAssegnato(@NotNull RegistraAssegnazioneTurnoDTO dto) throws AssegnazioneTurnoException {

        Shift shift = turnoDao.findAllByServizioNomeAndTipologiaTurno(dto.getServizio().getNome(), dto.getTipologiaTurno()).get(0);
        if(shift == null)
            throw new AssegnazioneTurnoException("Non esiste un shift con la coppia di attributi servizio: "+dto.getServizio().getNome() +",tipologia shift: "+dto.getTipologiaTurno().toString());

        ConcreteShift concreteShift = new ConcreteShift(LocalDate.of(dto.getAnno(),dto.getMese(),dto.getGiorno()), shift, MappaUtenti.utenteDTOtoEntity(dto.getUtentiReperibili()),MappaUtenti.utenteDTOtoEntity(dto.getUtentiDiGuardia()));

        return assegnazioneTurnoDao.save(concreteShift);
    }

    /**
     *
     * @param idPersona
     * @return
     * @throws ParseException
     */
    @Override
    public Set<AssegnazioneTurnoDTO> leggiTurniUtente(@NotNull Long idPersona) throws ParseException {
        Set<ConcreteShift> turniAllocatiERiserve = assegnazioneTurnoDao.findTurniUtente(idPersona);
        Set<AssegnazioneTurnoDTO> turniAllocati = new HashSet<>();
        for(ConcreteShift concreteShift : turniAllocatiERiserve){
            if(concreteShift.getShift().isReperibilitaAttiva() || !utenteInReperibilita(concreteShift, idPersona))
                turniAllocati.add(MappaAssegnazioneTurni.assegnazioneTurnoToDTO(concreteShift));
        }
        return turniAllocati;
    }

    private boolean utenteInReperibilita(ConcreteShift concreteShift, Long idPersona){
        for(Doctor doctorReperibile : concreteShift.getUtentiReperibili()){
            if(doctorReperibile.getId().longValue() == idPersona.longValue())
                return true;
        }
        return false;
    }


    @Override
    public ConcreteShift leggiTurnoByID(long idAssegnazione) {
        return assegnazioneTurnoDao.findById(idAssegnazione).get();
    }


}
