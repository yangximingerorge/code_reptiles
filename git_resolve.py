#! python3

import requests
from bs4 import BeautifulSoup
from colorama import Fore, Back, init

process = 0
output = 0


def req(type, addr, data='', **args):
    if type == 'get':
        try:
            responses = requests.get(addr, timeout=60, verify=False)
        except requests.exceptions.RequestException as e:
            pass
    elif type == 'post':
        try:
            responses = requests.post(addr, timeout=50)
        except requests.exceptions.RequestException as e:
            pass
    return responses


def access(url_addr):
    # print("access")
    for i in url_addr:
        print(i['git_addr'])
        responses = req('get', i['git_addr'])
        if responses.status_code == 200:
            print("[SUCCESS]" + " %s [status] %s" % (str(i['git_addr']), str(responses.status_code)))
            i['git_addr'] = i['git_addr'] + '/commits/'
            commits(i)
        else:
            print(Fore.BLACK + Back.RED + "[ERROR]   " + "%s [status] %s" % (
            str(i['git_addr']), str(responses.status_code)))
            i['code'] = responses.status_code
            # return


def commits(addr):
    url = addr['git_addr']
    responses = req('get', url)
    if responses.status_code != 200:
        print("[SUCCESS] %s [status] %s" % (str(url), str(responses.status_code)))
        addr['code'] = responses.status_code
        return
    text = BeautifulSoup(responses.text, "html.parser")
    # 判断空仓库

    if "This repository is empty." in text:
        print(print(
            Fore.RED + Back.WHITE + "%s 的仓库内容爬取过程中发现告警[This repository is empty.]" % (addr['username'])))
        return
    # commits_all_dict = []

    all_commits = text.find_all(class_='TimelineItem-body')

    # 展露细节内容的
    try:
        for texts in all_commits:
            date = texts.find(class_='text-normal').get_text()[11:]  # 日期
            # 我们获取的日期格式是标准的英文格式日期"Nov 26, 2020"，所以我们需要进行日期的转换
            # date = datetime.datetime.strptime(dateBar, '%b %d, %Y').strftime('%Y年%m月%d日')
            commits_second = 0
            if process:
                print("\n=================[%s]=================" % (str(date)))
            all_commits_find = texts.ol.find_all('li')
            for commits_find in all_commits_find:
                commits_dict = {
                    'commits_auth': commits_find.div.find('div', class_='d-flex').find('div', class_='f6').find(
                        class_='commit-author').get_text(),
                    'commits_time': commits_find.find('relative-time')['datetime'],  # 当前日期所提交的内容
                    'commits_href': "https://github.com" + commits_find.div.p.a['href'],
                    # 我们的text中式把summary和description内容融合在一起的于是我们需要把他们分开
                    'commits_summary': commits_find.find('clipboard-copy')['aria-label'],
                    # 'commits_summary': commits_find.div.p.a['aria-label'][:len(commits_find.div.p.a.get_text())],
                    # 'commits_description': commits_find.div.p.a['aria-label'][
                    #                        len(commits_find.div.p.a.get_text()):].strip()
                }
                # commits_all_dict.append(commits_dict)
                commits_second += 1
                # 处理爬取数据的输出
                if process:
                    print("\n-----------------[%s]-----------------" % (commits_dict['commits_auth']))
                    print("[提交时间] %s \n[提交代码] %s\n[提交主题] %s\n[提交描述] %s"
                          % (commits_dict['commits_time'], commits_dict['commits_href'],
                             commits_dict['commits_summary'], commits_dict['commits_description']))
        print(Fore.BLACK + Back.WHITE + "%s 于 %s 共计提交了 %s 次代码" % (addr['username'], date, commits_second))

        # 处理分页爬取
        next_a = text.find(class_='paginate-container').find_all('a')
        if len(next_a) and next_a[-1].get_text() == 'Older':
            print("------next page------")
            addr['git_addr'] = next_a[-1]['href']
            commits(addr)
    except Exception as e:
        print(print(Fore.RED + Back.WHITE + "%s 的仓库爬取过程中发生错误." % (addr['username'])))
        return


def main():
    global process
    url_addr = [
        {
            'username': 'yangeroge',
            'git_addr': 'https://github.com/yangeroge/my-awesome-repo',  # 项目地址
            'start': '',
            'commins': [],
        }

    ]
    if input("是否爬取commits细节(Y/N):").upper() == "Y":
        process = 1
    else:
        process = 0

    access(url_addr)
    print("[OK] 爬行结束 ...")
    if input("是否关闭当前窗口(Y/N):").upper() == "Y":
        exit()
    else:
        pass
    exit()


if __name__ == "__main__":
    init(autoreset=True)
    main()