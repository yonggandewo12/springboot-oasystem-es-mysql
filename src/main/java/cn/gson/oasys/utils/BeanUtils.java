package cn.gson.oasys.utils;

import lombok.Data;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

/**
 * @Description
 * @ClassName BeanUtils
 * @Author xuliang
 * @date 2020.04.30 14:28
 */
@Data
public class BeanUtils {

    private static Mapper mapper = new DozerBeanMapper();

    public static Object transfer(Object source, Object target) {
        if (source == null || target == null) {
            return null;
        }
        mapper.map(source, target);
        return target;
    }
}
