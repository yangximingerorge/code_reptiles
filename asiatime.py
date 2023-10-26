# import time, datetime
import pytz

from datetime import datetime, timedelta

start_time = '2023-10-10T02:19:18Z'

# formatted_time = datetime.datetime.strptime(s, "%Y-%m-%dT%H:%M:%SZ")
# print(formatted_time)
# print(type(formatted_time))
# local_tz = pytz.timezone('Asia/Shanghai')
# ss = local_tz.localize(formatted_time).astimezone(pytz.UTC)
# print(ss)
# start_time = "2020-05-08T11:16:51.000Z"
_date = datetime.strptime(start_time, "%Y-%m-%dT%H:%M:%SZ") + timedelta(hours=8)
end_time = _date.strftime("%Y-%m-%d %H:%M:%S")
print(end_time)

#! python3

import requests
from bs4 import BeautifulSoup
from colorama import Fore, Back, init

# process = 0
# output = 0
#
#
# def req(type, addr, data='', **args):
#     if type == 'get':
#         try:
#             responses = requests.get(addr, timeout=60, verify=False)
#         except requests.exceptions.RequestException as e:
#             pass
#     elif type == 'post':
#         try:
#             responses = requests.post(addr, timeout=50)
#         except requests.exceptions.RequestException as e:
#             pass
#     return responses
#
# def access(url_addr):
#     # print("access")
#     for i in url_addr:
#         print(i['git_addr'])
#         responses = req('get', i['git_addr'])
#         if responses.status_code == 200:
#             text = BeautifulSoup(responses.text, "html.parser")
#             all_commits = text.find_all(class_='blob-code blob-code-addition js-file-line')
#             for texts in all_commits:
#                 print(1)
#                 pass
#         else:
#             print(Fore.BLACK + Back.RED + "[ERROR]   " + "%s [status] %s" % (
#                 str(i['git_addr']), str(responses.status_code)))
#             i['code'] = responses.status_code
#             # return
#
# def main():
#     global process
#     url_addr = [
#         {
#             'username': 'yangeroge',
#             'git_addr': 'https://github.com/yangeroge/my-awesome-repo/commit/0a2c12074218d7de4c9e9c1c033d0f0e778c6660',  # 项目地址
#             'start': '',
#             'commins': [],
#         }
#
#     ]
#
#     if input("是否爬取commits细节(Y/N):").upper() == "Y":
#         process = 1
#     else:
#         process = 0
#
#     access(url_addr)
#     print("[OK] 爬行结束 ...")
#     if input("是否关闭当前窗口(Y/N):").upper() == "Y":
#         exit()
#     else:
#         pass
#     exit()
#
#
# if __name__ == "__main__":
#     init(autoreset=True)
#     main()