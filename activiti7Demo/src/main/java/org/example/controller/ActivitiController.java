package org.example.controller;


import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.example.bo.CandidateParam;
import org.example.bo.ProcessParam;
import org.example.bo.VarParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流测试
 *
 * @ClassName MyTestController
 * @Description
 * @Date 2023/08/14 13:02:00 星期一
 * @Version 1.0
 */
@Slf4j
@RestController
public class ActivitiController {
    @Resource
    RepositoryService repositoryService; // 部署类
    @Resource
    RuntimeService runtimeService;  // 运行类
    @Resource
    TaskService taskService;  // 任务类
    @Resource
    HistoryService historyService;  // 历史记录类

    /**
     * 部署-指定xml文件名
     *
     * @return String String
     */
    @PostMapping("/deployByFile")
    public String deployByFile(@RequestBody ProcessParam processParam) {
        Deployment deploy = null;
        String filename = processParam.getFilename();
        try {
            // module内文件相对路径，从module目录下开始
            FileInputStream fis = new FileInputStream("activiti7Demo/src/main/resources/prodef/" + filename + ".bpmn20.xml");
            deploy = repositoryService.createDeployment()
                    .name(filename)  // 流程的名称NAME_字段
                    .addInputStream(filename + ".bpmn20.xml", fis)  // 流程定义中的名称RESOURCE_NAME_字段
                    .deploy();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        log.info("部署成功\n部署的id是-> {}\n部署的名称是-> {}", deploy.getId(), deploy.getName());
        return "ok";
    }

    /**
     * 部署名称查询部署List
     * 部署名称是可以重复的
     *
     * @return Object Object
     */
    @PostMapping("/deploy/getByName")
    public String queryDeploy(@RequestBody ProcessParam processParam) {
        String deployName = processParam.getDeployName();
        //根据名称获取流程部署id
        DeploymentQuery deployQuery = repositoryService.createDeploymentQuery();
        List<Deployment> deployments = deployQuery.deploymentName(deployName).list();
        if (deployments.size() == 0) {
            return "没有查询到相关部署信息";
        }
        log.info("部署列表:");
        for (Deployment deployment : deployments) {
            log.info(deployment.getId());
        }
        return "ok";
    }

    /**
     * 部署id查询流程List
     * 一条部署id可以对应多条部署定义，但建议一对一
     */
    @PostMapping("/define/getByDeployId")
    public String getProcessDefineInfoByDeployId(@RequestBody ProcessParam processParam) {
        List<ProcessDefinition> processDefs = repositoryService.createProcessDefinitionQuery()
                .deploymentId(processParam.getDeployId())
                .list();
        log.info("流程定义列表:");
        for (ProcessDefinition processDef : processDefs) {
            log.info(processDef.getId());
        }
        return "ok";
    }

    /**
     * 流程定义id启动一个流程实例
     * 流程id (long) 和流程key(String 长度255)是唯一的
     * 都可以作为启动实例的入参
     *
     * @return String String
     */
    @PostMapping("/instance/startByDefineId")
    public String startProcess(@RequestBody ProcessParam processParam) {
        //根据流程定义key启动流程
//        runtimeService.startProcessInstanceByKey("myProcessDefinitionKey");
        //根据流程定义id启动流程
        ProcessInstance proInstance = runtimeService.startProcessInstanceById(processParam.getProcessDefineId());
        //业务id->BusinessKey自己设置,但没有强制要求唯一,索引形式是normal
        log.info("流程实例主键-> {} \n 关联的业务id-> {}", proInstance.getId(), proInstance.getBusinessKey());
        return "ok";
    }


    /**
     * 流程实例id查询当前活动任务List
     */
    @PostMapping("/task/getByInstanceId")
    public String queryActiveTask(@RequestBody ProcessParam processParam) {
        TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(processParam.getProcessInstanceId());
        List<Task> tasks = taskQuery.list();
        log.info("任务列表:");
        for (Task task : tasks) {
            log.info(task.getId());
        }
        log.info(tasks.toString());
        return "ok";
    }

    /**
     * 任务id完成任务
     */
    @PostMapping("/task/completeByTaskId")
    public String completeTaskById(@RequestBody ProcessParam processParam) {
        taskService.complete(processParam.getTaskId());
        log.info("任务完成");
        return "ok";
    }

    /**
     * 流程实例id,查询流程实例对象信息
     */
    @PostMapping("/instance/getById")
    public String getAllInfoOfProcessInstance(@RequestBody ProcessParam processParam) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().processInstanceId(processParam.getProcessInstanceId());
        ProcessInstance processInstance = query.singleResult();
        log.info(processInstance.toString());
        return "ok";
    }

    /**
     * 为流程实例自定义变量
     */
    @PostMapping("/var/instance/set")
    public String setVariable4ProcessInstance(@RequestBody VarParam varParam) {
        String processInstanceId = varParam.getProcessInstanceId();
        String key = varParam.getKey();
        String value = varParam.getValue();
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        runtimeService.setVariables(processInstanceId, map);
        log.info("流程 {} 设置变量 key: {} value: {}", processInstanceId, key, value);
        return "ok";
    }

    /**
     * 查询为流程实例自定义的变量
     */
    @PostMapping("/var/instance/get")
    public String getVariable4ProcessInstance(@RequestBody VarParam varParam) {
        String processInstanceId = varParam.getProcessInstanceId();
        String key = varParam.getKey();
        Object variable = runtimeService.getVariable(processInstanceId, key);
        log.info("流程 {} 查询变量 key {}  value {}", processInstanceId, key, variable);
        return "ok";
    }

    /**
     * 根据任务id设置变量
     */
    @PostMapping("/var/task/set")
    public String setVariable4Task(@RequestBody VarParam varParam) {
        String taskId = varParam.getTaskId();
        String key = varParam.getKey();
        String value = varParam.getValue();
        taskService.setVariable(taskId, key, value);
        log.info("任务 {}  设置变量 key {}  value {}", taskId, key, value);
        return "ok";
    }

    /**
     * 获取根据任务id自定义的变量
     */
    @PostMapping("/var/task/get")
    public String getVariable4Task(@RequestBody VarParam varParam) {
        String taskId = varParam.getTaskId();
        String key = varParam.getKey();
        Object variable = taskService.getVariable(taskId, key);
        log.info("任务 {} 自定义变量 key {} value {}", taskId, key, variable);
        return "ok";
    }

    /**
     * 判断流程实例结束
     */
    @PostMapping("/instance/state")
    public String getProcessInstanceStatus(@RequestBody ProcessParam processParam) {
        String processInstanceId = processParam.getProcessInstanceId();
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId);
        HistoricProcessInstance history = query.singleResult();
        if (history != null && history.getEndTime() != null) {
            return "流程已结束! 历史记录:"+ history.toString();
        } else {
            return "流程未结束";
        }
    }

    /**
     * 强制删除流程实例
     */
    @PostMapping("/instance/delete")
    public String deleteProcessInstance(@RequestBody ProcessParam processParam) {
        String processInstanceId = processParam.getProcessInstanceId();
        runtimeService.deleteProcessInstance(processInstanceId, "删除原因......");
        return "ok";
    }

    /**
     * 根据候选人查询任务-候选人是预设的
     */
    @PostMapping("/task/assignee")
    public String queryTasksByCandidate(@RequestBody CandidateParam candidateParam) {
        String assignee = candidateParam.getAssignee();
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();
        log.info("指定人任务列表:");
        for (Task task : tasks) {
            log.info(task.toString());
        }
        return "ok";
    }

    /**
     * 根据候选用户们查询任务-候选人是预设的
     */
    @PostMapping("/task/candidate")
    public String queryTasksByCandidateList(@RequestBody CandidateParam candidateParam) {
        String candidate = candidateParam.getCandidate();
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateUser(candidate)
                .list();
        log.info("候选人任务列表:");
        for (Task task : tasks) {
            log.info(task.toString());
        }
        return "ok";
    }

}
