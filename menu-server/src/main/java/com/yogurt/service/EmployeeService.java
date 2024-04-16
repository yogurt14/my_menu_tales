package com.yogurt.service;

import com.yogurt.dto.EmployeeDTO;
import com.yogurt.dto.EmployeeLoginDTO;
import com.yogurt.dto.EmployeePageQueryDTO;
import com.yogurt.entity.Employee;
import com.yogurt.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    void startOrStop(Integer status, Long id);
}
