package com.yogurt.controller.admin;

import com.yogurt.constant.JwtClaimsConstant;
import com.yogurt.dto.EmployeeDTO;
import com.yogurt.dto.EmployeeLoginDTO;
import com.yogurt.dto.EmployeePageQueryDTO;
import com.yogurt.entity.Employee;
import com.yogurt.properties.JwtProperties;
import com.yogurt.result.PageResult;
import com.yogurt.result.Result;
import com.yogurt.service.EmployeeService;
import com.yogurt.utils.JwtUtil;
import com.yogurt.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result save(
            @RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工:{}",employeeDTO); //是占位符，运行时将后面的参数放进去
        System.out.println("当前线程的id：" + Thread.currentThread().getId());
        //创建save方法
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数为：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);//下一步创建pageQuery方法
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用员工账号")
    public Result startOrStop(
            @PathVariable Integer status,
            Long id
    ){
        log.info("启用/禁用员工账号:{},{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }



}
