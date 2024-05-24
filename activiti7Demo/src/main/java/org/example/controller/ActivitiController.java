package org.example.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MyTestController
 * @Description
 * @Date 2023/08/14 13:02:00 星期一
 * @Version 1.0
 */
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
     * 根据bpm文件进行部署
     *
     * @param filename filename
     * @return String String
     */
    @GetMapping("/deploy/{filename}")
    public String deploy(@PathVariable String filename) {
        Deployment deploy = null;
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
        return "部署成功,部署的id是:" + deploy.getId() + ";\n部署的名称是:" + deploy.getName();
    }

    /**
     * 根据部署名称查询部署信息列表
     * 部署名称是可以重复的
     *
     * @param name name
     * @return Object Object
     */
    @GetMapping("/deploy/query/{deployName}")
    public String queryDeploy(@PathVariable("deployName") String name) {
        //根据名称获取流程部署id
        DeploymentQuery deployQuery = repositoryService.createDeploymentQuery();
        List<Deployment> deployments = deployQuery.deploymentName(name).list();
        if (deployments.size() == 0) {
            return "没有查询到相关部署信息";
        }
        for (Deployment deployment : deployments) {
            String string = deployment.toString();
            System.out.println(string);
        }
        return "直接toString打印结果，见上方---------------------------------";
    }

    /**
     * 根据部署id查询对应的部署定义id
     * 一条部署id可以对应多条部署定义，但建议一对一
     */
    public String getProcessDefineInfoByDeployId(String deployId){
        List<ProcessDefinition> processDefs = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployId)
                .list();
        for (ProcessDefinition processDef : processDefs) {
            System.out.println(processDef.toString());
        }
        System.out.println("-----------------------------");
        return "=================================";
    }

    /**
     * 根据流程定义id启动一个流程实例
     * 流程id (long) 和流程key(String 长度255)是唯一的
     * 都可以作为启动实例的入参
     *
     * @return String String
     */
    @GetMapping("/start/{processDefineId}")
    public String startProcess(@PathVariable("processDefineId") String proDefId) {
        //根据流程定义key启动流程
//        runtimeService.startProcessInstanceByKey("myProcessDefinitionKey");
        //根据流程定义id启动流程
        ProcessInstance proInstance = runtimeService.startProcessInstanceById(proDefId);
        //业务id->BusinessKey自己设置,但没有强制要求唯一,索引形式是normal
        return "流程实例主键:" + proInstance.getId()
                + "\n流程实例关联的业务id:" + proInstance.getBusinessKey();
    }


    /**
     * 根据流程实例ID查询当前活动的任务。
     */
    @GetMapping("/task/{processInstanceId}")
    public String queryActiveTask(@PathVariable("processInstanceId") String processInstanceId) {
        TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(processInstanceId);
        List<Task> tasks = taskQuery.list();
        System.out.println(tasks);
        return tasks.toString();
    }

    /**
     * 根据任务id完成任务
     */
    @GetMapping("/task/complete/{taskId}")
    public String completeTaskById(@PathVariable("taskId") String taskId) {
        taskService.complete(taskId);
        return "任务完成";
    }

    /**
     * 根据流程实例id,查询流程实例对象信息
     */
    @GetMapping("/processInstance/{id}")
    public ProcessInstance getAllInfoOfProcessInstance(@PathVariable("id") String id) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().processInstanceId(id);
        ProcessInstance processInstance = query.singleResult();
        System.out.println(processInstance);
        return processInstance;
    }

    /**
     * 为流程实例自定义变量
     */
    @GetMapping("/processInstance/set/{id}/{key}/{value}")
    public String setVariable4ProcessInstance(@PathVariable("id") String id,
                                              @PathVariable("key") String key,
                                              @PathVariable("value") String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        runtimeService.setVariables(id, map);
        return "已为流程" + id + "设置变量" + key + ":" + value;
    }

    /**
     * 查询为流程实例自定义的变量
     */
    @GetMapping("/processInstance/get/{id}/{key}")
    public String getVariable4ProcessInstance(@PathVariable("id") String id, @PathVariable("key") String key) {
        Object variable = runtimeService.getVariable(id, key);
        return "流程" + id + "自定义的变量" + key + "的值为:" + variable;
    }

    /**
     * 根据任务id设置变量
     */
    @GetMapping("/task/set/{taskId}/{key}/{value}")
    public String setVariable4Task(@PathVariable("taskId") String taskId,
                                   @PathVariable("key") String key,
                                   @PathVariable("value") String value) {
        taskService.setVariable(taskId, key, value);
        return "已为任务" + taskId + "设置变量" + key + ":" + value;
    }

    /**
     * 获取根据任务id自定义的变量
     */
    @GetMapping("/task/get/{taskId}/{key}")
    public String getVariable4Task(@PathVariable("taskId") String taskId,
                                   @PathVariable("key") String key) {
        Object variable = taskService.getVariable(taskId, key);
        return "任务" + taskId + "自定义的变量" + key + "的值为:" + variable;
    }

    /**
     * 判断流程实例结束
     */
    @GetMapping("/processInstance/status/{id}")
    public String getProcessInstanceStatus(@PathVariable("id") String id) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().processInstanceId(id);
        HistoricProcessInstance history = query.singleResult();
        if (history != null && history.getEndTime() != null) {
            return history.toString();
        } else {
            return "流程未结束";
        }
    }

    /**
     * 强制删除流程实例
     */
    @GetMapping("/processInstance/delete/{id}")
    public String deleteProcessInstance(@PathVariable("id") String id) {
        runtimeService.deleteProcessInstance(id, "删除原因......");
        return "已删除流程" + id;
    }

}
