package br.com.businfo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Horário de saída de uma linha (relacionamento N:1 com Linha). */
@Entity
@Table(name = "horarios")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String horario;

    @Column(name = "dia_semana", nullable = false, length = 40)
    private String diaSemana;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "linha_id", nullable = false)
    private Linha linha;

    protected Horario() {
    }

    public Long getId() {
        return id;
    }

    public String getHorario() {
        return horario;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public Linha getLinha() {
        return linha;
    }
}
