package com.yogurt.mapper;

import com.github.pagehelper.Page;
import com.yogurt.dto.EmployeePageQueryDTO;
import com.yogurt.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据  单表sql，直接用注解
     * @param employee
     */
    @Insert("insert into employee(name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user,status) " +
            "values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})"
    )
    void insert(Employee employee);

    /**
     * 分页查询  要用到动态sql，所以就要写在映射文件
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);


    /**
     * 根据主键动态修改属性（状态）
     * @param employee
     */
    void update(Employee employee);

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Select("select * from  employee where id = #{id}")
    Employee getById(Long id);

}
