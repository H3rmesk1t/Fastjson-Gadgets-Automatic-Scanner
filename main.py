#!/usr/bin/env python
import os.path
import argparse
import subprocess
from javalang.tree import *
from javalang.parse import parse

'''
jar 获取
jar 反编译
ast 语法树生成
ast 语法树分析获取Gadgets
返回结果
'''

def command():

    # Generate the logo and print it on terminal.
    logo_file = open(r'logo.txt')
    logo_file = logo_file.read()
    print('\033[1;36m\t' + logo_file + '\033[0m')

    # Use the command to obtain the jar packages in the terminal.
    parser = argparse.ArgumentParser(prog='python main.py')
    parser.add_argument('jar', help='Enter the jar to be scanned', type=str)
    parser.add_argument('operating_system', help='Enter the operating system Windows or Linux or MacOS', type=str)
    args = parser.parse_args()
    return args.jar, args.operating_system


def jar_process(jar, operating_system):

    # Generate the jar and create appropriate folders.
    if operating_system == 'Windows':
        jar_name = jar.split('\\')[-1].split('.')[0]
    else:
        jar_name = jar.split('/')[-1].split('.')[0]

    jar_dir = './project/' + jar_name
    jar_file_dir = './project/' + jar_name + '/file'
    subprocess.run(['mkdir', jar_dir])
    subprocess.run(['mkdir', jar_file_dir])
    return jar_dir, jar_file_dir, jar_name


def jar_decompile(jar, jar_dir, jar_file_dir, jar_name):

    # Decompile jar package.
    subprocess.run(['java', '-cp', './java-decompiler.jar', 'org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler', jar, jar_dir])
    ret = subprocess.run(['unzip', '-d', jar_file_dir, jar_dir + '/' + jar_name + '.jar'])
    if ret.returncode == 0:
        print('\033[1;31m[+] jar package decompile successfully.\033[0m')


def javafile_generate(content):

    # Generate java file list from jar package that had been decompiled.
    if os.path.exists(content) is False:
        return []

    content_path = os.path.abspath(content)
    files = os.listdir(content_path)
    java_file = []
    for file in files:
        _file = os.path.join(content_path, file)
        if os.path.isfile(_file):
            if file.endswith('.java'):
                java_file.append(_file)
        else:
            java_file.extend(javafile_generate(_file))
    return java_file


def blacklist_generate():

    # Generate blacklist.
    blacklist_file = open(r'./blacklist.txt', 'r')
    blacklist = blacklist_file.read().splitlines()
    blacklist_file.close()
    return blacklist


def class_declaration_generate(root_tree):

    # Filter eligible classes.
    class_list = []
    # Cannot implement DataSource and RowSet interfaces.
    black_interface = ('DataSource', 'RowSet')
    for node in root_tree.types:
        # Class declarations are not parsed.
        if isinstance(node, ClassDeclaration) is False:
            continue

        # Cannot inherit Classloader class.
        if node.extends is not None and node.extends.name == 'ClassLoader':
            continue

        # Determine whether the interface is in the blacklist.
        interface_flag = False
        if node.implements is None:
            node.implements = []
        for implement in node.implements:
            if implement.name in black_interface:
                interface_flag = True
                break
        if interface_flag is True:
            continue

        # Check if there is a constructor with no parameters.
        constructor_flag = False
        for constructor_declaration in node.constructors:
            if len(constructor_declaration.parameters) == 0:
                constructor_flag = True
                break
        if constructor_flag is False:
            continue

        class_list.append(node)
    return class_list


def lookup_detect(method_declaration):

    # Final check for each method declaration.
    available_variables = []
    for path, node in method_declaration:
        # Determine whether to call the lookup method.
        if isinstance(node, MethodInvocation) and node.member == 'lookup':
            # Determine if there is only one parameter.
            if len(node.arguments) != 1:
                continue
            # Determine whether a parameter is a controllable variable.
            argument = node.arguments[0]
            if isinstance(argument, Cast):
                available_variables.append(argument.expression.member)
            if isinstance(argument, MemberReference):
                available_variables.append(argument.member)
            if isinstance(argument, This):
                return True
    if len(available_variables) == 0:
        return False

    # Determine whether the parameters of the lookup come from the method, because only the input parameters are considered controllable.
    for parament in method_declaration.paraments:
        parament_name = parament.name
        if parament_name in available_variables:
            return True
    return False
    pass


def scanner_dir(jar_name, jar_file_dir):

    # Traverse the decompiled directory to find the Java file.
    global java_file_list
    contents = os.listdir(jar_file_dir)
    for content in contents:
        file = os.path.join(jar_file_dir, content)
        if os.path.isfile(file):
            continue
        java_file_list = javafile_generate(file)
        'A obtains a total of x Java files'
    print('\033[1;31m[+] ' + jar_name + ' jar obtains a total of ' + str(len(java_file_list)) + ' Java files.\033[0m')
    for java_file in java_file_list:
        scanner_file(java_file)


def scanner_file(java_file):

    # Scan and identify Java file.
    file = open(java_file, 'r')
    file_content = file.read()
    file.close()

    if 'InitialContext(' not in file_content:
        return False
    try:
        root_tree = parse(file_content)
    except:
        return False

    class_declaration_list = class_declaration_generate(root_tree)
    print('[+] class_declaration_list')
    print(class_declaration_list)
    for class_declaration in class_declaration_list:
        for method_declaration in class_declaration.methods:
            if lookup_detect(method_declaration) is True:
                result = f'{java_file} {method_declaration.name}'
                print(result)
            else:
                print('qaq')


def main():
    blacklist = blacklist_generate()
    jar, operating_system = command()
    jar_dir, jar_file_dir, jar_name = jar_process(jar, operating_system)
    jar_decompile(jar, jar_dir, jar_file_dir, jar_name)
    scanner_dir(jar_name, jar_file_dir)


if __name__ == '__main__':
    main()