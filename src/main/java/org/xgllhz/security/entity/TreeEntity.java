package org.xgllhz.security.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author: XGLLHZ
 * @date: 2022/11/11 10:58
 * @description: tree entity
 */
@Data
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TreeEntity extends BaseEntity {

    private static final long serialVersionUID = -4391300900978458532L;

    private Long parentId;   // 父 id

    @TableField(exist = false)
    private List<? extends TreeEntity> childrenList;   // 子列表

}
