package org.example.controller;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MyTestController
 * @Description
 * @Date 2023/08/14 13:02:00 星期一
 * @Version 1.0
 */
@RestController
public class MyTestController {
    @Resource
    RepositoryService repositoryService;
    @Resource
    RuntimeService runtimeService;

    /**
     * 根据bpmn图部署流程
     * @param filename filename
     * @return String String
     */
    @GetMapping("/deploy/{filename}")
    public String deploy(@PathVariable String filename) {
        Deployment deploy = null;
        try {
            //写死了,从资源路径下的bpm读取文件流,文件名是动态的
            FileInputStream fis = new FileInputStream("C:/workspace-test/测试/activitiTest/activiti7Demo/src/main/resources/bpm/" + filename + ".bpmn20.xml");
            deploy = repositoryService.createDeployment()
                    .name(filename)
                    .addInputStream(filename + ".bpmn20.xml", fis)
                    .deploy();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return "部署成功,部署的id是:" + deploy.getId() + ";\n部署的名称是:" + deploy.getName();
    }

    /**
     * 根据部署信息查询部署id
     * 再根据部署id查询流程定义的id
     * 部署名称是可以重复的
     * @param name name
     * @return Object Object
     */
    @GetMapping("/deploy/query/{deployName}")
    public Object queryDeploy(@PathVariable("deployName") String name){
        //根据名称获取流程部署id
        DeploymentQuery deployQuery = repositoryService.createDeploymentQuery();
        List<Deployment> deployments = deployQuery.deploymentName(name).list();
        if(deployments.size() == 0){
            return new Object();
        }
        //根据部署id获取流程定义id
        List<String> processDefinitionIds = new ArrayList<>();
        for (Deployment deployment : deployments) {
            //根据部署id获取流程定义id
            List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                    .deploymentId(deployment.getId())
                    .list();
            if (definitions.size()>0) {
                //一个部署id可以对应多个流程定义id
                for (ProcessDefinition definition : definitions) {
                    processDefinitionIds.add(definition.getId());
                }
            }
        }
        return processDefinitionIds;
    }

    /**
     * 根据流程定义id启动一个流程实例
     * 流程id (long) 和流程key(String 长度255)是唯一的
     * 都可以作为启动实例的入参
     * @return String String
     */
    @GetMapping("/start/{processDefineId}")
    public String startProcess(@PathVariable("processDefineId")String proDefId){
        //根据流程定义key启动流程
//        runtimeService.startProcessInstanceByKey("myProcessDefinitionKey");
        //根据流程定义id启动流程
        ProcessInstance proInstance = runtimeService.startProcessInstanceById(proDefId);
        //业务id->BusinessKey自己设置,但没有强制要求唯一,索引形式是normal
        return "流程实例主键:"+proInstance.getId()
                +"\n流程实例关联的业务id:" + proInstance.getBusinessKey();
    }



}
