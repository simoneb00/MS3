package org.cswteams.ms3.control.scocciatura;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.scocciature.ContestoScocciatura;
import org.cswteams.ms3.entity.scocciature.Scocciatura;
import org.cswteams.ms3.enums.PriorityQueueEnum;

import java.util.List;

public interface IControllerScocciatura {
    void updatePriorityDoctors(List<DoctorUffaPriority> allDoctorUffaPriority, ConcreteShift concreteShift, PriorityQueueEnum pq);
    void updateDoctorPriorityByValue(DoctorUffaPriority doctorUffaPriority, int priorityDelta, PriorityQueueEnum pq);
    void updateDoctorPrioritiesAfterShiftExchange(DoctorUffaPriority retiringDoctorPriority, DoctorUffaPriority substituteDoctorPriority, ConcreteShift concreteShift);
    void orderByPriority(List<DoctorUffaPriority> allDoctorUffaPriority, PriorityQueueEnum pq);
    int calcolaUffaComplessivoUtenteAssegnazione(ContestoScocciatura contestoScocciatura);
    void normalizeUffaPriority(List<DoctorUffaPriority> allDoctorUffaPriority);
    void populateScocciature();

}
