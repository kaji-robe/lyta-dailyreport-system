package com.techacademy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

import java.time.LocalDate;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Integer> {

//    // ■■ JavaScriptバージョン
//    // 論理削除されていない全ての日報を取得
//    List<Report> findByDeleteFlgFalse();
//
//    // 特定の従業員に関連し、論理削除されていない日報を取得
//    List<Report> findByEmployeeAndDeleteFlgFalse(Employee employee);
//
//    // 特定のEmployeeに関連するReportのリストを取得
//    List<Report> findByEmployee(Employee employee);
//
//   // 日付の重複チェック
//    Optional<Report> findByEmployeeAndReportDate(Employee employee, LocalDate reportDate);


    // ■■ SQLバージョン
    @Query("SELECT r FROM Report r WHERE r.deleteFlg = false")
    List<Report> findByDeleteFlgFalse();

    @Query("SELECT r FROM Report r WHERE r.employee = :employee AND r.deleteFlg = false")
    List<Report> findByEmployeeAndDeleteFlgFalse(Employee employee);

    @Query("SELECT r FROM Report r WHERE r.employee = :employee")
    List<Report> findByEmployee(Employee employee);

    @Query("SELECT r FROM Report r WHERE r.employee = :employee AND r.reportDate = :reportDate")
    Optional<Report> findByEmployeeAndReportDate(Employee employee, LocalDate reportDate);

    }
