package club.xiaozhe.shinycloud.util;

import club.xiaozhe.shinycloud.common.result.PageData;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.function.Function;

/**
 * 分页转换器，将 MP 的 IPage 对象转换为项目中的统一返回体 PageData
 */
public final class PageConverter {
    private PageConverter() {
    }

    /**
     * 直接打包为 PageData 对象
     *
     * @param page IPage
     * @param <T>  分页中的数据类型
     * @return PageData
     */
    public static <T> PageData<T> from(IPage<T> page) {
        return new PageData<>(
                page.getRecords(),
                page.getTotal(),
                page.getSize(),
                page.getCurrent(),
                page.getPages()
        );
    }

    /**
     * 将分页中的对象转换成其他类型后打包为 PageData 对象
     *
     * @param page   IPage
     * @param mapper 转换逻辑 （Entity -> VO）
     * @param <S>    转换前的数据类型
     * @param <T>    转换后的数据类型
     * @return PageData
     */
    public static <S, T> PageData<T> from(IPage<S> page, Function<S, T> mapper) {
        List<T> records = page.getRecords().stream().map(mapper).toList();
        return new PageData<>(
                records,
                page.getTotal(),
                page.getSize(),
                page.getCurrent(),
                page.getPages()
        );
    }
}
