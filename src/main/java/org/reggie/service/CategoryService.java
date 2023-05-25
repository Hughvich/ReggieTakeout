package org.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.reggie.pojo.Category;


public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
