package org.example.service;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName CandidateServiceTest
 * @Description
 * @Date 2024/02/20 09:37:00 星期二
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CandidateServiceTest2 extends TestCase {

    @Resource
    CandidateService candidateService;

    @Test
    public void fullProcess(){
        String deployId = candidateService.deploy("candidateTest2");
        String defId = candidateService.getProDefByDeployId(deployId);
        String instanceId = candidateService.startProcess(defId);
        candidateService.queryActiveTask(instanceId);
    }


    /**
     * 根据文件名部署
     */
    @Test
    public void testDeploy() {
        candidateService.deploy("candidateTest2");
    }

    /**
     * 根据部署名查询
     */
    @Test
    public void testQueryDeployByName() {
        candidateService.queryDeployByName("candidateTest2");
    }

    /**
     * 根据部署id查询流程定义
     */
    @Test
    public void testGetProDefByDeployId() {
        candidateService.getProDefByDeployId("ceeedfa7-cf92-11ee-9c43-10f60a992eb5");
    }

    /**
     * 根据流程定义id启动流程
     */
    @Test
    public void testStartProcess() {
        candidateService.startProcess("candidateTest1:2:cef8f1c9-cf92-11ee-9c43-10f60a992eb5");
    }

    /**
     * 根据流程实例id查询待办任务
     */
    @Test
    public void testQueryActiveTask() {
        candidateService.queryActiveTask("f56b513d-cf9d-11ee-92d4-10f60a992eb5");
    }

    /**
     * 根据任务id完成任务
     */
    @Test
    public void testCompleteTaskById() {
        // 完成任务
        candidateService.completeTaskById("7f6b618b-cf9e-11ee-84c8-10f60a992eb5");
        // 查询待办任务
        candidateService.queryActiveTask("7f698cc7-cf9e-11ee-84c8-10f60a992eb5");
    }

    /**
     * 启动流程后,为流程实例设置变量
     */
    @Test
    public void testSetVariable4ProcessInstance() {
        String instanceId = "7f698cc7-cf9e-11ee-84c8-10f60a992eb5";
        Map<String,String> map = new HashMap<>();
        map.put("salary","88");
        candidateService.setVariable4ProcessInstance(instanceId,map);
        candidateService.getVariable4ProcessInstance(instanceId,"salary");
    }

    /**
     * 获取指定流程实例的,指定key的value
     */
    @Test
    public void testGetVariable4ProcessInstance() {
        candidateService.getVariable4ProcessInstance("372ce479-cee7-11ee-9921-10f60a992eb5","salary");
    }


    @Test
    public void testGetProcessInstanceStatus() {
        candidateService.getProcessInstanceStatus("9df3178e-cee9-11ee-bfa2-10f60a992eb5");
    }

    @Test
    public void testDeleteProcessInstance() {
    }

    @Test
    public void queryTasksByCandidate(){
        candidateService.queryTasksByCandidate("lisi");
    }

    @Test
    public void queryTasksByCandidateList(){
        candidateService.queryTasksByCandidateList("lisi");
    }
}
/*
任务完成
[Task[id=f44f175f-cf93-11ee-8376-10f60a992eb5, name=第一分支,张三,大于100]]




*/
