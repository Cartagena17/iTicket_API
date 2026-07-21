package iTicket.Douglas.Evaluaciones.Entity;


import jakarta.persistence.*;

public class EvaluacionesEntity {
    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "SEQ_EVALUACIONES")
    @SequenceGenerator(name = "SEQ_EVALUACIONES", sequenceName = "SEQ_CATEGORIAS", allocationSize = 1)
    @Column(name = "ID_EVALUACIONES")
    private long idEvaluaciones;
    @Column(name = "CALIFICACIONES")
    private double calificacion;
    @Column(name = "COMEANTRIO")
    private String comentario;
}
