package org.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.reggie.pojo.AddressBook;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
