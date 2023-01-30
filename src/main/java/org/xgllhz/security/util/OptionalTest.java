package org.xgllhz.security.util;

import org.xgllhz.security.entity.BaseEntity;
import org.xgllhz.security.entity.PermEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author: XGLLHZ
 * @date: 2022/9/21 10:26
 * @description: Optional test
 */
public class OptionalTest {

    public static int size(PermEntity entity) {
        Optional.ofNullable(entity)
                .ifPresent(permEntity -> {
                    // do something
                });

        return Optional.ofNullable(entity)
                .map(PermEntity::getChildrenList)
                .map(List::size)
                .orElseThrow(() -> new RuntimeException("a"));
    }

    public static BaseEntity test(BaseEntity entity) {
        return Optional.ofNullable(entity)
                .filter(baseEntity -> baseEntity.getId().equals(1L))
                .orElseGet(() -> BaseEntity.builder()
                        .id(1L)
                        .build());
    }

    public static void main(String[] args) {
        BaseEntity baseEntity = BaseEntity.builder().id(1L).build();
        BaseEntity entity = Optional.ofNullable(baseEntity).orElse(new BaseEntity());
        Long id = Optional.ofNullable(baseEntity).get().getId();
        System.out.println(id);
    }

}
