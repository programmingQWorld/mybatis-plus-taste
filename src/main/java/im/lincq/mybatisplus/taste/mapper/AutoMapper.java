package im.lincq.mybatisplus.taste.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * <p>
 *     Mapper继承该接口后，无需编写mapper.xml 文件.,即可获取CRUD功能.
 * </p>
 * <p>
 *     java.lang.Long 类型 ID 主键
 * </p>
 * @Date 20190121
 */
public interface AutoMapper<T> extends BaseMapper<T, Long> {
}
