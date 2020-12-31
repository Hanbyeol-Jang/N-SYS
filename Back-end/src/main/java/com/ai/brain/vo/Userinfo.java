package com.ai.brain.vo;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "userinfo")

public class Userinfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int uPk;


    @Column(name="u_id")
    private String uiId;
    
    @Column(name="u_pw")
    private String uiPw;
    
    @Column(name="u_name")
    private String uiName;

    @Column(name="u_image")
    private String uiImage;

    @Column(name="u_imgtype")
    private String uiImgtype;

}
