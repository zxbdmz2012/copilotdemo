package com.github.copilot.exceptionhandler.repository;

import com.github.copilot.exceptionhandler.entity.ExceptionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExceptionRepository extends JpaRepository<ExceptionInfo, Long> {
}
