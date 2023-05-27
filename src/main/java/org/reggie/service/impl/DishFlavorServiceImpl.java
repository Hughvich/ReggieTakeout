package org.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.reggie.dto.DishDto;
import org.reggie.mapper.DishFlavorMapper;
import org.reggie.pojo.DishFlavor;
import org.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService {


}
