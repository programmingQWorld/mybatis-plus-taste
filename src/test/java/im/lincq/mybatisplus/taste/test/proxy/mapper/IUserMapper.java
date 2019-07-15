package im.lincq.mybatisplus.taste.test.proxy.mapper;

public interface IUserMapper {
    User selectById(Long id);
    int deleteById(Long id);
}
