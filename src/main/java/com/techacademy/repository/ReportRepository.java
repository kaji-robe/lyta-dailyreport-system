package com.techacademy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    // 論理削除されていない全ての日報を取得
    List<Report> findByDeleteFlgFalse();


    // 特定の従業員に関連し、論理削除されていない日報を取得
    List<Report> findByEmployeeAndDeleteFlgFalse(Employee employee);


    // 特定のEmployeeに関連するReportのリストを取得
    List<Report> findByEmployee(Employee employee);
}