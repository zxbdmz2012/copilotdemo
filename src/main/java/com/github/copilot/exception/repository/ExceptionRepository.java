package com.github.copilot.exception.repository;

import com.github.copilot.exception.entity.ExceptionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExceptionRepository extends JpaRepository<ExceptionInfo, Long> {
}
