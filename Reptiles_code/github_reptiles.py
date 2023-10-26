import os
import requests
import urllib3
import logging
import logging.handlers
import logging.config
import sys
from sshpubkeys import SSHKey
from bs4 import BeautifulSoup
from colorama import Fore, Back, init
from github_config import Dispose_ini

from datetime import datetime, timedelta


class REPRILE(object):

    def __init__(self):
        self.github_warehouse_path = '{}/{}/{}'.format(dis.get_option('url', 'url'), dis.get_option('username',
                                                                                                    'username'),
                                                       dis.get_option(
                                                           'code_warehouse', 'warehouse'))
        self.github_warehouse_commits_path = '{}/{}'.format(self.github_warehouse_path, 'commits/')
        self.ssh_key = dis.get_option('ssh_key', 'ssh_key')


        # 加载日志配置
        self.logger_path = os.getcwd()
        self.logger_config_path = os.path.abspath(os.path.join(self.logger_path, '..')) + '\\logging.conf'

        logging.config.fileConfig(self.logger_config_path)

        # 初始化日志
        self.logger = logging.getLogger('fileAndConsole')
        self.code_dist = self.code_warehouse_commits()

    def ssh_key_authentication(self):
        ssh = SSHKey(self.ssh_key, strict_mode=True)
        try:
            ssh.parse()
            self.logger.error('ssh_key认证成功')
        except Exception as err:
            self.logger.error('ssh_key认证失败')
            sys.exit(1)
        except NotImplementedError as err:
            self.logger.error('ssh_key认证失败')
            sys.exit(1)

    def req(self, type, addr, data='', **args):
        self.ssh_key_authentication()  # ssh_key 认证登录
        urllib3.disable_warnings()  # 报警接触

        if type == 'get':
            try:
                responses = requests.get(addr, timeout=120, verify=False)
                if responses.status_code == 200:
                    self.logger.info('连接成功，网址为%s' % addr)
                    return responses
                else:
                    self.logger.error('连接失败，网址为%s，状态码为%s' % (addr, responses.status_code))
            except requests.exceptions.RequestException as e:
                self.logger.error('连接失败,网址为%s' % addr)
                self.logger.error('连接方式为%s,异常信息为%s' % (type, e))
        elif type == 'post':
            try:
                responses = requests.post(addr, timeout=60, verify=False)
                if responses.status_code == 200:
                    self.logger.info('连接成功，网址为%s' % addr)
                    return responses
                else:
                    self.logger.error('连接失败，网址为%s，状态码为%s' % (addr, responses.status_code))
            except requests.exceptions.RequestException as e:
                self.logger.error('连接失败,网址为%s' % addr)
                self.logger.error('连接方式为%s,异常信息为%s' % (type, e))

    def code_warehouse_commits(self):
        # 代码仓库中的提交记录信息 以及获取提交记录的时间,作者,提交记录的链接
        responses = self.req(type='get', addr=self.github_warehouse_path)
        commits_response = self.req(type='get', addr=self.github_warehouse_commits_path)
        try:
            text = BeautifulSoup(commits_response.text, "html.parser")
            commits_dist = []
            if "This repository is empty" in text:
                self.logger.error('该仓库的提交记录为空，描述为%This repository is empty')
            all_commits = text.find_all(class_='TimelineItem-body')
            for texts in all_commits:
                all_commits_find = texts.ol.find_all('li')
                for commits_find in all_commits_find:
                    end_time = datetime.strptime(commits_find.find('relative-time')['datetime'],
                                                 "%Y-%m-%dT%H:%M:%SZ") + timedelta(hours=8)
                    commits_dict = {
                        'commits_auth': commits_find.div.find('div', class_='d-flex').find('div', class_='f6').find(
                            class_='commit-author').get_text(),
                        'commits_time': end_time.strftime("%Y-%m-%d %H:%M:%S"),
                        'commits_href': "https://github.com" + commits_find.div.p.a['href'],
                        'commit_summary': commits_find.div.find('p', class_='mb-1').get_text(),
                    }
                    commits_dist.append(commits_dict)
            self.logger.error('提交记录构成为%s' % commits_dist)
            return commits_dist
            # 处理分页爬取
            # next_a = text.find(class_='paginate-container').find_all('a')
            # if len(next_a) and next_a[-1].get_text() == 'Older':
            # addr['git_addr'] = next_a[-1]['href']
            #     commits(addr)
        except Exception as e:
            self.logger.error('获取提交记录失败失败原因为%s' % e)

    def code_warehouse_herf(self):
        for commits_information in self.code_dist:
            if commits_information.get('commits_href'):
                print(commits_information.get('commits_href'))


if __name__ == "__main__":
    init(autoreset=True)
    dis = Dispose_ini('../github_config.ini')
    code_reptiles = REPRILE()
    code_reptiles.code_warehouse_herf()
