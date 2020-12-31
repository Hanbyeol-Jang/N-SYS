package com.ai.brain.vo;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Play")
public class PlayVo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int pPk;

    @Column(name="u_pk")
    private int uiPk;

    @Column(name="g_id")
    private int gaId;

    @Column(name="p_score")
    private int plScore;

    @Column(name="p_victory")
    private int plVictory;

    @Column(name="p_level")
    private int plLevel;

}
