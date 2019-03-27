package com.steven.selectimage.widget.recyclerview;

/**
 * Created by zhiwenyan on 5/25/17.
 * 多布局的支持
 */

public interface MultiTypeSupport<T> {
    int getLayoutId(T item);
}
