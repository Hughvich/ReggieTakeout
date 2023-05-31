package org.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.BaseContext;
import org.reggie.common.R;
import org.reggie.pojo.AddressBook;
import org.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户收货地址簿管理
 *
 */

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址簿
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        // 设置当前用户的userid, 用到BaseContext.getCurrentId()
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("用户收货地址簿：" + addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 把其中一个地址设置成默认地址，用到LambdaUpdateWrapper
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("设置默认地址：" + addressBook);
        // 构造一个LambdaUpdateWrapper，
        // update address_book set is_default = 0 where user_id = ?
        // 当前用户userid下所有地址is_default全改为0
        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.set(AddressBook::getIsDefault, 0);

        addressBookService.update(queryWrapper);

        // 当前要改的地址is_default值设为1
        addressBook.setIsDefault(1);
        // update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }

    /**
     * 根据id查地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) return R.success(addressBook);
         else return R.error("没有找到该地址");
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);

        // select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if(addressBook == null) return R.error("没有找到该地址");
        else return R.success(addressBook);
    }

    /**
     * 查询/列出所有地址
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("查询全部地址：" + addressBook);

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        // select * from address_book where user_id = ? order by update_time desc
        List<AddressBook> list = addressBookService.list(queryWrapper);

        return R.success(list);
    }
}
