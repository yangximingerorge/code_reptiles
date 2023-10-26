# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.


def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    import requests
    from bs4 import BeautifulSoup

    r1 = requests.get('https://github.com/login')
    soup = BeautifulSoup(r1.text, features='lxml')  # 生成soup 对象
    s1 = soup.find(name='input', attrs={'name': 'authenticity_token'}).get('value')
    # 查到我们要的token
    r1_COOKIEs = r1.COOKIEs.get_dict()  # 下次提交用户名时用的COOKIE
    print(r1_COOKIEs)
    print(s1)

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
