package com.techacademy.entity;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;




@Entity
@Table(name = "reports")
public class Report {

    //    @ManyToOne
    //    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    //    private Employee employee;




    //ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    //日付
    @Column
    private Date reportDate;

    //タイトル
    @Column(length = 100)
    private String title;

    //内容
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    //社員番号
    @Column(length = 10)
    private String employeeCode;

    //削除フラグ
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    //登録日時
    @Column
    private Timestamp createdAt;

    //更新日時
    @Column
    private Timestamp updatedAt;

}
