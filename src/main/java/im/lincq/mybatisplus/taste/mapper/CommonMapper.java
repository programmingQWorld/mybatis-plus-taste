package im.lincq.mybatisplus.taste.mapper;

/**
 * <p>Mapper 继承该接口后，无需编写 mapper.xml 文件，即可获得CRUD功能</p>
 *
 * <p>java.lang.String 类型 ID 主键</p>
 *
 * @author lincq
 * @date 2019/6/23 22:55
 */
public interface CommonMapper<T> extends BaseMapper<T, String> {
}
