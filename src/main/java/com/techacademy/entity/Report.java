package com.techacademy.entity;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;


@Entity
@Table(name = "reports")
public class Report {

    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;


    //ID
    @Id
    @NotEmpty
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    //日付
    @Column
    @NotEmpty
    private LocalDate reportDate;

    //タイトル
    @Column(length = 100, nullable = false)
    @NotEmpty
    private String title;

    //内容
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    @NotEmpty
    private String content;

    //社員番号
    //@Column(length = 10)
    //@NotEmpty
    //private String employeeCode;

    //削除フラグ
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    //登録日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    //更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
