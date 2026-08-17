package iTicket.Douglas.Evaluaciones.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class MetricasDTO {

        private Long totalEvaluaciones;
        private Double promedio;
        private Long positivas;
        private Long negativas ;

    }
