package org.example.controller;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.sql.SQLOutput;

/**
 * @ClassName ActivitiControllerTest
 * @Description
 * @Date 2024/02/18 14:08:00 星期日
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ActivitiControllerTest extends TestCase {
    @Resource
    ActivitiController activitiController;

    /**
     * 用bpm的xml文件部署流程
     */
    @Test
    public void deploy(){
        activitiController.deploy("simple-test");
    }
    /**
     * 用部署名称查询部署信息列表
     */
    @Test
    public void queryDeployByName(){
        String s = activitiController.queryDeploy("simple-test");
        System.out.println(s);
    }

    /**
     * 用queryDeployByName查出的部署信息中的部署id，查询部署定义
     */
    @Test
    public void getProcessDefineInfoByDeployId(){
        activitiController.getProcessDefineInfoByDeployId("c83205a3-ce24-11ee-9640-10f60a992eb5");
    }

    /**
     * 根据查出的流程定义id（也有用key启动的方法）启动一个流程实例
     */
    @Test
    public void startProcess(){
        String info = activitiController.startProcess("simple-test:1:c842f595-ce24-11ee-9640-10f60a992eb5");
        System.out.println(info);

    }

    /**
     * 流程实例当前需要完成的任务
     */
    @Test
    public void queryActiveTask(){
        activitiController.queryActiveTask("02862d3f-ce2a-11ee-acac-10f60a992eb5");
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask(){
        activitiController.completeTaskById("d7b70d04-ce2a-11ee-a95e-10f60a992eb5");
        activitiController.queryActiveTask("02862d3f-ce2a-11ee-acac-10f60a992eb5");
    }


}