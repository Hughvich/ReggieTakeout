package org.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggie.mapper.AddressBookMapper;
import org.reggie.pojo.AddressBook;
import org.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
