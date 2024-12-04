package org.example.bo;

import lombok.Data;

/**
 * @ClassName Param
 * @Description
 * @Date 2024/12/04 13:24:00 星期三
 * @Version 1.0
 */
@Data
public class ProcessParam {
    private String filename;
    private String deployName;
    private String deployId;
    private String processDefineId;
    private String processInstanceId;
    private String taskId;
}
