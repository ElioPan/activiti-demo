package org.example.service;

import junit.framework.TestCase;
import org.activiti.engine.delegate.event.ActivitiVariableEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.rmi.activation.ActivationID;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ActivitiServiceTest
 * @Description
 * @Date 2024/02/19 13:21:00 星期一
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ActivitiServiceTest extends TestCase {
    @Resource
    ActivitiService activitiService;

    @Test
    public void testDeploy() {
        activitiService.deploy("simple-test-2");
    }

    @Test
    public void testQueryDeployByName() {
        activitiService.queryDeployByName("simple-test-2");
    }

    @Test
    public void testGetProDefByDeployId() {
        activitiService.getProDefByDeployId("efdfb79d-cee6-11ee-89cc-10f60a992eb5");
    }

    @Test
    public void testStartProcess() {
        activitiService.startProcess("simple-test:2:efede86f-cee6-11ee-89cc-10f60a992eb5");
    }

    @Test
    public void testQueryActiveTask() {
        activitiService.queryActiveTask("9df3178e-cee9-11ee-bfa2-10f60a992eb5");
    }

    @Test
    public void testCompleteTaskById() {
        activitiService.completeTaskById("c7ef627b-cee9-11ee-90b3-10f60a992eb5");
        activitiService.queryActiveTask("9df3178e-cee9-11ee-bfa2-10f60a992eb5");
    }

    @Test
    public void testGetAllInfoOfProcessInstance() {
    }

    @Test
    public void testSetVariable4ProcessInstance() {
        Map<String,String> map = new HashMap<>();
        map.put("salary","50");
        activitiService.setVariable4ProcessInstance("9df3178e-cee9-11ee-bfa2-10f60a992eb5",map);
    }

    @Test
    public void testGetVariable4ProcessInstance() {
        activitiService.getVariable4ProcessInstance("372ce479-cee7-11ee-9921-10f60a992eb5","salary");
    }

    @Test
    public void testSetVariable4Task() {
    }

    @Test
    public void testGetVariable4Task() {
    }

    @Test
    public void testGetProcessInstanceStatus() {
        activitiService.getProcessInstanceStatus("9df3178e-cee9-11ee-bfa2-10f60a992eb5");
    }

    @Test
    public void testDeleteProcessInstance() {
    }
}