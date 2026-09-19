package club.xiaozhe.shinycloud.common.result;

import java.util.List;

/**
 * 分页统一返回体
 *
 * @param records 载荷
 * @param total   数据总数
 * @param size    每页条数
 * @param current 当前页
 * @param pages   总页数
 * @param <T>     载荷类型
 */
public record PageData<T>(
        List<T> records,
        long total,
        long size,
        long current,
        long pages
) {
}
