package org.example.service;

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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ActivitiService
 * @Description
 * @Date 2024/02/18 15:45:00 星期日
 * @Version 1.0
 */
@Service
public class CandidateService {
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
    public String deploy(String filename) {
        Deployment deploy = null;
        try {
            // module内文件相对路径，从module目录下开始
            FileInputStream fis = new FileInputStream("src/main/resources/prodef/" + filename + ".bpmn20.xml");
            deploy = repositoryService.createDeployment()
                    .name(filename)  // 流程的名称NAME_字段
                    .addInputStream(filename + ".bpmn20.xml", fis)  // 流程定义中的名称RESOURCE_NAME_字段
                    .deploy();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String info = "部署成功,部署的id是:" + deploy.getId() + ";\n部署的名称是:" + deploy.getName();
        System.out.println(info);
        return deploy.getId();
    }

    /**
     * 根据部署名称查询部署信息列表
     * 部署名称是可以重复的
     *
     * @param name name
     * @return Object Object
     */
    public void queryDeployByName(String name) {
        //根据名称获取流程部署id
        DeploymentQuery deployQuery = repositoryService.createDeploymentQuery();
        List<Deployment> deployments = deployQuery.deploymentName(name).list();
        if (deployments.size() == 0) {
            System.out.println("没有查询到相关部署信息");
            return;
        }
        for (Deployment deployment : deployments) {
            String string = deployment.toString();
            System.out.println(string);
        }
        System.out.println("直接toString打印结果，见上方---------------------------------");
    }

    /**
     * 根据部署id查询对应的部署定义id
     * 一条部署id可以对应多条部署定义，但建议一对一
     */
    public String getProDefByDeployId(String deployId){
        List<ProcessDefinition> processDefs = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployId)
                .list();
        for (ProcessDefinition processDef : processDefs) {
            System.out.println(processDef.toString());
        }
        System.out.println("=================================");
        return processDefs.get(0).getId();
    }

    /**
     * 根据流程定义id启动一个流程实例
     * 流程id (long) 和流程key(String 长度255)是唯一的
     * 都可以作为启动实例的入参
     *
     * @return String String
     */
    public String startProcess(String proDefId) {
        //根据流程定义key启动流程
//        runtimeService.startProcessInstanceByKey("myProcessDefinitionKey");
        //根据流程定义id启动流程
        ProcessInstance proInstance = runtimeService.startProcessInstanceById(proDefId);
        //业务id->BusinessKey自己设置,但没有强制要求唯一,索引形式是normal
        String info = "流程实例主键:" + proInstance.getId()
                + "\n流程实例关联的业务id:" + proInstance.getBusinessKey();
        System.out.println(info);
        return proInstance.getId();
    }


    /**
     * 根据流程实例ID查询当前活动的任务。
     */
    public void queryActiveTask(String processInstanceId) {
        TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(processInstanceId);
        List<Task> tasks = taskQuery.list();
        System.out.println(tasks);
    }

    /**
     * 根据任务id完成任务
     */
    @GetMapping("/task/complete/{taskId}")
    public void completeTaskById(@PathVariable("taskId") String taskId) {
        taskService.complete(taskId);
        System.out.println("任务完成");
    }

    /**
     * 根据流程实例id,查询流程实例对象信息
     */
    public void getAllInfoOfProcessInstance(String proInstanceId) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().processInstanceId(proInstanceId);
        ProcessInstance processInstance = query.singleResult();
        System.out.println(processInstance);
    }

    /**
     * 为流程实例自定义变量
     */
    public void setVariable4ProcessInstance(String proInstanceId,
                                               Map<String,String> map) {
        runtimeService.setVariables(proInstanceId, map);
    }

    /**
     * 查询为流程实例自定义的变量
     */
    public void getVariable4ProcessInstance(String id,String key) {
        Object variable = runtimeService.getVariable(id, key);
        String info = "流程" + id + "自定义的变量" + key + "的值为:" + variable;
        System.out.println(info);
    }

    /**
     * 根据任务id设置变量
     */
    public void setVariable4Task( String taskId,
                                    String key,
                                   String value) {
        taskService.setVariable(taskId, key, value);
        String info = "已为任务" + taskId + "设置变量" + key + ":" + value;
        System.out.println(info);
    }

    /**
     * 获取根据任务id自定义的变量
     */
    public void getVariable4Task( String taskId, String key) {
        Object variable = taskService.getVariable(taskId, key);
        String info = "任务" + taskId + "自定义的变量" + key + "的值为:" + variable;
        System.out.println(info);
    }

    /**
     * 判断流程实例结束
     */
    public void getProcessInstanceStatus(String id) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().processInstanceId(id);
        HistoricProcessInstance history = query.singleResult();
        if (history != null && history.getEndTime() != null) {
            System.out.println("流程已结束："+history);
        } else {
            System.out.println("流程未结束");
        }
    }

    /**
     * 强制删除流程实例
     */
    public void deleteProcessInstance(String id) {
        runtimeService.deleteProcessInstance(id, "删除原因......");
        String info = "已删除流程" + id;
        System.out.println(info);
    }

    /**
     * 根据候选人查询任务-候选人是预设的
     */
    public void queryTasksByCandidate(String candidate){
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(candidate)
                .list();
        for (Task task : tasks) {
            System.out.println(task);
            System.out.println("=====================================");
        }
    }

}
