package org.example.bo;

import lombok.Data;

/**
 * @ClassName VarParam
 * @Description
 * @Date 2024/12/04 13:26:00 星期三
 * @Version 1.0
 */
@Data
public class VarParam {
    private String processInstanceId;
    private String taskId;
    private String key;
    private String value;
}
