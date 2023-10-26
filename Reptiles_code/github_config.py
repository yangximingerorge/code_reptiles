# -*- coding: utf-8 -*-
# description: ini 文件处理

import configparser


class Dispose_ini(object):
    """
    封装一个类，进行ini文件的常用操作
    """

    def __init__(self, filepath):
        self._path = filepath
        self.config = configparser.ConfigParser(allow_no_value=True)  # 实例化解析对象
        self.config.read(filepath, encoding='utf-8')  # 读文件

    def get_sections(self):
        """
        获取ini文件所有的块，返回为list
        """
        sect = self.config.sections()
        return sect

    def get_options(self, sec):
        """
        获取ini文件指定块的项
        :param sec: 传入的块名
        :return: 返回指定块的项（列表形式）
        """
        return self.config.options(sec)

    def get_items(self, sec):
        """
        获取指定section的所有键值对
        :param sec: 传入的块名
        :return: section的所有键值对（元组形式）
        """
        return self.config.items(sec)

    def get_option(self, sec, opt):
        """
        :param sec: 传入的块名
        :param opt: 传入项
        :return: 返回项的值(string类型)
        """
        return self.config.get(sec, opt)


