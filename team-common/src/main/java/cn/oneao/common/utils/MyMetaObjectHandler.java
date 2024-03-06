package cn.oneao.common.utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 获取元数据中 名为 gmtCreate 字段的值
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("createBy", SecurityUtils.getUsername(), metaObject);
        this.setFieldValByName("isDelete", 0, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", SecurityUtils.getUsername(), metaObject);
    }
}