from javalang import parse
from javalang.tree import ClassDeclaration, MethodDeclaration, MethodInvocation,TypeDeclaration

if __name__ == '__main__':

    file_path = "AttendanceGroupServiceImpl.java"

    with open(file_path, "r", encoding="utf-8") as f:
        file_content = f.read()

    tree = parse.parse(file_content)

    for path, class_node in tree:
        if isinstance(class_node, ClassDeclaration):
            print("class_name" + class_node.name)
            for path, method_node in class_node:
                method_list = []
                if isinstance(method_node, MethodDeclaration):
                    print("method_name" + method_node.name)
                    # 找方法调用
                    for path, node in method_node:
                        node_list = []
                        if isinstance(node, MethodInvocation):
                            print("-----" + node.member)

# result = [(class_node.name, method_node.name) for path, class_node in tree if isinstance(class_node,ClassDeclaration), for path, method_node in class_node if isinstance(method_node,MethodDeclaration) ]
