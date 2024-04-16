package com.yogurt.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yogurt.constant.MessageConstant;
import com.yogurt.constant.PasswordConstant;
import com.yogurt.constant.StatusConstant;
import com.yogurt.context.BaseContext;
import com.yogurt.dto.EmployeeDTO;
import com.yogurt.dto.EmployeeLoginDTO;
import com.yogurt.dto.EmployeePageQueryDTO;
import com.yogurt.entity.Employee;
import com.yogurt.exception.AccountLockedException;
import com.yogurt.exception.AccountNotFoundException;
import com.yogurt.exception.PasswordErrorException;
import com.yogurt.mapper.EmployeeMapper;
import com.yogurt.result.PageResult;
import com.yogurt.service.EmployeeService;
import org.apache.poi.util.PackageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对前端传过来的明文进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO) {

        System.out.println("当前线程的id：" + Thread.currentThread().getId());

        //传进来的是DTO，为了方便封装前端提交过来的数据，但是最终传到持久层还是建议使用实体类，将DTO转成实体
        Employee employee = new Employee();

        //里面属性是空的，所以需要设置
        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO,employee);//从employeeDTO拷贝到employee

        //设置账号状态，默认正常 1正常 0锁定  StatusConstant常量类
        employee.setStatus(StatusConstant.ENABLE);

        //设置密码，密码默认123456  PasswordConstant常量类
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置当前记录的创建时间、修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //设置当前记录的创建人id、修改人id
        // TODO 后期需要改为当前登录用户的id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        //调用持久层把数据插入  创建insert方法
        employeeMapper.insert(employee);
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //select * from employee limit 0,10
        //开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);//下一步编写pageQuery方法

        long total = page.getTotal();
        List<Employee> records = page.getResult();

        return new PageResult(total,records);
    }

    /**
     * 启用/禁用员工账号
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        //update employee set status = ? where id =?
        /*Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);*/
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);//总的update，而不是只针对修改状态，所以传进来对象
    }



    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        //不想让密码被看到
        employee.setPassword("*****");
        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //把属性复制过去
        BeanUtils.copyProperties(employeeDTO,employee);

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.update(employee);

    }
}
